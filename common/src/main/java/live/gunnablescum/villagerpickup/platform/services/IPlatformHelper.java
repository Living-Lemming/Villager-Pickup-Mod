package live.gunnablescum.villagerpickup.platform.services;

import java.io.File;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Get the Path of the Platform-Dependant config Directory.
     *
     * @return Path of config Directory.
     */
    File getConfigDirectory();
}