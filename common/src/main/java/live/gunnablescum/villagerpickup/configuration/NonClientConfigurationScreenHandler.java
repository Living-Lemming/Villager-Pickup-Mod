package live.gunnablescum.villagerpickup.configuration;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

import java.util.ArrayList;
import java.util.List;

public class NonClientConfigurationScreenHandler extends ChestMenu {

    public NonClientConfigurationScreenHandler(int syncId, Inventory playerInventory) {
        super(MenuType.GENERIC_9x3, syncId, playerInventory, new SimpleContainer(27), 3);
        fillPanes();
        updateInventory();
    }

    private void updateInventory() {

        this.slots.get(11).set(createConfigurationItem(Items.VILLAGER_SPAWN_EGG, ConfigurationElement.ENABLE_VILLAGER_PICKUP));
        this.slots.get(12).set(createConfigurationItem(Items.NAME_TAG, ConfigurationElement.ALLOW_VILLAGER_ANVIL_RENAME));
//        this.slots.get(13).set(createConfigurationItem(Items.ZOMBIE_VILLAGER_SPAWN_EGG, ConfigurationElement.ENABLE_ZOMBIE_VILLAGER_PICKUP));
//        this.slots.get(14).set(createConfigurationItem(Items.PIGLIN_SPAWN_EGG, ConfigurationElement.ENABLE_PIGLIN_PICKUP));
//        this.slots.get(15).set(createConfigurationItem(Items.NAME_TAG, ConfigurationElement.ALLOW_PIGLIN_ANVIL_RENAME));
    }

    @Override
    public void clicked(int slotId, int button, ContainerInput actionType, Player player) {
        if(!(player instanceof ServerPlayer serverPlayer)) return;

        // Edge case - Player gets deopped while in the config screen
        if(!player.permissions().hasPermission(Permissions.COMMANDS_OWNER)) {
            removed(player);
            return;
        }


        ConfigurationElement element = switch (slotId) {
            case 11 -> ConfigurationElement.ENABLE_VILLAGER_PICKUP;
            case 12 -> ConfigurationElement.ALLOW_VILLAGER_ANVIL_RENAME;
//            case 13 -> ConfigurationElement.ENABLE_ZOMBIE_VILLAGER_PICKUP;
//            case 14 -> ConfigurationElement.ENABLE_PIGLIN_PICKUP;
//            case 15 -> ConfigurationElement.ALLOW_PIGLIN_ANVIL_RENAME;
            default -> null;
        };

        if(element == null) return;
        ConfigurationHandler.toggle(element);
        serverPlayer.createCommandSourceStack().sendSuccess(() -> Component.literal("Updated " + element.displayName + " to " + (ConfigurationHandler.get(element) ? "Enabled" : "Disabled")), true);
        updateInventory();
        ConfigurationHandler.saveConfig();
    }

    private ItemStack createConfigurationItem(Item icon, ConfigurationElement element) {
        ItemStack item = icon.getDefaultInstance();
        DataComponentPatch.Builder changes = DataComponentPatch.builder();
        changes.set(DataComponents.ITEM_NAME, Component.literal(element.displayName).withStyle(ChatFormatting.GOLD));
        changes.set(DataComponents.LORE, getStatusLore(element));
        item.applyComponentsAndValidate(changes.build());
        return item;
    }

    private void fillPanes() {
        // Fill the Inventory with Glass Panes as a Background
        #if MC_26_1
        ItemStack item = Items.GRAY_STAINED_GLASS_PANE.getDefaultInstance();
        #elif MC_26_2
        ItemStack item = Items.STAINED_GLASS_PANE.gray().getDefaultInstance();
        #endif

        DataComponentPatch.Builder changes = DataComponentPatch.builder();
        changes.set(DataComponents.CUSTOM_NAME, CommonComponents.EMPTY);
        item.applyComponentsAndValidate(changes.build());

        for (int i = 0; i < 9*3; i++) this.slots.get(i).set(item);
    }

    private ItemLore getStatusLore(ConfigurationElement element) {
        List<Component> lore = new ArrayList<>();
        StringBuilder built = new StringBuilder();
        for(String str : element.description.split(" ")) {
            if((built + str).length() > 40) {
                lore.add(Component.literal(built.toString().trim()).withStyle(ChatFormatting.GRAY));
                built = new StringBuilder();
            }
            built.append(str).append(" ");
        }
        lore.add(Component.literal(built.toString().trim()).withStyle(ChatFormatting.GRAY));

        boolean status = ConfigurationHandler.get(element);

        lore.add(Component.literal("Status:").withStyle(ChatFormatting.GRAY));
        MutableComponent enabled = Component.literal("Enabled").withStyle(ChatFormatting.GREEN);
        enabled.withStyle(enabled.getStyle().withUnderlined(status));
        lore.add(enabled);

        MutableComponent disabled = Component.literal("Disabled").withStyle(ChatFormatting.RED);
        disabled.withStyle(disabled.getStyle().withUnderlined(!status));
        lore.add(disabled);

        return new ItemLore(lore);
    }

}