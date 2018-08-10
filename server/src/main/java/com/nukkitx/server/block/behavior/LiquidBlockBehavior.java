package com.nukkitx.server.block.behavior;

import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.metadata.block.Liquid;
import com.nukkitx.server.entity.BaseEntity;

import java.util.List;
import java.util.Optional;

public abstract class LiquidBlockBehavior extends SimpleBlockBehavior {
    private static final int[] FLOW_COST = new int[]{1000, 1000, 1000, 1000};
    private static final int DECAY_PER_BLOCK = 1;

    @Override
    public boolean onUpdate(Block block) {
        BlockState state = block.getBlockState();
        Liquid liquid = state.ensureMetadata(Liquid.class);
        byte level = liquid.getLevel();
        int multiplier = getFlowDecayPerBlock();

        if (level > 0) {

        }
        return false;
    }

    @Override
    public boolean onEntityCollision(BaseEntity entity) {
        return false;
    }

    protected boolean canFlowInto(Block block) {
        BlockState state = block.getBlockState();
        return state.getBlockType().isFloodable() &&
                !(state.getMetadata().isPresent() && state.getMetadata().get() instanceof Liquid && ((Liquid) state.getMetadata().get()).getLevel() == 0);
    }

    protected int getFlowDecayPerBlock() {
        return DECAY_PER_BLOCK;
    }

    protected Vector3i getFlowVector(Block liquid) {
        Vector3i flowVector = Vector3i.ZERO;

        int level = liquid.getBlockState().ensureMetadata(Liquid.class).getLevel();

        List<Block> neighbors = liquid.getNeighboringBlocksIfLoaded();
        for (Block neighbor : neighbors) {
            if (neighbor.getBlockPosition().getY() != liquid.getBlockPosition().getY()) {
                continue;
            }

            int flowLevel = getFlowLevel(liquid, neighbor);
            if (flowLevel < 0) {
                if (!neighbor.getBlockState().getBlockType().isFloodable()) {
                    continue;
                }
                Optional<Block> below = liquid.getLevel().getBlockIfChunkLoaded(liquid.getBlockPosition().sub(0, -1, 0));
                flowLevel = below.map(block -> getFlowLevel(liquid, block)).orElse(-1);

                if (flowLevel >= 0) {
                    int realLevel = flowLevel - (level - 8);
                    flowVector = flowVector.add(neighbor.getBlockPosition().sub(liquid.getBlockPosition()).mul(realLevel));
                }
            } else {
                int realLevel = flowLevel - level;
                flowVector = flowVector.add(neighbor.getBlockPosition().sub(liquid.getBlockPosition()).mul(realLevel));
            }
        }

        if (level >= 8) {
            boolean floodable = false;
            for (Block neighbor : neighbors) {
                if (neighbor.getBlockState().getBlockType().isFloodable()) {
                    floodable = true;
                    break;
                }
            }
            if (floodable) {
                flowVector = flowVector.add(0, -6, 0);
            }
        }

        return flowVector;
    }

    protected int getFlowLevel(Block block, Block neighbor) {
        if (block.getBlockState().getBlockType() != neighbor.getBlockState().getBlockType()) {
            return -1;
        } else {
            return neighbor.getBlockState().ensureMetadata(Liquid.class).getLevel();
        }
    }
}
