package com.livinglemming;

import com.livinglemming.events.RightClickEventListener;
import live.gunnablescum.configuration.ConfigurationHandler;
import live.gunnablescum.configuration.ConfigurationScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.commands.Commands.literal;

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
						context.getSource().sendSystemMessage(Component.literal("Villager-Pickup Status:").withStyle(Style.EMPTY.applyFormat(ChatFormatting.GOLD)));
						context.getSource().sendSystemMessage(getStatusOfBool("enable_villager_pickup", "Villager Pickup"));
						context.getSource().sendSystemMessage(getStatusOfBool("allow_villager_rename_with_anvil", "Villager Anvil Renaming"));
						return 1;
					}))
					.then(literal("reload").requires(source -> source.hasPermission(4)).executes(context -> {
						ConfigurationHandler.reloadConfig();
						context.getSource().sendSuccess(() -> Component.literal("Config Reload successful.").withStyle(Style.EMPTY.applyFormat(ChatFormatting.GREEN)), true);
						return 1;
					}))
					.then(literal("config-gui").requires(source -> source.hasPermission(4)).executes(context -> {
						if(context.getSource().isPlayer()) {
							ServerPlayer player = context.getSource().getPlayer();
							context.getSource().sendSuccess(() -> Component.literal("Editing Villager-Pickup Config...").withStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)), true);
							player.openMenu(new SimpleMenuProvider((syncId, playerInventory, playerEntity) -> new ConfigurationScreenHandler(syncId, playerInventory), Component.literal("Villager-Pickup Config")));
						} else {
							context.getSource().sendSuccess(() -> Component.literal("This command can only be executed by a player.").withStyle(Style.EMPTY.applyFormat(ChatFormatting.RED)), false);
							return 0;
						}
						return 1;
					}))
			);
		});
	}

	private Component getStatusOfBool(String key, String displayName) {
		boolean value = ConfigurationHandler.getBoolean(key);
		return Component.literal(displayName + ": ").withStyle(Style.EMPTY.applyFormat(value ? ChatFormatting.GREEN : ChatFormatting.RED)).append(Component.literal(value ? "Enabled" : "Disabled"));
	}

}