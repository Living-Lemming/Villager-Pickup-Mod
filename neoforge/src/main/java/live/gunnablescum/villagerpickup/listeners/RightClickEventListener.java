package live.gunnablescum.villagerpickup.listeners;

import live.gunnablescum.villagerpickup.CommonClass;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class RightClickEventListener {

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        var player = event.getEntity();
        var entity = event.getTarget();
        var hand = event.getHand();

        if(event.getTarget().getType() != EntityType.VILLAGER) {
            event.setCancellationResult(InteractionResult.PASS); // Fix for #28
            return;
        }

        if(!player.isCrouching() && player.getItemInHand(hand).getItem() == Items.VILLAGER_SPAWN_EGG) {
            event.setCancellationResult(InteractionResult.FAIL); // Fix for #26
            event.setCanceled(true);
            return;
        }

        var spawnEgg = CommonClass.convertVillagerToItemStack(player, entity);

        if(spawnEgg == null) {
            event.setCancellationResult(InteractionResult.PASS);
            return;
        }

        if(player.getInventory().getFreeSlot() != -1) {
            player.getInventory().add(spawnEgg);
        } else {
            player.drop(spawnEgg, true);
        }

        entity.remove(Entity.RemovalReason.DISCARDED);
        event.setCanceled(true);
    }

}
