package com.livinglemming;

import com.livinglemming.Events.RightClickEventListener;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityPickup implements ModInitializer {
	public static final String MODID = "entity-pickup";
	public static final Logger LOGGER = LoggerFactory.getLogger("Entity Pickup");

	@Override
	public void onInitialize() {
		RightClickEventListener.registerRightClickEvent();

		MidnightConfig.init("entity-pickup", EntityPickupConfig.class);
	}
}