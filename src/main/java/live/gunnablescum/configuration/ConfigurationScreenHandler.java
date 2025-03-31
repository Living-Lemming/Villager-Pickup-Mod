package live.gunnablescum.configuration;

import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationScreenHandler extends GenericContainerScreenHandler {

    public ConfigurationScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(ScreenHandlerType.GENERIC_9X3, syncId, playerInventory, 3);
        updateInventory();
    }

    // TODO: Replace this with a Paginated system if enough config options are added
    private void updateInventory() {
        ItemStack[] content = new ItemStack[27];
        // Fill container with barrier blocks
        for (int i = 0; i < content.length; i++) {
            content[i] = Items.LIGHT_GRAY_STAINED_GLASS.getDefaultStack();
            ComponentChanges.Builder changes = ComponentChanges.builder();
            changes.add(DataComponentTypes.CUSTOM_NAME, Text.of(""));
            content[i].applyChanges(changes.build());
        }
        content[11] = Items.VILLAGER_SPAWN_EGG.getDefaultStack();
        ComponentChanges.Builder changes = ComponentChanges.builder();
        changes.add(DataComponentTypes.ITEM_NAME, Text.literal("Villager Pickup").formatted(Formatting.GOLD));
        changes.add(DataComponentTypes.LORE, new LoreComponent(
                getStatusLore(
                        ConfigurationHandler.getBoolean("enable_villager_pickup"),
                        "Toggle this option to enable or disable Villager Pickup."
                )
        ));
        content[11].applyChanges(changes.build());

        content[15] = Items.ANVIL.getDefaultStack();
        changes = ComponentChanges.builder();
        changes.add(DataComponentTypes.ITEM_NAME, Text.literal("Allow Villager Rename with Anvil").formatted(Formatting.GOLD));
        changes.add(DataComponentTypes.LORE, new LoreComponent(
                getStatusLore(
                        ConfigurationHandler.getBoolean("allow_villager_rename_with_anvil"),
                        "Toggle this option to enable or disable",
                        "renaming Villagers in an Anvil."
                )
        ));
        content[15].applyChanges(changes.build());

        for(int i = 0; i < content.length; i++) {
            this.slots.get(i).setStack(content[i]);
        }
    }

    private List<Text> getStatusLore(boolean status, String... loreText) {
        List<Text> lore = new ArrayList<>();
        for(String str : loreText) {
            lore.add(Text.literal(str).formatted(Formatting.GRAY));
        }

        lore.add(Text.literal("Status:").formatted(Formatting.GRAY));
        MutableText enabled = Text.literal("Enabled").formatted(Formatting.GREEN);
        enabled.fillStyle(enabled.getStyle().withUnderline(status));
        lore.add(enabled);

        MutableText disabled = Text.literal("Disabled").formatted(Formatting.RED);
        disabled.fillStyle(disabled.getStyle().withUnderline(!status));
        lore.add(disabled);

        return lore;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        for(PlayerEntity operator : player.getServer().getPlayerManager().getPlayerList()) {
            if(operator.getPermissionLevel() != 4) continue;
            operator.sendMessage(
                    Text.literal("[")
                            .append(player.getDisplayName())
                            .append(": Changed Villager-Pickup Config]")
                            .fillStyle(Style.EMPTY
                                    .withFormatting(Formatting.GRAY)
                                    .withItalic(true)
                            ),
                    false
            );
        }

        ConfigurationHandler.saveConfig();
        super.onClosed(player);
    }

    @Override
    public void onSlotClick(int slotId, int button, SlotActionType actionType, PlayerEntity player) {
        // Edge case - Player gets deopped while in the config screen
        if(player.getPermissionLevel() != 4) {
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