package live.gunnablescum.villagerpickup.listeners;

import live.gunnablescum.villagerpickup.CommonClass;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class RightClickEventListener {

    public static void registerListener() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if(!player.isCrouching() && player.getItemInHand(hand).getItem() == Items.VILLAGER_SPAWN_EGG) return InteractionResult.FAIL; // Fix for #26

            ItemStack spawnEgg = CommonClass.convertVillagerToItemStack(player, entity);

            if(spawnEgg == null) return InteractionResult.PASS;

            if(player.getInventory().getFreeSlot() != -1) {
                player.getInventory().add(spawnEgg);
            } else {
                player.drop(spawnEgg, true);
            }

            entity.remove(Entity.RemovalReason.DISCARDED);
            return InteractionResult.SUCCESS;
        });
    }

}
