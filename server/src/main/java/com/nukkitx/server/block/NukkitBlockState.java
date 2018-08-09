package com.nukkitx.server.block;

import com.google.common.base.Preconditions;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.block.BlockStateBuilder;
import com.nukkitx.api.block.BlockType;
import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.metadata.blockentity.BlockEntity;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;
import java.util.Optional;

@Immutable
public class NukkitBlockState implements BlockState {

    public static final NukkitBlockState AIR = new NukkitBlockState(BlockTypes.AIR, null, null);
    private final BlockType type;
    private final Metadata metadata;
    private final BlockEntity blockEntity;

    public NukkitBlockState(BlockType type, Metadata metadata, BlockEntity blockEntity) {
        this.type = Preconditions.checkNotNull(type, "type");
        this.metadata = metadata;
        this.blockEntity = blockEntity;
    }

    @Override
    public BlockType getBlockType() {
        return type;
    }

    @Override
    public Optional<Metadata> getBlockData() {
        return Optional.ofNullable(metadata);
    }

    @Override
    public Optional<BlockEntity> getBlockEntity() {
        return Optional.ofNullable(blockEntity);
    }

    @Override
    public BlockStateBuilder toBuilder() {
        return new NukkitBlockStateBuilder().setBlockType(type).setMetadata(blockEntity == null ? metadata : blockEntity);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NukkitBlockState that = (NukkitBlockState) o;
        return this.type == that.type && Objects.equals(this.metadata, that.metadata) &&
                Objects.equals(this.blockEntity, that.blockEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, metadata, blockEntity);
    }

    @Override
    public String toString() {
        return "NukkitBlockState(" +
                "type='minecraft:" + type.getName() +
                "', metadata=" + metadata +
                ", blockEntity=" + blockEntity +
                ')';
    }
}
