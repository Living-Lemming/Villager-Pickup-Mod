package live.gunnablescum.configuration;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

public class ConfigurationScreenHandler extends ChestMenu {

    public ConfigurationScreenHandler(int syncId, Inventory playerInventory) {
        super(MenuType.GENERIC_9x3, syncId, playerInventory, new SimpleContainer(9 * 3), 3);
        updateInventory();
    }

    // TODO: Replace this with a Paginated system if enough config options are added
    private void updateInventory() {
        ItemStack[] content = new ItemStack[27];
        // Fill container with barrier blocks
        for (int i = 0; i < content.length; i++) {
            content[i] = Items.LIGHT_GRAY_STAINED_GLASS_PANE.getDefaultInstance();
            DataComponentPatch.Builder changes = DataComponentPatch.builder();
            changes.set(DataComponents.CUSTOM_NAME, Component.nullToEmpty(""));
            content[i].applyComponentsAndValidate(changes.build());
        }
        content[11] = Items.VILLAGER_SPAWN_EGG.getDefaultInstance();
        DataComponentPatch.Builder changes = DataComponentPatch.builder();
        changes.set(DataComponents.ITEM_NAME, Component.literal("Villager Pickup").withStyle(ChatFormatting.GOLD));
        changes.set(DataComponents.LORE, new ItemLore(
                getStatusLore(
                        ConfigurationHandler.getBoolean("enable_villager_pickup"),
                        "Toggle this option to enable or disable Villager Pickup."
                )
        ));
        content[11].applyComponentsAndValidate(changes.build());

        content[15] = Items.ANVIL.getDefaultInstance();
        changes = DataComponentPatch.builder();
        changes.set(DataComponents.ITEM_NAME, Component.literal("Allow Villager Rename with Anvil").withStyle(ChatFormatting.GOLD));
        changes.set(DataComponents.LORE, new ItemLore(
                getStatusLore(
                        ConfigurationHandler.getBoolean("allow_villager_rename_with_anvil"),
                        "Toggle this option to enable or disable",
                        "renaming Villagers in an Anvil."
                )
        ));
        content[15].applyComponentsAndValidate(changes.build());

        for(int i = 0; i < content.length; i++) {
            this.slots.get(i).setByPlayer(content[i]);
        }
    }

    private List<Component> getStatusLore(boolean status, String... loreText) {
        List<Component> lore = new ArrayList<>();
        for(String str : loreText) {
            lore.add(Component.literal(str).withStyle(ChatFormatting.GRAY));
        }

        lore.add(Component.literal("Status:").withStyle(ChatFormatting.GRAY));
        MutableComponent enabled = Component.literal("Enabled").withStyle(ChatFormatting.GREEN);
        enabled.withStyle(enabled.getStyle().withUnderlined(status));
        lore.add(enabled);

        MutableComponent disabled = Component.literal("Disabled").withStyle(ChatFormatting.RED);
        disabled.withStyle(disabled.getStyle().withUnderlined(!status));
        lore.add(disabled);

        return lore;
    }

    @Override
    public void removed(Player player) {
        for(Player operator : player.getServer().getPlayerList().getPlayers()) {
            if(!operator.hasPermissions(4)) continue;
            operator.displayClientMessage(
                    Component.literal("[")
                            .append(player.getDisplayName())
                            .append(": Changed Villager-Pickup Config]")
                            .withStyle(Style.EMPTY
                                    .applyFormat(ChatFormatting.GRAY)
                                    .withItalic(true)
                            ),
                    false
            );
        }

        ConfigurationHandler.saveConfig();
        super.removed(player);
    }

    @Override
    public void clicked(int slotId, int button, ClickType actionType, Player player) {
        // Edge case - Player gets deopped while in the config screen
        if(!player.hasPermissions(4)) {
            return;
        }

        switch (slotId) {
            case 11:
                ConfigurationHandler.setBoolean("enable_villager_pickup", !ConfigurationHandler.getBoolean("enable_villager_pickup"));
                break;
            case 15:
                ConfigurationHandler.setBoolean("allow_villager_rename_with_anvil", !ConfigurationHandler.getBoolean("allow_villager_rename_with_anvil"));
                break;
        }
        updateInventory();
    }
}