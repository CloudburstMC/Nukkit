package com.nukkitx.api.block;

import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.metadata.blockentity.BlockEntity;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

@Immutable
public interface BlockState {

    BlockType getBlockType();

    Optional<Metadata> getBlockData();

    Optional<BlockEntity> getBlockEntity();

    BlockStateBuilder toBuilder();

    default <T extends Metadata> T ensureBlockData(Class<T> clazz) {
        return (T) getBlockData().get();
    }

    default <T extends BlockEntity> T ensureBlockEntity(Class<T> clazz) {
        return (T) getBlockEntity().get();
    }
}
