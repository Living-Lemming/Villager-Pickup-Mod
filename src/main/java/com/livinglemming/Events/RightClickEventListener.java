package com.livinglemming.Events;

import com.livinglemming.EntityPickupConfig;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;

import java.awt.*;

public class RightClickEventListener {
    public static void registerRightClickEvent() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player.isSneaking() && entity instanceof VillagerEntity villager) {
                if ( EntityPickupConfig.Villager ) {
                    NbtCompound nbt = new NbtCompound();
                    villager.writeCustomDataToNbt(nbt);

                    Item spawnEgg = SpawnEggItem.forEntity(villager.getType());
                    if (spawnEgg != null) {
                        ItemStack spawnEggStack = new ItemStack(spawnEgg);

                        NbtCompound nbtCompound = new NbtCompound();
                        NbtCompound textCompound = new NbtCompound();
                        NbtList tooltipList = new NbtList();

                        nbtCompound.put("EntityTag", nbt);
                        tooltipList.add(NbtString.of("{\"text\":\"Profession: [" + villager.getVillagerData().getProfession().toString() + "]\",\"color\":\"gray\",\"italic\":false}"));
                        textCompound.put("Lore", tooltipList);
                        nbtCompound.put("display", textCompound);

                        spawnEggStack.setNbt(nbtCompound);
                        if (player.getInventory().getEmptySlot() != -1) {
                            player.giveItemStack(spawnEggStack);
                        } else {
                            player.dropItem(spawnEggStack, true);
                        }
                    }
                    villager.remove(Entity.RemovalReason.DISCARDED);
                    return ActionResult.SUCCESS;
                }
                if (player.isSneaking() && entity instanceof PiglinEntity piglin) {
                    if ( EntityPickupConfig.Piglin ) {
                        NbtCompound nbt = new NbtCompound();
                        piglin.writeCustomDataToNbt(nbt);

                        if (!PiglinBrain.wearsGoldArmor(player)) {
                            player.sendMessage(Text.literal("You need to equip some gold armor to pickup a piglin"));
                            return ActionResult.FAIL;
                        }

                        Item spawnEgg = SpawnEggItem.forEntity(piglin.getType());
                        if (spawnEgg != null) {
                            ItemStack spawnEggStack = new ItemStack(spawnEgg);

                            NbtCompound nbtCompound = new NbtCompound();

                            nbtCompound.put("EntityTag", nbt);

                            spawnEggStack.setNbt(nbtCompound);
                            if (player.getInventory().getEmptySlot() != -1) {
                                player.giveItemStack(spawnEggStack);
                            } else {
                                player.dropItem(spawnEggStack, true);
                            }
                        }

                        piglin.remove(Entity.RemovalReason.DISCARDED);
                        return ActionResult.SUCCESS;
                    }
                }
            }

            return ActionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!player.isCreative() && world.getBlockState(hitResult.getBlockPos()).getBlock() == Blocks.SPAWNER && player.getStackInHand(hand).getItem() == Items.VILLAGER_SPAWN_EGG) {
                player.sendMessage(Text.literal("You cannot put picked up mobs in spawners!").formatted(Formatting.RED));
                world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 1f, 0.5f);
                return ActionResult.FAIL;
            }
            if ( !player.isCreative() && world.getBlockState(hitResult.getBlockPos()).getBlock() == Blocks.SPAWNER && player.getStackInHand(hand).getItem() == Items.PIGLIN_SPAWN_EGG ) {
                player.sendMessage(Text.literal("You cannot put picked up mobs in spawners!").formatted(Formatting.RED));
                world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 1f, 0.5f);
                return ActionResult.FAIL;
            }

            return ActionResult.PASS;
        });
    }
}