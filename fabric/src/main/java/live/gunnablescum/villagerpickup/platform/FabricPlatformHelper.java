package live.gunnablescum.villagerpickup.platform;

import live.gunnablescum.villagerpickup.Constants;
import live.gunnablescum.villagerpickup.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public File getConfigDirectory() {
        Constants.LOG.info("Accessing config directory via Fabric API");
        return FabricLoader.getInstance().getConfigDir().toFile();
    }
}
