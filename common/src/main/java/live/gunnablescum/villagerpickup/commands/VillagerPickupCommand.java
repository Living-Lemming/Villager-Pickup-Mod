package live.gunnablescum.villagerpickup.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import live.gunnablescum.villagerpickup.configuration.ConfigurationElement;
import live.gunnablescum.villagerpickup.configuration.ConfigurationHandler;
import live.gunnablescum.villagerpickup.configuration.ConfigurationScreenHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.SimpleMenuProvider;

import static live.gunnablescum.villagerpickup.CommonClass.getDescribedCfgOption;
import static net.minecraft.commands.Commands.literal;

public class VillagerPickupCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("villager-pickup")
                .then(literal("status").executes(VillagerPickupCommand::configurationStatus))
                .then(literal("reload").requires(VillagerPickupCommand::isOperator).executes(VillagerPickupCommand::reloadConfiguration))
                .then(literal("config-gui").requires(VillagerPickupCommand::isOperator).executes(VillagerPickupCommand::openConfigurationMenu))
        );
    }

    private static boolean isOperator(CommandSourceStack css) {
        return css.permissions().hasPermission(Permissions.COMMANDS_OWNER);
    }

    private static int configurationStatus(CommandContext<CommandSourceStack> context) {
        CommandSourceStack css = context.getSource();
        css.sendSystemMessage(Component.literal("Villager-Pickup Status:").withStyle(ChatFormatting.GOLD));
        css.sendSystemMessage(getDescribedCfgOption(ConfigurationElement.ENABLE_VILLAGER_PICKUP));
        css.sendSystemMessage(getDescribedCfgOption(ConfigurationElement.ALLOW_VILLAGER_ANVIL_RENAME));
        return 1;
    }

    private static int reloadConfiguration(CommandContext<CommandSourceStack> context) {
        if(ConfigurationHandler.reloadConfig()) {
            context.getSource().sendSuccess(() -> Component.literal("Config Reload successful.").withStyle(ChatFormatting.GREEN), true);
            return 1;
        }
        context.getSource().sendFailure(Component.literal("Failure to load Configuration. Check Console."));
        return 0;
    }

    private static int openConfigurationMenu(CommandContext<CommandSourceStack> context) {
        if(context.getSource().isPlayer()) {
            ServerPlayer player = context.getSource().getPlayer();
            context.getSource().sendSuccess(() -> Component.literal("Editing Villager-Pickup Config..."), true);
            //noinspection DataFlowIssue (player is not null, because there is a isPlayer check above)
            player.openMenu(new SimpleMenuProvider((syncId, playerInventory, _) -> new ConfigurationScreenHandler(syncId, playerInventory), Component.literal("Villager-Pickup Config")));
            return 1;
        }
        context.getSource().sendFailure(Component.literal("This command can only be executed by a player."));
        return 0;
    }

}
