package live.gunnablescum.villagerpickup.platform.services;

import java.io.File;

public interface IPlatformHelper {

    /**
     * Get the Path of the Platform-Dependant config Directory.
     *
     * @return Path of config Directory.
     */
    File getConfigDirectory();

}