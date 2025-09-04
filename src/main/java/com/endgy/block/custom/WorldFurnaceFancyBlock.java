package com.endgy.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WorldFurnaceFancyBlock extends Block {
    public WorldFurnaceFancyBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {

        PlayerEntity player = ctx.getPlayer();

        if (player != null && !player.isCreative()) {
            // Prevent placement in Survival/Adventure
            return null; // Returning null cancels placement
        }

        return this.getDefaultState();
    }
}
