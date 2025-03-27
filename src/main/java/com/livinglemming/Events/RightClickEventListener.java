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
import org.jetbrains.annotations.NotNull;

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

                    Text healthText = createHealthText(villager.getHealth(), villager.getMaxHealth());
                    Text professionText = createLoreText("Profession: [" + villager.getVillagerData().getProfession() + "]");

                    List<Text> lore = new java.util.ArrayList<>(List.of(healthText, professionText));

                    // Add Level to lore if applicable
                    if(villager.getVillagerData().getLevel() > 1) {
                        lore.add(createLoreText("Level: [" + villager.getVillagerData().getLevel() + "]"));
                    }

                    TradeOfferList list = villager.getOffers();
                    if(!list.isEmpty()) {
                        lore.add(Text.literal(""));
                        lore.add(createLoreText("Trades:"));
                    }
                    for(TradeOffer offer : list) {
                        lore.add(convertTradeToText(offer));
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

    private static @NotNull Text convertTradeToText(TradeOffer offer) {
    ItemStack firstBuyItem = offer.getFirstBuyItem().itemStack();
    Optional<TradedItem> secondBuyItem = offer.getSecondBuyItem();
    ItemStack sellItem = offer.getSellItem();

    MutableText toDisplay = Text.literal(firstBuyItem.getCount() + "x ").append(Text.translatable(firstBuyItem.getItem().getTranslationKey()));
    if (secondBuyItem.isPresent()) {
        ItemStack secondBuyItemStack = secondBuyItem.get().itemStack();
        toDisplay.append(" + ").append(secondBuyItemStack.getCount() + "x ").append(Text.translatable(secondBuyItemStack.getItem().getTranslationKey()));
    }
    toDisplay.append(" = " + sellItem.getCount() + "x ").append(Text.translatable(sellItem.getItem().getTranslationKey()));
    return createLoreText(toDisplay);
}

    private static MutableText createLoreText(String inputText) {
        MutableText text = Text.literal(inputText);
        return text.setStyle(text.getStyle().withItalic(false).withColor(0x808080));
    }

    private static MutableText createLoreText(MutableText inputText) {
        return inputText.setStyle(inputText.getStyle().withItalic(false).withColor(0x808080));
    }

    private static Text createHealthText(float health, float maxHealth) {
        char fullHeart = '❤';
        char damagedHeart = '❥';

        int fullHearts = (int) Math.floor(health);
        int halfHearts = (int) Math.ceil(health - fullHearts);

        MutableText fullHeartText = Text.literal(String.valueOf(fullHeart).repeat(fullHearts)).withColor(0xFF0000);
        MutableText halfHeartText = Text.literal(String.valueOf(damagedHeart).repeat(halfHearts)).withColor(0xFF0000);
        MutableText missingHeartText = Text.literal(String.valueOf(fullHeart).repeat((int) Math.ceil(maxHealth - health))).withColor(0x808080);

        return createLoreText("Health: ").append(fullHeartText).append(halfHeartText).append(missingHeartText);
    }

}