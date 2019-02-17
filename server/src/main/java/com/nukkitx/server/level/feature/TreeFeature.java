package com.nukkitx.server.level.feature;

import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.block.BlockSnapshot;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.level.Level;
import com.nukkitx.server.level.WorldChangeTransaction;

import java.util.Optional;
import java.util.Random;

public class TreeFeature extends Feature {
    protected TreeFeature(WorldChangeTransaction transaction) {
        super(transaction);
    }

    @Override
    public boolean place(Level level, Vector3i position, Random random) {
        return place(level, position, random, random.nextInt() % 3 + 5);
    }

    public boolean place(Level level, Vector3i position, Random random, int height) {
        if (!prepareSpawn(level, position, height)) {
            return false;
        }
        return true;
    }

    protected void placeLeaves(Level level, Vector3i position) {

    }

    protected boolean isFree(Block block) {
        return false;
    }

    protected void placeFallenTrunk(Level level, Vector3i position, Random random) {

    }

    protected boolean placeTrunk(Level level, Vector3i position, Random random, int height) {
        return false;
    }

    protected boolean prepareSpawn(Level level, Vector3i position, int height) {
        // Check for enough vertical space
        if (position.getY() < 1 || (position.getY() + height) >= level.getHeight()) {
            return false;
        }

        Optional<BlockState> block = level.getBlockIfChunkLoaded(position).map(BlockSnapshot::getBlockState);
        if (!block.isPresent()) {
            return false;
        }
        return true;
    }
}
