package live.gunnablescum.villagerpickup;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommonUtilities {

    private static Component createHealthText(float health, float maxHealth) {
        char fullHeart = '❤';
        String damagedHeart = "\uD83D\uDC94";

        int fullHearts = (int) Math.floor(health);
        int halfHearts = (int) Math.ceil(health - fullHearts);

        MutableComponent fullHeartText = Component.literal(String.valueOf(fullHeart).repeat(fullHearts)).withColor(0xFF0000);
        MutableComponent halfHeartText = Component.literal(damagedHeart.repeat(halfHearts)).withColor(0xFF0000);
        MutableComponent missingHeartText = Component.literal(String.valueOf(fullHeart).repeat((int) Math.ceil(maxHealth - health))).withColor(0x808080);

        return createLoreText(Component.literal("Health: ")).append(fullHeartText).append(halfHeartText).append(missingHeartText);
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

        toDisplay.append(" = " + sellItem.getCount() + "x ");

        if (sellItem.getItem() != Items.ENCHANTED_BOOK) {
            toDisplay.append(Component.translatable(sellItem.getItem().getDescriptionId()));
            return createLoreText(toDisplay);
        }

        ItemEnchantments enchantments = sellItem.getComponents().get(DataComponents.STORED_ENCHANTMENTS);
        if(enchantments == null || enchantments.isEmpty() || enchantments.size() > 1) {
            toDisplay.append(Component.translatable(sellItem.getItem().getDescriptionId()));
            return createLoreText(toDisplay);
        }


        //noinspection OptionalGetWithoutIsPresent (There is a check above to make sure there is at least 1 enchantment)
        toDisplay.append(enchantments.keySet().stream().findFirst().get().value().description());
        return createLoreText(toDisplay);
    }

    public static ItemLore createLore(Villager villager) {
        List<Component> lore = new ArrayList<>();

        lore.add(createHealthText(villager.getHealth(), villager.getMaxHealth()));
        lore.add(Component.literal("Profession: [" + villager.getVillagerData().profession().getRegisteredName() + "]"));


        // Add Level to lore if applicable
        if(villager.getVillagerData().level() > 1) {
            lore.add(createLoreText(Component.literal("Level: [" + villager.getVillagerData().level() + "]")));
        }

        MerchantOffers list = villager.getOffers();

        if(!list.isEmpty()) {
            lore.add(Component.literal(""));
            lore.add(createLoreText(Component.literal("Trades:")));
        }

        for(MerchantOffer offer : list) {
            lore.add(convertTradeToText(offer));
        }

        return new ItemLore(lore);
    }

    private static MutableComponent createLoreText(MutableComponent inputText) {
        return inputText.setStyle(inputText.getStyle().withItalic(false).withColor(0x808080));
    }

}
