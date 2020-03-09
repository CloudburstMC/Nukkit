package cn.nukkit.blockentity;

import cn.nukkit.level.chunk.Chunk;
import com.nukkitx.math.vector.Vector3i;

public interface BlockEntityFactory<T extends BlockEntity> {

    T create(BlockEntityType<T> type, Chunk chunk, Vector3i position);
}
