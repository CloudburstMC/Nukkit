package cn.nukkit.api.block;

import cn.nukkit.api.level.Level;
import cn.nukkit.api.level.chunk.Chunk;
import cn.nukkit.api.util.BoundingBox;
import com.flowpowered.math.vector.Vector3i;

public interface Block extends BlockSnapshot {

    Level getLevel();

    Chunk getChunk();

    Vector3i getLevelLocation();

    default Vector3i getBlockPosition() {
        Vector3i level = getLevelLocation();
        return new Vector3i(level.getX() & 0x0f, level.getY(), level.getZ() & 0x0f);
    }

    BoundingBox getBoundingBox();
}
