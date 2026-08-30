package live.gunnablescum.villagerpickup.listeners;

import live.gunnablescum.villagerpickup.CommonClass;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
#if MC_26_1
import net.minecraft.world.entity.EntityType;
#elif MC_26_2
import net.minecraft.world.entity.EntityTypes;
#endif

public class RightClickEventListener {

    public static void registerListener() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            #if MC_26_1
            if(entity.getType() != EntityType.VILLAGER) return InteractionResult.PASS;
            #elif MC_26_2
            if(entity.getType() != EntityTypes.VILLAGER) return InteractionResult.PASS;
            #endif
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
        UseBlockCallback.EVENT.register(((player, level, interactionHand, blockHitResult) -> {

            if (!player.isCreative() && level.getBlockState(blockHitResult.getBlockPos()).getBlock() == Blocks.SPAWNER && player.getItemInHand(interactionHand).getItem() == Items.VILLAGER_SPAWN_EGG) {
                return InteractionResult.FAIL; // Fix for #31
            }

            return InteractionResult.PASS;
        }));
    }

}
