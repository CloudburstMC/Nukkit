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
        try {
            return (T) getBlockData().orElseThrow(() -> new IllegalArgumentException("BlockState has no data"));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Invalid class supplied", e);
        }
    }

    default <T extends BlockEntity> T ensureBlockEntity(Class<T> clazz) {
        try {
            return (T) getBlockEntity().orElseThrow(() -> new IllegalArgumentException("BlockState has no entity"));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Invalid class supplied", e);
        }
    }
}
