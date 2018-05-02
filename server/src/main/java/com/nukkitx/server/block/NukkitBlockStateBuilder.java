package com.nukkitx.server.block;

import com.google.common.base.Preconditions;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.block.BlockStateBuilder;
import com.nukkitx.api.block.BlockType;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.metadata.blockentity.BlockEntity;
import com.nukkitx.server.metadata.MetadataSerializers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NukkitBlockStateBuilder implements BlockStateBuilder {
    private BlockType type;
    private Metadata metadata;

    @Override
    public BlockStateBuilder setBlockType(@Nonnull BlockType type) {
        this.type = Preconditions.checkNotNull(type, "type");
        this.metadata = null;
        return this;
    }

    @Override
    public BlockStateBuilder setMetadata(@Nullable Metadata metadata) {
        if (metadata != null) {
            Preconditions.checkState(type != null, "BlockType has not been set");
            Preconditions.checkArgument(type.getMetadataClass() != null, "Block does not have metadata");
            Preconditions.checkArgument(metadata.getClass().isAssignableFrom(type.getMetadataClass()), "Invalid metadata for BlockType");
        }
        this.metadata = metadata;
        return this;
    }

    @Override
    public BlockState build() {
        Preconditions.checkState(type != null, "BlockType must be set");
        if (type.getMetadataClass() != null && metadata == null) {
            metadata = MetadataSerializers.deserializeMetadata(type, (short) 0);
        }
        return new NukkitBlockState(type, metadata, metadata instanceof BlockEntity ? (BlockEntity) metadata : null);
    }
}
