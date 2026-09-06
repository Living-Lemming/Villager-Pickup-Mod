package live.gunnablescum.villagerpickup.configuration;

import java.util.List;

public enum ConfigurationElement {
    ENABLE_VILLAGER_PICKUP("enable_villager_pickup", "Enable Villager Pickup", "Toggle this option to enable or disable Villager Pickup.", true, null),
    ALLOW_VILLAGER_ANVIL_RENAME("allow_villager_anvil_rename", "Allow Villager Rename with Anvil", "Toggle this option to enable or disable renaming Villagers in an Anvil.", false, List.of("allow_villager_rename_with_anvil"));

    public final String configName;
    public final String displayName;
    public final String description;
    public final boolean defaultValue;
    public final List<String> previousConfigNames;

    ConfigurationElement(String configName, String displayName, String description, boolean defaultValue, List<String> previousConfigNames) {
        this.configName = configName;
        this.displayName = displayName;
        this.description = description;
        this.defaultValue = defaultValue;
        this.previousConfigNames = previousConfigNames;
    }
}
