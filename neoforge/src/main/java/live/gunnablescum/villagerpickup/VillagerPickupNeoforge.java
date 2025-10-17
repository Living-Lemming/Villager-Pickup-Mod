package live.gunnablescum.villagerpickup;

import live.gunnablescum.villagerpickup.listeners.RightClickEventListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(Constants.MOD_ID)
public class VillagerPickupNeoforge {

    public VillagerPickupNeoforge(IEventBus eventBus) {
        NeoForge.EVENT_BUS.addListener(VillagerPickupNeoforge::onCommandRegister);
        NeoForge.EVENT_BUS.register(new RightClickEventListener());
    }

    private static void onCommandRegister(RegisterCommandsEvent event) {
        CommonClass.initCommands(event.getDispatcher());
    }

}