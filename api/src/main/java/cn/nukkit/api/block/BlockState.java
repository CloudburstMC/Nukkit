package cn.nukkit.api.block;

import cn.nukkit.api.metadata.Metadata;
import cn.nukkit.api.metadata.blockentity.BlockEntity;

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
