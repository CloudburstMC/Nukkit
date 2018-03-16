package cn.nukkit.api.block;

import cn.nukkit.api.block.entity.BlockEntity;
import cn.nukkit.api.metadata.Metadata;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

@Immutable
public interface BlockState {

    BlockType getBlockType();

    Metadata getBlockData();

    Optional<BlockEntity> getBlockEntity();

    BlockStateBuilder toBuilder();
}
