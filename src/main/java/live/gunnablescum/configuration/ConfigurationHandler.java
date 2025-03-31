package live.gunnablescum.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.livinglemming.VillagerPickup;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigurationHandler {
    private static Configuration config = Configuration.loadConfig(getConfigFile());

    public static void reloadConfig() {
        config = Configuration.loadConfig(getConfigFile());
    }

    public static boolean getBoolean(String key) {
        for (ConfigurationObject<?> section : config.sections) {
            if (section instanceof ConfigurationObject<?> && section.values.get(key) instanceof Boolean) {
                return (boolean) section.values.get(key);
            }
        }
        return false; // Default to false if key not found
    }

    // Suppressing unchecked cast warning because the instanceof check ensures the cast is safe
    @SuppressWarnings("unchecked")
    public static void setBoolean(String key, boolean value) {
        for (ConfigurationObject<?> section : config.sections) {
            if (section instanceof ConfigurationObject<?> && section.values.get(key) instanceof Boolean) {
                ((ConfigurationObject<Boolean>)section).values.put(key, value);
            }
        }
    }

    private static File getConfigFile() {
        return new File(FabricLoader.getInstance().getConfigDir().toFile(), "villager-pickup.json");
    }

    public static void saveConfig() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            FileWriter writer = new FileWriter(getConfigFile());
            writer.write(gson.toJson(config));
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class Configuration {

    List<ConfigurationObject<?>> sections;

    public Configuration() {
        sections = new java.util.ArrayList<>();
    }

    private static void loadDefaults(Configuration configuration) {
        ConfigurationObject<Boolean> boolSection = new ConfigurationObject<>();
        boolSection.values.put("enable_villager_pickup", true);
        boolSection.values.put("allow_villager_rename_with_anvil", false);
        configuration.sections.add(boolSection);
    }

    private static void createConfig(Gson gson, File file) throws IOException {
        VillagerPickup.LOGGER.info("Creating configuration file: {}", file.getAbsolutePath());
        Configuration configuration = new Configuration();
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        loadDefaults(configuration);
        writer.write(gson.toJson(configuration));
        writer.close();
    }

    public static Configuration loadConfig(File file) {
        VillagerPickup.LOGGER.info("Loading configuration file: {}", file.getAbsolutePath());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            if(!file.exists()) createConfig(gson, file);
            FileReader reader = new FileReader(file);
            return gson.fromJson(reader, Configuration.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

class ConfigurationObject<T> {
    Map<String, T> values;

    public ConfigurationObject() {
        values = new HashMap<>();
    }

}