package live.gunnablescum.villagerpickup;

import live.gunnablescum.villagerpickup.configuration.ConfigurationElement;
import live.gunnablescum.villagerpickup.configuration.ConfigurationHandler;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.TagValueOutput;
#if MC_26_1
import net.minecraft.world.entity.EntityType;
#elif MC_26_2
import net.minecraft.world.entity.EntityTypes;
#endif

import java.util.List;
import java.util.Optional;

public class CommonMechanics {

    public static InteractionResult handleVillagerInteraction(Player player, Entity entity, InteractionHand hand) {
        if(!ConfigurationHandler.get(ConfigurationElement.ENABLE_VILLAGER_PICKUP)) return InteractionResult.PASS; // If the mod is disabled, don't do anything.

        #if MC_26_1
        if(entity.getType() != EntityType.VILLAGER) return InteractionResult.PASS; // Don't do anything if it's not a villager.
        #elif MC_26_2
        if(entity.getType() != EntityTypes.VILLAGER) return InteractionResult.PASS; // Don't do anything if it's not a villager.
        #endif

        if(player.level().isClientSide()) return InteractionResult.SUCCESS_SERVER; // Don't process anything on the client side, show the client that their interaction has been acknowledged.
        if(!player.isCrouching() && player.getItemInHand(hand).getItem() == Items.VILLAGER_SPAWN_EGG) return InteractionResult.FAIL; // Don't spawn baby villagers
        if(!player.isCrouching()) return InteractionResult.PASS; // Don't do anything if the player isn't sneaking.

        ItemStack spawnEgg = convertVillagerToItemStack((Villager)entity);

        if(spawnEgg == null) return InteractionResult.PASS; // There isn't a Villager Spawn Egg sometimes for some reason.

        if(player.getInventory().getFreeSlot() != 1) player.getInventory().add(spawnEgg);
        else player.drop(spawnEgg, true);
        return InteractionResult.SUCCESS;
    }

    public static InteractionResult handleSpawnerInteraction(Player player, Block block, InteractionHand hand) {
        if(player.isCreative()) return InteractionResult.PASS;
        if(block != Blocks.SPAWNER) return InteractionResult.PASS;
        if(player.getItemInHand(hand).getItem() != Items.VILLAGER_SPAWN_EGG) return InteractionResult.PASS;
        return InteractionResult.FAIL; // Fix for #31
    }

    public static ItemStack convertVillagerToItemStack(Villager villager) {
        TagValueOutput nbt = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, villager.level().registryAccess());

        villager.addAdditionalSaveData(nbt);


        nbt.discard("sleeping_pos");
        #if MC_26_1
        nbt.putString("id", EntityType.VILLAGER.getDescriptionId());
        Optional<Holder<Item>> spawnEgg = SpawnEggItem.byId(EntityType.VILLAGER);
        #elif MC_26_2
        nbt.putString("id", EntityTypes.VILLAGER.getDescriptionId());
        Optional<Holder<Item>> spawnEgg = SpawnEggItem.byId(EntityTypes.VILLAGER);
        #endif

        if(spawnEgg.isEmpty()) return null;

        ItemStack spawnEggStack = new ItemStack(spawnEgg.get().value());

        ItemLore loreData = CommonUtilities.createLore(villager);

        DataComponentPatch.Builder changes = DataComponentPatch.builder()
                #if MC_26_1
                .set(DataComponents.ENTITY_DATA, TypedEntityData.of(EntityType.VILLAGER, nbt.buildResult()))
                #elif MC_26_2
                        .set(DataComponents.ENTITY_DATA, TypedEntityData.of(EntityTypes.VILLAGER, nbt.buildResult()))
                #endif
                .set(DataComponents.LORE, loreData);

        if(villager.hasCustomName()) {
            //noinspection DataFlowIssue (getCustomName can return null, but we check for it above)
            changes.set(DataComponents.CUSTOM_NAME, villager.getCustomName());
        }

        changes.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(
                List.of(),
                List.of(),
                List.of(villager.getVillagerData().profession().getRegisteredName()),
                List.of()
        ));

        spawnEggStack.applyComponentsAndValidate(changes.build());

        // Yay, Villager is converted. Remove it.
        villager.remove(Entity.RemovalReason.DISCARDED);

        return spawnEggStack;
    }

}
