package com.nukkitx.server.level.util;

import com.google.common.base.Preconditions;
import com.nukkitx.server.network.util.VarInts;
import gnu.trove.list.array.TIntArrayList;
import io.netty.buffer.ByteBuf;

import java.util.Arrays;

public class BlockStorage {
    private static final int SECTION_SIZE = 4096;
    private final TIntArrayList ids = new TIntArrayList(16, -1);
    private final short[] indexes;

    public BlockStorage(short[] indexes, int[] ids) {
        Preconditions.checkArgument(indexes.length == SECTION_SIZE);
        Preconditions.checkArgument(ids.length >= 1 && ids[0] == 0);
        this.indexes = indexes;
        this.ids.addAll(ids);
    }

    public BlockStorage() {
        indexes = new short[SECTION_SIZE];
        ids.add(0);
    }

    public int getBlockId(int xyz) {
        return ids.get(indexes[xyz]);
    }

    public void setBlockId(int xyz, int id) {
        short oldIndex = indexes[xyz];
        int index;
        if ((index = ids.indexOf(id)) == -1) {
            ids.add(id);
            index = ids.lastIndexOf(id);
        }
        indexes[xyz] = (short) index;
        checkForUnused(oldIndex);
    }

    public void replaceBlockIds(int oldId, int newId) {
        short oldIndex;
        if ((oldIndex = (short) ids.indexOf(oldId)) == -1) {
            return;
        }
        short newIndex;
        if ((newIndex = (short) ids.indexOf(newId)) == -1) {
            // new runtime id doesn't exist so we can just change the runtimeId at the old index position.
            ids.set(oldIndex, newId);
        } else {
            // new runtime id already exists so we need to loop through all the blocks
            for (int i = 0; i < SECTION_SIZE; i++) {
                if (indexes[i] == oldIndex) {
                    indexes[i] = newIndex;
                }
            }
        }
    }

    public boolean isEmpty() {
        return ids.size() == 1;
    }

    private void checkForUnused(short index) {
        if (index == 0) {
            // Air is always zero.
            return;
        }

        for (int i = 0; i < SECTION_SIZE; i++) {
            if (indexes[i] == index) {
                return;
            }
        }
        ids.remove(index);
        // Move values back one
        for (int i = 0; i < indexes.length; i++) {
            short value = indexes[i];
            if (value <= index) {
                continue;
            }

            indexes[i] = --value;
        }
    }

    public int[] getIds() {
        return ids.toArray();
    }

    public BlockStorage copy() {
        return new BlockStorage(Arrays.copyOf(indexes, indexes.length), ids.toArray());
    }

    public void writeTo(ByteBuf buf) {
        int[] ids = this.ids.toArray();
        int blocksPerWord = Palette.getBlocksPerWord(ids.length);
        Palette palette = new Palette(blocksPerWord, false);
        int paletteInfo = Palette.getPalletInfo(palette.getVersion(), true);
        buf.writeByte(paletteInfo);
        palette.writeIndexes(buf, indexes);

        VarInts.writeInt(buf, ids.length);
        for (int runtimeId : ids) {
            VarInts.writeInt(buf, runtimeId);
        }
    }
}
