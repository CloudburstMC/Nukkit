package cn.nukkit.api.block;

import cn.nukkit.api.level.Level;
import cn.nukkit.api.level.chunk.Chunk;
import cn.nukkit.api.util.BoundingBox;
import com.flowpowered.math.vector.Vector3i;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Block extends BlockSnapshot {

    Level getLevel();

    Chunk getChunk();

    Vector3i getBlockPosition();

    default Vector3i getChunkLocation() {
        Vector3i level = getBlockPosition();
        return new Vector3i(level.getX() & 0x0f, level.getY(), level.getZ() & 0x0f);
    }

    BoundingBox getBoundingBox();

    List<Block> getNeighboringBlocksIfLoaded();

    CompletableFuture<List<Block>> getNeighboringBlocks();
}
