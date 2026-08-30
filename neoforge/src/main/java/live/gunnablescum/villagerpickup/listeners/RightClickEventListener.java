package live.gunnablescum.villagerpickup.listeners;

import live.gunnablescum.villagerpickup.CommonMechanics;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class RightClickEventListener {

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        InteractionResult result = CommonMechanics.handleVillagerInteraction(event.getEntity(), event.getTarget(), event.getHand());
        event.setCancellationResult(result);
        if(result != InteractionResult.PASS) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        Block block = event.getLevel().getBlockState(event.getHitVec().getBlockPos()).getBlock();
        InteractionResult result = CommonMechanics.handleSpawnerInteraction(event.getEntity(), block, event.getHand());
        event.setCancellationResult(result);
        if(result != InteractionResult.PASS) event.setCanceled(true);
    }

}
