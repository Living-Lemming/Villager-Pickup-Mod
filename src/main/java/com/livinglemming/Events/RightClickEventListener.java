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
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradedItem;

import java.util.List;
import java.util.Optional;

public class RightClickEventListener {
    public static void registerRightClickEvent() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if(world.isClient) return ActionResult.PASS;
            if (player.isSneaking() && entity instanceof VillagerEntity villager) {
                NbtCompound nbt = new NbtCompound();
                villager.writeCustomDataToNbt(nbt);

                Item spawnEgg = SpawnEggItem.forEntity(villager.getType());
                if (spawnEgg != null) {
                    ItemStack spawnEggStack = new ItemStack(spawnEgg);

                    nbt.put("id", NbtString.of("minecraft:villager"));
                    NbtComponent entityData = NbtComponent.of(nbt);

                    // TODO: Display Health as Hearts
                    Text healthText = createLoreText("Health: [" + villager.getHealth() + " / " + villager.getMaxHealth() + "]");
                    Text professionText = createLoreText("Profession: [" + villager.getVillagerData().getProfession() + "]");

                    List<Text> lore = new java.util.ArrayList<>(List.of(healthText, professionText));


                    // TODO: Trades in the lore
                    TradeOfferList list = villager.getOffers();
                    for(TradeOffer offer : list) {
                        ItemStack firstBuyItem = offer.getFirstBuyItem().itemStack();
                        Optional<TradedItem> secondBuyItem = offer.getSecondBuyItem();
                        ItemStack sellItem = offer.getSellItem();

                        String toDisplay = firstBuyItem.getCount() + "x " + firstBuyItem.getItemName().toString();
                        if(secondBuyItem.isPresent()) {
                            ItemStack secondBuyItemStack = secondBuyItem.get().itemStack();
                            toDisplay += " + " + secondBuyItemStack.getCount() + "x " + secondBuyItemStack.getItemName().toString();
                        }
                        toDisplay += " = " + sellItem.getCount() + "x " + sellItem.getItemName().toString();

                        lore.add(createLoreText(toDisplay));
                    }
                    LoreComponent loreData = new LoreComponent(lore);

                    ComponentChanges changes = ComponentChanges.builder()
                            .add(DataComponentTypes.ENTITY_DATA, entityData)
                            .add(DataComponentTypes.LORE, loreData)
                            .build();

                    spawnEggStack.applyChanges(changes);
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

    private static Text createLoreText(String inputText) {
        MutableText text = Text.literal(inputText);
        return text.setStyle(text.getStyle().withItalic(false).withColor(0x808080));
    }

}