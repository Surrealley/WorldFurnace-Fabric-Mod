package com.endgy.item;

import com.endgy.WorldFurnace;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(WorldFurnace.MOD_ID, name), item);
    }

    public static void registerModItems() {
        WorldFurnace.LOGGER.info("Registering Mod Items for " + WorldFurnace.MOD_ID);

    }
}
