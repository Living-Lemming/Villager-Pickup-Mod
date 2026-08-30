package live.gunnablescum.villagerpickup.listeners;

import live.gunnablescum.villagerpickup.CommonMechanics;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;

public class RightClickEventListener {

    public static void registerListener() {
        UseEntityCallback.EVENT.register((player, _, hand, entity, _) ->
                CommonMechanics.handleVillagerInteraction(player, entity, hand)
        );

        UseBlockCallback.EVENT.register((player, level, hand, hitResult) ->
                CommonMechanics.handleSpawnerInteraction(player, level.getBlockState(hitResult.getBlockPos()).getBlock(), hand)
        );
    }

}
