package cn.nukkit.server.block;

import cn.nukkit.api.block.Block;
import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.level.Level;
import cn.nukkit.api.level.chunk.Chunk;
import cn.nukkit.api.util.BoundingBox;
import com.flowpowered.math.vector.Vector3i;

public class NukkitBlock implements Block {
    private final Level level;
    private final Chunk chunk;
    private final Vector3i blockPosition;
    private final BlockState state;

    public NukkitBlock(Level level, Chunk chunk, Vector3i blockPosition, BlockState state) {
        this.level = level;
        this.chunk = chunk;
        this.blockPosition = blockPosition;
        this.state = state;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public Chunk getChunk() {
        return chunk;
    }

    @Override
    public Vector3i getBlockPosition() {
        return blockPosition;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return new BoundingBox(blockPosition.toFloat().floor(), blockPosition.toFloat().ceil());
    }

    @Override
    public BlockState getBlockState() {
        return state;
    }
}
