package live.gunnablescum.villagerpickup.platform;

import live.gunnablescum.villagerpickup.Constants;
import live.gunnablescum.villagerpickup.platform.services.IPlatformHelper;

public class Services {

    // Expose a PlatformHelper so we can determine the Config Folder
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    // Runtime detection via platform loader class presence (deterministic when JARs are merged)
    // God I hope these don't change anytime soon
    public static <T> T load(Class<T> clazz) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        if (clazz.equals(IPlatformHelper.class)) {
            // Check if we are on NeoForge
            if (isClassPresent("net.neoforged.fml.loading.FMLLoader", classLoader)) {
                Object inst = instantiate("live.gunnablescum.villagerpickup.platform.NeoForgePlatformHelper", classLoader);
                if (inst != null) return clazz.cast(inst);
            }

            // Check if we are on Fabric
            if (isClassPresent("net.fabricmc.loader.api.FabricLoader", classLoader)) {
                Object inst = instantiate("live.gunnablescum.villagerpickup.platform.FabricPlatformHelper", classLoader);
                if (inst != null) return clazz.cast(inst);
            }
        }

        // Uh oh. Something's wrong, I can feel it.
        throw new NullPointerException(String.format("[%s] Couldn't determine running ModLoader or instantiate the correct PlatformHelper, cowardly throwing a NullPointerException...", Constants.MOD_NAME));
    }

    private static boolean isClassPresent(String fqcn, ClassLoader classLoader) {
        try {
            Class.forName(fqcn, false, classLoader);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        } catch (Throwable t) {
            // Any other error treat as absent and log
            Constants.LOG.warn("Error while checking presence of {}", fqcn, t);
            return false;
        }
    }

    private static Object instantiate(String fqcn, ClassLoader classLoader) {
        try {
            return Class.forName(fqcn, true, classLoader).getDeclaredConstructor().newInstance();
        } catch (Throwable t) {
            Constants.LOG.warn("Failed to instantiate {} via reflection", fqcn, t);
            return null;
        }
    }
}