package live.gunnablescum.villagerpickup.configuration;

import com.google.gson.*;
import live.gunnablescum.villagerpickup.Constants;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Configuration {

    Map<ConfigurationElement, Setting> settings;
    private final Gson gson;
    private final File file;

    public Configuration(File file) {
        this.file = file;
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        this.settings = new HashMap<>();
        loadConfigValues();
    }

    public boolean loadConfigValues() {
        if(!file.exists()) {
            Constants.LOG.info("Config file not found, creating it.");
            initializeConfig();
            return true;
        }

        if(isOldConfig()) {
            Constants.LOG.info("Detected old config format. Converting to new format.");
            if(!readAndResaveOldFormat()) {
                Constants.LOG.error("Failed to convert old configuration format. Using defaults instead.");
                initializeConfig();
                return true;
            }
        }

        // If it exists, read it.
        HashMap<String, Boolean> values = null;
        try(FileReader reader = new FileReader(file)) {
            values = (HashMap<String, Boolean>) gson.fromJson(reader, HashMap.class);
        } catch(ClassCastException e) {
            Constants.LOG.error("Formatting error while reading the config. Using default/current Values instead.");
            Constants.LOG.debug("Failed while casting config contents");
        } catch(JsonSyntaxException e) {
            Constants.LOG.error("JSON Syntax error while reading the config. Using default/current Values instead.");
        } catch(Exception e) {
            Constants.LOG.error("Failure reading the config. Using default/current Values instead.");
            Constants.LOG.debug("Failure: {}", e.toString());
        }
        if(values == null) return loadDefaults();

        for(String key : values.keySet()) {
            Optional<ConfigurationElement> opt = findConfigurationElement(key);
            if(opt.isEmpty()) {
                Constants.LOG.warn("Extra unrecognized Configuration Entry: \"{}\". Ignoring...", key);
                continue;
            }
            settings.get(opt.get()).setValue(values.get(key));
        }
        return true;
    }

    private boolean loadDefaults() {
        for(ConfigurationElement element : ConfigurationElement.values()) {
            this.settings.put(element, new Setting(element.defaultValue));
        }
        return false;
    }

    private void initializeConfig() {
        loadDefaults();

        try {
            //noinspection ResultOfMethodCallIgnored (Doesn't matter if it already exists as it's contents are rewritten)
            file.createNewFile();
            saveConfig();
        } catch(IOException e) {
            Constants.LOG.error("Couldn't create the config. Config changes will not persist.");
        }
    }

    void toggle(ConfigurationElement element) { /* package-private */
        settings.get(element).toggle();
    }

    void saveConfig() { /* package-private */
        try {
            HashMap<String, Boolean> values = new HashMap<>();
            for(ConfigurationElement element : ConfigurationElement.values()) {
                values.put(element.configName, settings.get(element).getValue());
            }
            FileWriter writer = new FileWriter(file);
            writer.write(gson.toJson(values));
            writer.close();
        } catch(IOException e) {
            Constants.LOG.error("Failure to save configuration. Config changes will not persist.");
        }
    }

    public boolean isOldConfig() {
        try (FileReader reader = new FileReader(file)) {
            JsonElement root = JsonParser.parseReader(reader);
            if(root == null || !root.isJsonObject()) return false;

            JsonObject jsonObject = root.getAsJsonObject();
            return jsonObject.has("sections") && jsonObject.get("sections").isJsonArray();
        } catch(Exception e) {
            Constants.LOG.error("Couldn't determine if config is old. Presuming it's not.");
            return false;
        }
    }

    public boolean readAndResaveOldFormat() {
        if(!isOldConfig()) return false;

        try (FileReader reader = new FileReader(file)) {
            JsonElement root = JsonParser.parseReader(reader);
            if(root == null || !root.isJsonObject()) return false;

            JsonObject jsonObject = root.getAsJsonObject();

            JsonArray sections = jsonObject.getAsJsonArray("sections");
            if(sections == null || sections.isEmpty()) return false;

            JsonObject values = sections.get(0).getAsJsonObject().getAsJsonObject("values");
            if(values == null) return false;


            for(Map.Entry<String, JsonElement> entry : values.entrySet()) {
                String key = entry.getKey();
                boolean value = entry.getValue().getAsBoolean();
                Optional<ConfigurationElement> opt = findConfigurationElement(key);
                if(opt.isPresent()) {
                    settings.get(opt.get()).setValue(value);
                    continue;
                }

                Constants.LOG.info("Invalid/Old configuration entry: \"{}\". Checking for older keys...", key);
                if(!updatePossiblyOldKey(key, value))
                    Constants.LOG.warn("Couldn't associate \"{}\" with a proper config option, discarding it.", key);
            }

            saveConfig();
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    // Some helper functions

    private Optional<ConfigurationElement> findConfigurationElement(String key) {
        if(settings.isEmpty()) loadDefaults();
        return settings.keySet().stream()
                .filter(c -> c.configName.equalsIgnoreCase(key))
                .findFirst();
    }

    private boolean updatePossiblyOldKey(String key, boolean value) {
        for(ConfigurationElement element : ConfigurationElement.values()) {
            if(element.previousConfigNames == null) continue;

            if(element.previousConfigNames.contains(key)) {
                Constants.LOG.info("Changed old key \"{}\" to \"{}\".", key, element.configName);
                settings.get(element).setValue(value);
                return true;
            }
        }
        return false;
    }

}
