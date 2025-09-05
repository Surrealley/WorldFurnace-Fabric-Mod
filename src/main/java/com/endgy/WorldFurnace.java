package com.endgy;

import com.endgy.block.ModBlocks;
import com.endgy.block.entity.ModBlockEntities;
import com.endgy.item.ModItemGroups;
import com.endgy.item.ModItems;
import com.endgy.screen.ModScreenHandler;
import com.endgy.screen.WorldFurnaceScreen;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldFurnace implements ModInitializer {
	public static final String MOD_ID = "worldfurnace";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerBlockEntities();
		ModScreenHandler.registerScreenHandlers();

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayerEntity player = handler.getPlayer();

			// Example: send a join message
			player.sendMessage(Text.translatable("worldfurnace.join"), false);
		});
		LOGGER.info("Hello Fabric world!");
	}

}