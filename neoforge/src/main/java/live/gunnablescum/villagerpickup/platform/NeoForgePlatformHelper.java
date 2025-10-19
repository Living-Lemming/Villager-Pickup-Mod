package live.gunnablescum.villagerpickup.platform;

import live.gunnablescum.villagerpickup.platform.services.IPlatformHelper;
import net.neoforged.fml.ModList;

import java.io.File;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public File getConfigDirectory() {
        return net.neoforged.fml.loading.FMLPaths.CONFIGDIR.get().toFile();
    }
}