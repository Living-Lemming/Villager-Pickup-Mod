package live.gunnablescum.villagerpickup.platform;

import live.gunnablescum.villagerpickup.platform.services.IPlatformHelper;
import net.neoforged.fml.loading.FMLPaths;

import java.io.File;

@SuppressWarnings("unused") // It's used, loaded dynamically.
public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public File getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get().toFile();
    }
}