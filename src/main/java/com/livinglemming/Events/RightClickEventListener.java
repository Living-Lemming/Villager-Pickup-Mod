package com.livinglemming.Events;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.block.Blocks;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public class RightClickEventListener {
    public static void registerRightClickEvent() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player.isSneaking() && entity instanceof VillagerEntity villager) {
                NbtCompound nbt = new NbtCompound();
                villager.writeCustomDataToNbt(nbt);

                Item spawnEgg = SpawnEggItem.forEntity(villager.getType());
                if (spawnEgg != null) {
                    ItemStack spawnEggStack = new ItemStack(spawnEgg);

                    nbt.put("id", NbtString.of("minecraft:villager"));
                    NbtComponent entityData = NbtComponent.of(nbt);

                    Text professionText = Text.literal("Profession: [" + villager.getVillagerData().getProfession() + "]");
                    LoreComponent loreData = new LoreComponent(professionText.getWithStyle(professionText.getStyle().withItalic(false).withColor(0x808080)));

                    ComponentChanges changes = ComponentChanges.builder()
                            .add(DataComponentTypes.ENTITY_DATA, entityData)
                            .add(DataComponentTypes.LORE, loreData)
                            .build();

                    spawnEggStack.applyChanges(changes);

                    // Pre Component Change Code below:
//                    NbtCompound nbtCompound = new NbtCompound();
//                    NbtCompound textCompound = new NbtCompound();
//                    NbtList tooltipList = new NbtList();
//
//                    nbtCompound.put("EntityTag", nbt);
//                    tooltipList.add(NbtString.of("{\"text\":\"Profession: [" + villager.getVillagerData().getProfession().toString() + "]\",\"color\":\"gray\",\"italic\":false}"));
//                    textCompound.put("Lore", tooltipList);
//                    nbtCompound.put("display", textCompound);
//
//                    spawnEggStack.setNbt(nbtCompound);
                    if (player.getInventory().getEmptySlot() != -1) {
                        player.giveItemStack(spawnEggStack);
                    } else {
                        player.dropItem(spawnEggStack, true);
                    }
                }

                villager.remove(Entity.RemovalReason.DISCARDED);
                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!player.isCreative() && world.getBlockState(hitResult.getBlockPos()).getBlock() == Blocks.SPAWNER && player.getStackInHand(hand).getItem() == Items.VILLAGER_SPAWN_EGG) {
                return ActionResult.FAIL;
            }

            return ActionResult.PASS;
        });
    }
}