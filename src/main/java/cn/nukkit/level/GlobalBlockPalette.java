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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

public class GlobalBlockPalette {
    private static final Int2IntMap legacyToRuntimeId = new Int2IntOpenHashMap();
    private static final Int2IntMap runtimeIdToLegacy = new Int2IntOpenHashMap();
    private static final AtomicInteger runtimeIdAllocator = new AtomicInteger(0);
    public static final byte[] BLOCK_PALETTE;

    static {
        legacyToRuntimeId.defaultReturnValue(-1);
        runtimeIdToLegacy.defaultReturnValue(-1);
    
        Map<CompoundTag, int[]> metaOverrides = new LinkedHashMap<>();
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_block_states_overrides.dat")) {
    
            ListTag<CompoundTag> states = NBTIO.read(stream).getList("Overrides", CompoundTag.class);
            for (CompoundTag override : states.getAll()) {
                if (override.contains("block") && override.contains("meta")) {
                    metaOverrides.put(override.getCompound("block").remove("version"), override.getIntArray("meta"));
                }
            }
    
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    
        ListTag<CompoundTag> tag;
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_block_states.dat")) {
            if (stream == null) {
                throw new AssertionError("Unable to locate block state nbt");
            }
            
            //noinspection UnstableApiUsage
            byte[] blockPalette = ByteStreams.toByteArray(stream);
            //noinspection unchecked
            tag = (ListTag<CompoundTag>) NBTIO.readNetwork(new ByteArrayInputStream(blockPalette));
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        for (CompoundTag state : tag.getAll()) {
            int runtimeId = runtimeIdAllocator.getAndIncrement();
            int[] meta = metaOverrides.get(state.getCompound("block").copy().remove("version"));
            if (meta == null) {
                if (!state.contains("meta")) {
                    continue;
                } else {
                    meta = state.getIntArray("meta");
                }
            }

            int id = state.getShort("id");

            // Resolve to first legacy id
            runtimeIdToLegacy.put(runtimeId, id << 6 | meta[0]);
            for (int val : meta) {
                int legacyId = id << 6 | val;
                legacyToRuntimeId.put(legacyId, runtimeId);
            }
            state.remove("meta"); // No point in sending this since the client doesn't use it.
        }
    
        try {
            BLOCK_PALETTE = NBTIO.writeNetwork(tag);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static int getOrCreateRuntimeId(int id, int meta) {
        int legacyId = id << 6 | meta;
        int runtimeId = legacyToRuntimeId.get(legacyId);
        if (runtimeId == -1) {
            //runtimeId = registerMapping(runtimeIdAllocator.incrementAndGet(), legacyId);
            runtimeId = legacyToRuntimeId.get(248 << 6);
            //throw new NoSuchElementException("Unmapped block registered id:" + id + " meta:" + meta);
        }
        return runtimeId;
    }

    public static int getOrCreateRuntimeId(int legacyId) throws NoSuchElementException {
        return getOrCreateRuntimeId(legacyId >> 4, legacyId & 0xf);
    }
}
