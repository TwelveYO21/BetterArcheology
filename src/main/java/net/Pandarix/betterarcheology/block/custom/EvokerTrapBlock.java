package net.Pandarix.betterarcheology.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class EvokerTrapBlock extends HorizontalFacingBlock {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;

    public EvokerTrapBlock(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState) ((BlockState) ((BlockState) this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(TRIGGERED, false));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        boolean bl = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());
        boolean bl2 = (Boolean) state.get(TRIGGERED);
        if (bl && !bl2) {
            world.scheduleBlockTick(pos, this, 4);
            world.setBlockState(pos, (BlockState) state.with(TRIGGERED, true), 4);
        } else if (!bl && bl2) {
            world.setBlockState(pos, (BlockState) state.with(TRIGGERED, false), 4);
        }

    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int maxFangs = 3;
        switch (state.get(FACING)) {
            case NORTH -> {
                for (int i = 0; i < maxFangs; ++i) {
                    world.spawnEntity(new EvokerFangsEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() - 0.5 - i * 1.5, (float) Math.toRadians(90), 0, null));
                }
            }
            case SOUTH -> {
                for (int i = 0; i < maxFangs; ++i) {
                    world.spawnEntity(new EvokerFangsEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 1.5 + i * 1.5, (float) Math.toRadians(90), 0, null));
                }
            }
            case EAST -> {
                for (int i = 0; i < maxFangs; ++i) {
                    world.spawnEntity(new EvokerFangsEntity(world, pos.getX() + 1.5 + i * 1.5, pos.getY(), pos.getZ() + 0.5, 0, 0, null));
                }
            }
            case WEST -> {
                for (int i = 0; i < maxFangs; ++i) {
                    world.spawnEntity(new EvokerFangsEntity(world, pos.getX() - 0.5 - i * 1.5, pos.getY(), pos.getZ() + 0.5, 0, 0, null));
                }
            }
            default -> {
                for (int i = 0; i < maxFangs; ++i) {
                    world.spawnEntity(new EvokerFangsEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() - 0.5 - i * 1.5, 0, 0, null));
                }
            }
        }
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState) state.with(FACING, rotation.rotate((Direction) state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction) state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TRIGGERED);
    }
}