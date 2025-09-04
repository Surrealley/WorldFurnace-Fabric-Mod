package com.endgy.block;

import com.endgy.WorldFurnace;
import com.endgy.block.custom.WorldFurnaceBlock;
import com.endgy.block.custom.WorldFurnaceFancyBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block WORLD_FURNACE = registerBlock("world_furnace",
            new WorldFurnaceBlock(FabricBlockSettings.copyOf(Blocks.BLAST_FURNACE).requiresTool().strength(-1.0F, 3600000.0F).dropsNothing()));
    public static final Block WORLD_FURNACE_FANCY = registerBlock("world_furnace_fancy",
            new WorldFurnaceFancyBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).requiresTool().strength(-1.0F, 3600000.0F).dropsNothing()));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(WorldFurnace.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(WorldFurnace.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerModBlocks() {
        WorldFurnace.LOGGER.info("Registering ModBlocks for " + WorldFurnace.MOD_ID);
    }
}
