package com.endgy.item;

import com.endgy.WorldFurnace;
import com.endgy.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup WF_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(WorldFurnace.MOD_ID, "worldfurnace"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.worldfurnace"))
                    .icon(() -> new ItemStack(ModBlocks.WORLD_FURNACE)).entries((displayContext, entries) -> {
                        entries.add(ModBlocks.WORLD_FURNACE);
                        entries.add(ModBlocks.WORLD_FURNACE_FANCY);
                    }).build());


    public static void registerItemGroups() {
        WorldFurnace.LOGGER.info("Registering Item Groups for " + WorldFurnace.MOD_ID);
    }

}
