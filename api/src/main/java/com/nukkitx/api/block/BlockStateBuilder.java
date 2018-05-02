package com.nukkitx.api.block;

import com.nukkitx.api.metadata.Metadata;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface BlockStateBuilder {

    BlockStateBuilder setBlockType(@Nonnull BlockType type);

    BlockStateBuilder setMetadata(@Nullable Metadata metadata);

    BlockState build();
}
