package live.gunnablescum.villagerpickup.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
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
        for(ConfigurationElement element : ConfigurationElement.values()) {
            this.settings.put(element, new Setting(element.defaultValue));
        }
        loadConfigValues();
    }

    public boolean loadConfigValues() {
        if(!file.exists()) {
            Constants.LOG.info("Config file not found, creating it.");
            initializeConfig();
            return true;
        }

        HashMap<String, Boolean> values = null;
        try {
            // If it exists, read it.
            FileReader reader = new FileReader(file);
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
        if(values == null) return false;

        for(String key : values.keySet()) {
            Optional<ConfigurationElement> opt = settings.keySet().stream().filter(c -> c.configName.equalsIgnoreCase(key)).findFirst();
            if(opt.isEmpty()) {
                Constants.LOG.warn("Extra unrecognized Configuration Entry: \"{}\". Ignoring...", key);
                continue;
            }
            settings.get(opt.get()).setValue(values.get(key));
        }
        return true;
    }

    private void initializeConfig() {
        try {
            //noinspection ResultOfMethodCallIgnored (I don't care if it already exists because it was checked for in it's only usage)
            file.createNewFile();
            saveConfig();
        } catch(IOException e) {
            Constants.LOG.error("Couldn't create the config. Config changes will not persist.");
        }
    }

    void toggle(ConfigurationElement element) { /* package-private */
        settings.get(element).toggle();
    }

    boolean saveConfig() { /* package-private */
        try {
            HashMap<String, Boolean> values = new HashMap<>();
            for(ConfigurationElement element : ConfigurationElement.values()) {
                values.put(element.configName, settings.get(element).getValue());
            }
            FileWriter writer = new FileWriter(file);
            writer.write(gson.toJson(values));
            writer.close();
            return true;
        } catch(IOException e) {
            Constants.LOG.error("Failure to save configuration. Config changes will not persist.");
        }
        return false;
    }
}
