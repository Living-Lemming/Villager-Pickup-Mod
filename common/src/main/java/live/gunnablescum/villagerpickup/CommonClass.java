package live.gunnablescum.villagerpickup;

import com.mojang.brigadier.CommandDispatcher;
import live.gunnablescum.villagerpickup.commands.VillagerPickupCommand;
import live.gunnablescum.villagerpickup.configuration.ConfigurationHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommonClass {

    public static void initCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        // Register the VillagerPickup Command
        VillagerPickupCommand.register(dispatcher);
    }

    public static ItemStack convertVillagerToItemStack(Player player, Entity entity) {
        if(player.level().isClientSide()) return null;
        if(!player.isCrouching()) return null;
        if(!ConfigurationHandler.getBoolean("enable_villager_pickup")) return null;
        if(!(entity instanceof Villager villager)) return null;
        TagValueOutput nbt = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, villager.level().registryAccess());
        villager.addAdditionalSaveData(nbt);

        nbt.discard("sleeping_pos");

        Item spawnEgg = SpawnEggItem.byId(EntityType.VILLAGER);
        if(spawnEgg == null) return null;
        ItemStack spawnEggStack = new ItemStack(spawnEgg);

        Component healthText = createHealthText(villager.getHealth(), villager.getMaxHealth());
        Component professionText = createLoreText(Component.literal("Profession: [" + villager.getVillagerData().profession().getRegisteredName() + "]"));

        List<Component> lore = new ArrayList<>(List.of(healthText, professionText));

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

        ItemLore loreData = new ItemLore(lore);

        DataComponentPatch.Builder changes = DataComponentPatch.builder()
                .set(DataComponents.ENTITY_DATA, TypedEntityData.of(EntityType.VILLAGER, nbt.buildResult()))
                .set(DataComponents.LORE, loreData);

        if(villager.hasCustomName()) {
            changes.set(DataComponents.CUSTOM_NAME, villager.getCustomName());
        }

        spawnEggStack.applyComponentsAndValidate(changes.build());

        return spawnEggStack;
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

    private static MutableComponent createLoreText(MutableComponent inputText) {
        return inputText.setStyle(inputText.getStyle().withItalic(false).withColor(0x808080));
    }

    public static Component getStatusOfBool(String key, String displayName) {
        boolean value = ConfigurationHandler.getBoolean(key);
        return Component.literal(displayName + ": " + value).withStyle(value ? ChatFormatting.GREEN : ChatFormatting.RED).append(Component.literal(value ? "Enabled" : "Disabled"));
    }
}