package live.gunnablescum.villagerpickup.platform;

import live.gunnablescum.villagerpickup.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

@SuppressWarnings("unused") // It's used, loaded dynamically.
public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public File getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir().toFile();
    }
}
