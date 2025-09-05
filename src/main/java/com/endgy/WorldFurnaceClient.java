package com.endgy;

import com.endgy.screen.ModScreenHandler;
import com.endgy.screen.WorldFurnaceScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class WorldFurnaceClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandler.WORLD_FURNACE_SCREEN_HANDLER, WorldFurnaceScreen::new);
    }
}