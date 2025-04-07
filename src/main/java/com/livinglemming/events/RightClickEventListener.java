package com.livinglemming.events;

import live.gunnablescum.configuration.ConfigurationHandler;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class RightClickEventListener {
    public static void registerRightClickEvent() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if(world.isClientSide) return InteractionResult.PASS;
            if(!ConfigurationHandler.getBoolean("enable_villager_pickup")) return InteractionResult.PASS;
            if (player.isShiftKeyDown() && entity instanceof Villager villager) {
                CompoundTag nbt = new CompoundTag();
                villager.addAdditionalSaveData(nbt);

                Item spawnEgg = SpawnEggItem.byId(villager.getType());
                if (spawnEgg != null) {
                    ItemStack spawnEggStack = new ItemStack(spawnEgg);

                    nbt.put("id", StringTag.valueOf("minecraft:villager"));
                    CustomData entityData = CustomData.of(nbt);

                    Component healthText = createHealthText(villager.getHealth(), villager.getMaxHealth());
                    Component professionText = createLoreText("Profession: [" + villager.getVillagerData().profession().getRegisteredName() + "]");

                    List<Component> lore = new java.util.ArrayList<>(List.of(healthText, professionText));

                    // Add Level to lore if applicable
                    if(villager.getVillagerData().level() > 1) {
                        lore.add(createLoreText("Level: [" + villager.getVillagerData().level() + "]"));
                    }

                    MerchantOffers list = villager.getOffers();
                    if(!list.isEmpty()) {
                        lore.add(Component.literal(""));
                        lore.add(createLoreText("Trades:"));
                    }
                    for(MerchantOffer offer : list) {
                        lore.add(convertTradeToText(offer));
                    }
                    ItemLore loreData = new ItemLore(lore);

                    DataComponentPatch.Builder changes = DataComponentPatch.builder()
                            .set(DataComponents.ENTITY_DATA, entityData)
                            .set(DataComponents.LORE, loreData);

                    if(villager.hasCustomName()) {
                        changes.set(DataComponents.CUSTOM_NAME, villager.getCustomName());
                    }

                    spawnEggStack.applyComponentsAndValidate(changes.build());
                    if (player.getInventory().getFreeSlot() != -1) {
                        player.addItem(spawnEggStack);
                    } else {
                        player.drop(spawnEggStack, true);
                    }
                }

                villager.remove(Entity.RemovalReason.DISCARDED);
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!player.isCreative() && world.getBlockState(hitResult.getBlockPos()).getBlock() == Blocks.SPAWNER && player.getItemInHand(hand).getItem() == Items.VILLAGER_SPAWN_EGG) {
                return InteractionResult.FAIL;
            }

            return InteractionResult.PASS;
        });
    }

    private static @NotNull Component convertTradeToText(MerchantOffer offer) {
    ItemStack firstBuyItem = offer.getItemCostA().itemStack();
    Optional<ItemCost> secondBuyItem = offer.getItemCostB();
    ItemStack sellItem = offer.getResult();

    MutableComponent toDisplay = Component.literal(firstBuyItem.getCount() + "x ").append(Component.translatable(firstBuyItem.getItem().getDescriptionId()));
    if (secondBuyItem.isPresent()) {
        ItemStack secondBuyItemStack = secondBuyItem.get().itemStack();
        toDisplay.append(" + ").append(secondBuyItemStack.getCount() + "x ").append(Component.translatable(secondBuyItemStack.getItem().getDescriptionId()));
    }
    toDisplay.append(" = " + sellItem.getCount() + "x ").append(Component.translatable(sellItem.getItem().getDescriptionId()));
    return createLoreText(toDisplay);
}

    private static MutableComponent createLoreText(String inputText) {
        MutableComponent text = Component.literal(inputText);
        return text.setStyle(text.getStyle().withItalic(false).withColor(0x808080));
    }

    private static MutableComponent createLoreText(MutableComponent inputText) {
        return inputText.setStyle(inputText.getStyle().withItalic(false).withColor(0x808080));
    }

    private static Component createHealthText(float health, float maxHealth) {
        char fullHeart = '❤';
        char damagedHeart = '❥';

        int fullHearts = (int) Math.floor(health);
        int halfHearts = (int) Math.ceil(health - fullHearts);

        MutableComponent fullHeartText = Component.literal(String.valueOf(fullHeart).repeat(fullHearts)).withColor(0xFF0000);
        MutableComponent halfHeartText = Component.literal(String.valueOf(damagedHeart).repeat(halfHearts)).withColor(0xFF0000);
        MutableComponent missingHeartText = Component.literal(String.valueOf(fullHeart).repeat((int) Math.ceil(maxHealth - health))).withColor(0x808080);

        return createLoreText("Health: ").append(fullHeartText).append(halfHeartText).append(missingHeartText);
    }

}