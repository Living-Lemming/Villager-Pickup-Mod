package live.gunnablescum.villagerpickup;

import live.gunnablescum.villagerpickup.listeners.RightClickEventListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class VillagerPickupFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        registerCommand();
        registerEvents();
    }

    private void registerCommand() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, __, ___) ->
                CommonClass.initCommands(dispatcher)
        ));
    }

    private void registerEvents() {
        RightClickEventListener.registerListener();
    }
}
