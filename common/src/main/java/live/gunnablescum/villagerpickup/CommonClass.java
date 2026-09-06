package live.gunnablescum.villagerpickup;

import com.mojang.brigadier.CommandDispatcher;
import live.gunnablescum.villagerpickup.commands.VillagerPickupCommand;
import live.gunnablescum.villagerpickup.configuration.ConfigurationElement;
import live.gunnablescum.villagerpickup.configuration.ConfigurationHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class CommonClass {

    public static void initCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        // Register the VillagerPickup Command
        VillagerPickupCommand.register(dispatcher);
    }

    public static Component getDescribedCfgOption(ConfigurationElement element) {
        boolean value = ConfigurationHandler.get(element);
        return Component.literal(element.displayName + ": ")
                .append(value ? "Enabled" : "Disabled")
                .withStyle(value ? ChatFormatting.GREEN : ChatFormatting.RED);
    }
}