package com.nukkitx.api.block;

import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.metadata.Metadatable;
import com.nukkitx.api.metadata.blockentity.BlockEntity;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

@Immutable
public interface BlockState extends Metadatable {

    BlockType getBlockType();

    Optional<BlockEntity> getBlockEntity();

    BlockStateBuilder toBuilder();

    default <T extends BlockEntity> T ensureBlockEntity(Class<T> clazz) {
        try {
            return (T) getBlockEntity().orElseThrow(() -> new IllegalArgumentException("BlockState has no entity"));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Invalid class supplied", e);
        }
    }
}
