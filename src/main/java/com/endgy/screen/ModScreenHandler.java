package com.endgy.screen;

import com.endgy.WorldFurnace;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandler {
    public static final ScreenHandlerType<WorldFurnaceScreenHandler> WORLD_FURNACE_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(WorldFurnace.MOD_ID, "world_furnace"),
                    new ExtendedScreenHandlerType<>(WorldFurnaceScreenHandler::new));

    public static void registerScreenHandlers() {
        WorldFurnace.LOGGER.info("Registering Screen Handlers for " + WorldFurnace.MOD_ID);
    }
}