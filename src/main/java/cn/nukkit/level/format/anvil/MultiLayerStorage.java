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
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

/**
 * @author joserobjr
 * @since 2020-10-02
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public final class MultiLayerStorage extends LayerStorage {
    private BlockStorage[] storages;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public MultiLayerStorage() {
        storages = BlockStorage.EMPTY_ARRAY;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public MultiLayerStorage(BlockStorage... storages) {
        this.storages = storages;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void compress(Consumer<LayerStorage> setLayerStorage) {
        // Remove unused storage layers
        int newSize = storages.length;
        for (int i = storages.length - 1; i >= 0; i--) {
            BlockStorage storage = storages[i];
            if (storage == null || storage == ImmutableBlockStorage.EMPTY) {
                newSize--;
            } else if (storage.hasBlockIds()) {
                storage.recheckBlocks();
                if (storage.hasBlockIds()) {
                    break;
                } else {
                    newSize--;
                }
            } else {
                newSize--;
            }
        }
        if (newSize == 0) {
            setLayerStorage.accept(EMPTY);
        } else if (newSize == 1) {
            setLayerStorage.accept(new SingleLayerStorage(storages[0]));
        } else if (newSize != storages.length) {
            storages = Arrays.copyOf(storages, newSize);
        }
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int size() {
        return storages.length;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void writeTo(BinaryStream stream) {
        stream.putByte((byte) ChunkSection.STREAM_STORAGE_VERSION);
        stream.putByte((byte) storages.length);
        for (BlockStorage blockStorage : storages) {
            blockStorage.writeTo(stream);
        }
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void delayPaletteUpdates() {
        for (BlockStorage storage : storages) {
            storage.delayPaletteUpdates();
        }
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public BlockStorage getOrSetStorage(@Nullable Consumer<LayerStorage> setLayerStorage, IntSupplier contentVersion, int layer) {
        int oldLen = storages.length;
        if (layer >= oldLen) {
            if (layer > 1) {
                throw new IndexOutOfBoundsException("Only layer 0 and 1 are supported. Attempted: " + layer);
            }
            storages = Arrays.copyOf(storages, layer + 1);
            Arrays.fill(storages, oldLen, layer, ImmutableBlockStorage.EMPTY);
            BlockStorage storage = createBlockStorage(contentVersion.getAsInt());
            storages[layer] = storage;
            return storage;
        } else if (layer < 0) {
            throw new IndexOutOfBoundsException("Only layer 0 and 1 are supported. Attempted: " + layer);
        }

        BlockStorage storage = storages[layer];
        if (storage != ImmutableBlockStorage.EMPTY) {
            return storage;
        }
        storage = createBlockStorage(contentVersion.getAsInt());
        storages[layer] = storage;
        return storage;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public BlockStorage getStorageOrEmpty(int layer) {
        return layer < storages.length ? storages[layer] : ImmutableBlockStorage.EMPTY;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    @Override
    public BlockStorage getStorageOrNull(int layer) {
        if (layer >= storages.length) {
            return null;
        }

        BlockStorage storage = storages[layer];
        return storage == ImmutableBlockStorage.EMPTY ? null : storage;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean hasBlocks() {
        for (BlockStorage storage : storages) {
            if (storage.hasBlockIds()) {
                return true;
            }
        }
        return false;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @SneakyThrows(CloneNotSupportedException.class)
    @Override
    public MultiLayerStorage clone() {
        MultiLayerStorage clone = (MultiLayerStorage) super.clone();
        clone.storages = clone.storages.clone();
        for (int i = 0; i < clone.storages.length; i++) {
            clone.storages[i] = clone.storages[i].copy();
        }
        return clone;
    }
}
