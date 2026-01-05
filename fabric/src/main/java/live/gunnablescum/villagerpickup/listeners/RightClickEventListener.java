package live.gunnablescum.villagerpickup.listeners;

import live.gunnablescum.villagerpickup.CommonClass;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class RightClickEventListener {

    public static void registerListener() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if(entity.getType() != EntityType.VILLAGER) return InteractionResult.PASS;
            if(!player.isCrouching() && player.getItemInHand(hand).getItem() == Items.VILLAGER_SPAWN_EGG && entity instanceof Villager) return InteractionResult.FAIL; // Fix for #26

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
