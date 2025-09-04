package com.endgy.datagen;

import com.endgy.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleState(ModBlocks.WORLD_FURNACE);
        blockStateModelGenerator.registerSimpleState(ModBlocks.WORLD_FURNACE_FANCY);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

    }
}