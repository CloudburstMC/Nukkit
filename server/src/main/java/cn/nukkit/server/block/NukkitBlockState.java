package cn.nukkit.server.block;

import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.block.BlockStateBuilder;
import cn.nukkit.api.block.BlockType;
import cn.nukkit.api.block.entity.BlockEntity;
import cn.nukkit.api.metadata.Metadata;
import com.google.common.base.Preconditions;

import java.util.Optional;

public class NukkitBlockState implements BlockState {
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
    public Metadata getBlockData() {
        return metadata;
    }

    @Override
    public Optional<BlockEntity> getBlockEntity() {
        return Optional.ofNullable(blockEntity);
    }

    @Override
    public BlockStateBuilder toBuilder() {
        return new NukkitBlockStateBuilder().setBlockType(type).setMetadata(blockEntity == null ? metadata : blockEntity);
    }
}
