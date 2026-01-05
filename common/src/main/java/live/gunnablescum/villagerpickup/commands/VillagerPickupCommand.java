package live.gunnablescum.villagerpickup.commands;

import com.mojang.brigadier.CommandDispatcher;
import live.gunnablescum.villagerpickup.configuration.ConfigurationHandler;
import live.gunnablescum.villagerpickup.configuration.ConfigurationScreenHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.SimpleMenuProvider;

import static live.gunnablescum.villagerpickup.CommonClass.getStatusOfBool;
import static net.minecraft.commands.Commands.literal;

public class VillagerPickupCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("villager-pickup")
                .then(literal("status").executes(context -> {
                    context.getSource().sendSystemMessage(Component.literal("Villager-Pickup Status:").withStyle(ChatFormatting.GOLD));
                    context.getSource().sendSystemMessage(getStatusOfBool("enable_villager_pickup", "Villager Pickup"));
                    context.getSource().sendSystemMessage(getStatusOfBool("allow_villager_rename_with_anvil", "Villager Anvil Renaming"));
                    return 1;
                }))
                .then(literal("reload").requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_OWNER)).executes(context -> {
                    ConfigurationHandler.reloadConfig();
                    context.getSource().sendSuccess(() -> Component.literal("Config Reload successful.").withStyle(ChatFormatting.GREEN), true);
                    return 1;
                }))
                .then(literal("config-gui").requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_OWNER)).executes(context -> {
                    if(context.getSource().isPlayer()) {
                        ServerPlayer player = context.getSource().getPlayer();
                        context.getSource().sendSuccess(() -> Component.literal("Editing Villager-Pickup Config...").withStyle(ChatFormatting.GRAY), true);
                        player.openMenu(new SimpleMenuProvider((syncId, playerInventory, playerEntity) -> new ConfigurationScreenHandler(syncId, playerInventory), Component.literal("Villager-Pickup Config")));
                    } else {
                        context.getSource().sendSuccess(() -> Component.literal("This command can only be executed by a player.").withStyle(ChatFormatting.RED), false);
                        return 0;
                    }
                    return 1;
                }))
        );
    }

}
