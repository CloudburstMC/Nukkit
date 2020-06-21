package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import com.google.common.io.ByteStreams;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

public class GlobalBlockPalette {

    public static final byte[] BLOCK_PALETTE;

    private static final Int2IntMap legacyToRuntimeId = new Int2IntOpenHashMap();

    private static final Int2IntMap runtimeIdToLegacy = new Int2IntOpenHashMap();

    private static final AtomicInteger runtimeIdAllocator = new AtomicInteger(0);

    static {
        GlobalBlockPalette.legacyToRuntimeId.defaultReturnValue(-1);
        GlobalBlockPalette.runtimeIdToLegacy.defaultReturnValue(-1);

        final ListTag<CompoundTag> tag;
        try (final InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_block_states.dat")) {
            if (stream == null) {
                throw new AssertionError("Unable to locate block state nbt");
            }
            //noinspection unchecked
            tag = (ListTag<CompoundTag>) NBTIO.readTag(new ByteArrayInputStream(ByteStreams.toByteArray(stream)), ByteOrder.LITTLE_ENDIAN, false);
        } catch (final IOException e) {
            throw new AssertionError("Unable to load block palette", e);
        }

        for (final CompoundTag state : tag.getAll()) {
            final int runtimeId = GlobalBlockPalette.runtimeIdAllocator.getAndIncrement();
            if (!state.contains("LegacyStates")) {
                continue;
            }

            final List<CompoundTag> legacyStates = state.getList("LegacyStates", CompoundTag.class).getAll();

            // Resolve to first legacy id
            final CompoundTag firstState = legacyStates.get(0);
            GlobalBlockPalette.runtimeIdToLegacy.put(runtimeId, firstState.getInt("id") << 6 | firstState.getShort("val"));

            for (final CompoundTag legacyState : legacyStates) {
                final int legacyId = legacyState.getInt("id") << 6 | legacyState.getShort("val");
                GlobalBlockPalette.legacyToRuntimeId.put(legacyId, runtimeId);
            }
            state.remove("meta"); // No point in sending this since the client doesn't use it.
        }

        try {
            BLOCK_PALETTE = NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN, true);
        } catch (final IOException e) {
            throw new AssertionError("Unable to write block palette", e);
        }
    }

    public static int getOrCreateRuntimeId(final int id, final int meta) {
        final int legacyId = id << 6 | meta;
        int runtimeId = GlobalBlockPalette.legacyToRuntimeId.get(legacyId);
        if (runtimeId == -1) {
            runtimeId = GlobalBlockPalette.legacyToRuntimeId.get(id << 6);
            if (runtimeId == -1) {
                throw new NoSuchElementException("Unmapped block registered id:" + id + " meta:" + meta);
            }
        }
        return runtimeId;
    }

    public static int getOrCreateRuntimeId(final int legacyId) throws NoSuchElementException {
        return GlobalBlockPalette.getOrCreateRuntimeId(legacyId >> 4, legacyId & 0xf);
    }

}
