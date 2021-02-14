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
import cn.nukkit.level.format.updater.ChunkUpdater;
import cn.nukkit.utils.BinaryStream;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

/**
 * @author joserobjr
 * @since 2020-10-02
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class LayerStorage implements Cloneable {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final LayerStorage EMPTY = new LayerStorage();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected LayerStorage() {

    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean hasBlocks() {
        return false;
    }

    @Override
    public LayerStorage clone() throws CloneNotSupportedException {
        if (getClass() == LayerStorage.class) {
            return this;
        }
        return (LayerStorage) super.clone();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockStorage getStorageOrEmpty(int layer) {
        return ImmutableBlockStorage.EMPTY;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockStorage getOrSetStorage(Consumer<LayerStorage> setLayerStorage, IntSupplier contentVersion, int layer) {
        LayerStorage populatedLayerStorage = layer == 0? new SingleLayerStorage() : new MultiLayerStorage();
        setLayerStorage.accept(populatedLayerStorage);
        return populatedLayerStorage.getOrSetStorage(setLayerStorage, contentVersion, layer);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public BlockStorage getStorageOrNull(int layer) {
        return null;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void delayPaletteUpdates() {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void writeTo(BinaryStream stream) {
        stream.putByte((byte) ChunkSection.STREAM_STORAGE_VERSION);
        stream.putByte((byte) 0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int size() {
        return 0;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void compress(Consumer<LayerStorage> setLayerStorage) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected BlockStorage createBlockStorage(int contentVersion) {
        BlockStorage storage = new BlockStorage();
        if (contentVersion < ChunkUpdater.getCurrentContentVersion()) {
            storage.delayPaletteUpdates();
        }
        return storage;
    }
}
