package live.gunnablescum.villagerpickup;

import com.mojang.brigadier.CommandDispatcher;
import live.gunnablescum.villagerpickup.commands.VillagerPickupCommand;
import live.gunnablescum.villagerpickup.configuration.ConfigurationElement;
import live.gunnablescum.villagerpickup.configuration.ConfigurationHandler;
import live.gunnablescum.villagerpickup.configuration.Setting;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class CommonClass {

    public static void initCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        // Register the VillagerPickup Command
        VillagerPickupCommand.register(dispatcher);
    }

    public static Component getDescribedCfgOption(ConfigurationElement element) {
        Setting setting = ConfigurationHandler.getSetting(element);
        return Component.literal(element.displayName + ": ")
                .append(setting.getValue() ? "Enabled" : "Disabled")
                .withStyle(setting.getValue() ? ChatFormatting.GREEN : ChatFormatting.RED);
    }
}