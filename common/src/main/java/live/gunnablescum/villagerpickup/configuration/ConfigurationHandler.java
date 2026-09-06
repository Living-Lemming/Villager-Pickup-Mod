package live.gunnablescum.villagerpickup.configuration;

import live.gunnablescum.villagerpickup.platform.Services;

import java.io.File;

public class ConfigurationHandler {
    private static final Configuration config = new Configuration(getConfigFile());

    public static boolean reloadConfig() {
        return config.loadConfigValues();
    }

    public static boolean get(ConfigurationElement element) {
        return config.settings.get(element);
    }

    public static void toggle(ConfigurationElement element) {
        config.toggle(element);
    }

    private static File getConfigFile() {
        return new File(Services.PLATFORM.getConfigDirectory(), "villager-pickup.json");
    }

    public static void saveConfig() {
        config.saveConfig();
    }

}