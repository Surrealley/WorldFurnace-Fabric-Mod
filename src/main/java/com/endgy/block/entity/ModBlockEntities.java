package com.endgy.block.entity;

import com.endgy.WorldFurnace;
import com.endgy.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<WorldFurnaceBlockEntity> WORLD_FURNACE_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(WorldFurnace.MOD_ID, "world_furnace"),
                    FabricBlockEntityTypeBuilder.create(WorldFurnaceBlockEntity::new,
                            ModBlocks.WORLD_FURNACE).build());

    public static void registerBlockEntities() {
        WorldFurnace.LOGGER.info("Registering Block Entities for " + WorldFurnace.MOD_ID);
    }
}
