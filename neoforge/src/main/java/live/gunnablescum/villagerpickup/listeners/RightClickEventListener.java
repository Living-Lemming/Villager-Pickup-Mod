package live.gunnablescum.villagerpickup.listeners;

import live.gunnablescum.villagerpickup.CommonClass;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
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

        ItemStack item = player.getItemInHand(hand);
        if(item.getItem() == Items.VILLAGER_SPAWN_EGG)
        {
            boolean cond;
            #if PRE_TYPED_ENTITY_DATA
            cond = item.getComponents().get(DataComponents.ENTITY_DATA) != null;
            #else
            cond = !item.getComponents().get(DataComponents.ENTITY_DATA).copyTagWithoutId().isEmpty();
            #endif
            if(cond) {
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true); // Fix for #26
                return;
            }
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
