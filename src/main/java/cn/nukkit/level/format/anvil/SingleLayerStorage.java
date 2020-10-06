/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cn.nukkit.level.format.anvil;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.format.anvil.util.BlockStorage;
import cn.nukkit.level.format.anvil.util.ImmutableBlockStorage;
import cn.nukkit.utils.BinaryStream;
import lombok.SneakyThrows;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

/**
 * @author joserobjr
 * @since 2020-10-02
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public final class SingleLayerStorage extends LayerStorage {
    private BlockStorage storage;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public SingleLayerStorage() {
        storage = ImmutableBlockStorage.EMPTY;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public SingleLayerStorage(BlockStorage storage) {
        this.storage = storage;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean hasBlocks() {
        return storage.hasBlockIds();
    }

    @SneakyThrows(CloneNotSupportedException.class)
    @Override
    public SingleLayerStorage clone() {
        SingleLayerStorage clone = (SingleLayerStorage) super.clone();
        clone.storage = clone.storage.copy();
        return clone;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public BlockStorage getStorageOrEmpty(int layer) {
        switch (layer) {
            case 0:
                return storage;
            case 1:
                return ImmutableBlockStorage.EMPTY;
            default:
                throw new IndexOutOfBoundsException("Invalid layer: " + layer);
        }
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public BlockStorage getOrSetStorage(Consumer<LayerStorage> setLayerStorage, IntSupplier contentVersion, int layer) {
        switch (layer) {
            case 0:
                break;
            case 1:
                BlockStorage blockStorage = createBlockStorage(contentVersion.getAsInt());
                MultiLayerStorage multiLayerStorage = new MultiLayerStorage(storage, blockStorage);
                setLayerStorage.accept(multiLayerStorage);
                return blockStorage;
            default:
                throw new IndexOutOfBoundsException("Invalid layer: " + layer);
        }

        BlockStorage blockStorage = this.storage;
        if (blockStorage != ImmutableBlockStorage.EMPTY) {
            return blockStorage;
        }
        blockStorage = createBlockStorage(contentVersion.getAsInt());
        storage = blockStorage;
        return blockStorage;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    @Override
    public BlockStorage getStorageOrNull(int layer) {
        if (layer != 0) {
            return null;
        }
        BlockStorage blockStorage = this.storage;
        return blockStorage != ImmutableBlockStorage.EMPTY ? blockStorage : null;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void delayPaletteUpdates() {
        storage.delayPaletteUpdates();
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void writeTo(BinaryStream stream) {
        stream.putByte((byte) ChunkSection.STREAM_STORAGE_VERSION);
        BlockStorage blockStorage = this.storage;

        if (blockStorage == ImmutableBlockStorage.EMPTY) {
            stream.putByte((byte) 0);
            return;
        }

        stream.putByte((byte) 1);
        blockStorage.writeTo(stream);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int size() {
        return storage == ImmutableBlockStorage.EMPTY? 0 : 1;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void compress(Consumer<LayerStorage> setLayerStorage) {
        BlockStorage blockStorage = this.storage;
        if (blockStorage == ImmutableBlockStorage.EMPTY) {
            setLayerStorage.accept(EMPTY);
            return;
        }
        blockStorage.recheckBlocks();
        if (!blockStorage.hasBlockIds()) {
            setLayerStorage.accept(EMPTY);
        }
    }
}
