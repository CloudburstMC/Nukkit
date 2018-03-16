package cn.nukkit.api.block;

import cn.nukkit.api.metadata.Metadata;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface BlockStateBuilder {

    BlockStateBuilder setBlockType(@Nonnull BlockType type);

    BlockStateBuilder setMetadata(@Nullable Metadata metadata);

    BlockState build();
}
