package cn.nukkit.api.block;

import cn.nukkit.api.block.entity.BlockEntity;
import cn.nukkit.api.metadata.Metadata;

import java.util.Optional;

public interface BlockState {

    BlockType getBlockType();

    Metadata getBlockData();

    Optional<BlockEntity> getBlockEntity();
}
