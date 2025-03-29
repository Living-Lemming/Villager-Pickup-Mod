package com.livinglemming;

import live.gunnablescum.configuration.ConfigurationHandler;
import com.livinglemming.events.RightClickEventListener;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.literal;

public class VillagerPickup implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("villager-pickup");

	@Override
	public void onInitialize() {
		registerCommand();
		RightClickEventListener.registerRightClickEvent();
	}

	private void registerCommand() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
			dispatcher.register(literal("villager-pickup")
					.then(literal("status").executes(context -> {
						context.getSource().sendMessage(Text.literal("Villager-Pickup Status:").fillStyle(Style.EMPTY.withFormatting(Formatting.GOLD)));
						context.getSource().sendMessage(getStatusOfBool("enable_villager_pickup", "Villager Pickup"));
						context.getSource().sendMessage(getStatusOfBool("allow_villager_rename_with_anvil", "Villager Anvil Renaming"));
						return 1;
					}))
					.then(literal("reload").requires(source -> source.hasPermissionLevel(4)).executes(context -> {
						ConfigurationHandler.reloadConfig();
						context.getSource().sendFeedback(() -> Text.literal("Config Reload successful.").fillStyle(Style.EMPTY.withFormatting(Formatting.GREEN)), true);
						return 1;
					})));
		});
	}

	private Text getStatusOfBool(String key, String displayName) {
		boolean value = ConfigurationHandler.getBoolean(key);
		return Text.literal(displayName + ": ").fillStyle(Style.EMPTY.withFormatting(value ? Formatting.GREEN : Formatting.RED)).append(Text.literal(value ? "Enabled" : "Disabled"));
	}

}