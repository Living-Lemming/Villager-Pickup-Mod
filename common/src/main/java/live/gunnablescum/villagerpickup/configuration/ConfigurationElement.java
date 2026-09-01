package live.gunnablescum.villagerpickup.configuration;

public enum ConfigurationElement {
    ENABLE_VILLAGER_PICKUP("enable_villager_pickup", "Enable Villager Pickup", true),
    ALLOW_VILLAGER_ANVIL_RENAME("allow_villager_anvil_rename", "Allow Villager Rename with Anvil", false);

    public final String configName;
    public final String displayName;
    public final boolean defaultValue;

    ConfigurationElement(String configName, String displayName, boolean defaultValue) {
        this.configName = configName;
        this.displayName = displayName;
        this.defaultValue = defaultValue;
    }
}
