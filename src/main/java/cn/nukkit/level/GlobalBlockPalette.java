package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIce;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import com.google.common.collect.HashBiMap;
import com.google.common.io.ByteStreams;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public class GlobalBlockPalette {
    private static final Int2ObjectMap<Block> runtimeToState = new Int2ObjectOpenHashMap<>();
    private static final Int2IntMap stateToRuntime = new Int2IntOpenHashMap();
    private static final Int2IntMap runtimeIdToLegacy = new Int2IntOpenHashMap();
    private static final AtomicInteger runtimeIdAllocator = new AtomicInteger(0);
    private static final HashBiMap<String, Integer> nameToLegacyId = HashBiMap.create();
    public static final byte[] BLOCK_PALETTE;

    static {
        runtimeIdToLegacy.defaultReturnValue(-1);
        stateToRuntime.defaultReturnValue(-1);

        InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_block_states.dat");
        if (stream == null) {
            throw new AssertionError("Unable to locate block state nbt");
        }
        ListTag<CompoundTag> tag;
        try {
            //noinspection UnstableApiUsage
            BLOCK_PALETTE = ByteStreams.toByteArray(stream);
            //noinspection unchecked
            tag = (ListTag<CompoundTag>) NBTIO.readNetwork(new ByteArrayInputStream(BLOCK_PALETTE));
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        for (CompoundTag state : tag.getAll()) {
            int runtimeId = runtimeIdAllocator.getAndIncrement();

            if (!state.contains("meta")) continue;

            String name = state.getCompound("block").getString("name");
            int id = state.getShort("id");
            nameToLegacyId.putIfAbsent(name, id);
            int[] meta = state.getIntArray("meta");

            for (int i = 0; i < meta.length; i++) {
                int val = meta[i];

                if (val < 15) {
                    int legacyId = id << 4 | val;
                    runtimeIdToLegacy.put(runtimeId, legacyId);
                }

                Block block = registerBlockState(runtimeId, id, val);
                if (i == 0) {
                    runtimeToState.put(runtimeId, block);
                }
            }

            state.remove("meta"); // No point in sending this since the client doesn't use it.
        }
    }

    public static int getRuntimeId(int legacyId) {
        return getRuntimeId(legacyId >> 4, legacyId & 0xf);
    }

    public static int getRuntimeId(Block block) {
        return getRuntimeId(block.getId(), block.getDamage());
    }

    public static int getRuntimeId(int id, int meta) {
        int fullId = (id << 8) | meta;
        int runtimeId = stateToRuntime.get(fullId);
        if (runtimeId == -1) {
            throw new NoSuchElementException("No runtime ID for block " + id + ":" + meta);
        }
        return runtimeId;
    }

    public static Block getBlock(int id, int meta) {
        return getBlock(getRuntimeId(id, meta));
    }

    public static Block getBlock(int runtimeId) {
        Block block = runtimeToState.get(runtimeId);
        if (block == null) {
            throw new NoSuchElementException("No block for runtime ID registered");
        }
        return block;
    }

    public static int getLegacyIdFromName(String name) {
        //noinspection ConstantConditions
        return nameToLegacyId.get(name);
    }

    public static String getNameFromLegacyId(int id) {
        return nameToLegacyId.inverse().get(id);
    }

    private static Block registerBlockState(int runtimeId, int id, int meta) {
        Block block = createBlock(id, meta);

        stateToRuntime.put(id << 8 | meta, runtimeId);

        if (meta == 0) {
            registerBlock(meta, block);
        }

        if (block == null) {
            block = new BlockUnknown(id, meta);
            log.debug("Unable to find block state for {}:{}", id, meta);
        }
        return block;
    }

    private static void registerBlock(int id, Block block) {
        int[] lightFilter = Block.lightFilter;

        if (block != null) {
            Block.solid[id] = block.isSolid();
            Block.transparent[id] = block.isTransparent();
            Block.hardness[id] = block.getHardness();
            Block.light[id] = block.getLightLevel();

            if (block.isSolid()) {
                if (block.isTransparent()) {
                    if (block instanceof BlockLiquid || block instanceof BlockIce) {
                        lightFilter[id] = 2;
                    } else {
                        lightFilter[id] = 1;
                    }
                } else {
                    lightFilter[id] = 15;
                }
            } else {
                lightFilter[id] = 1;
            }
        } else {
            lightFilter[id] = 1;
        }
    }

    private static Block createBlock(int id, int meta) {
        Class<? extends Block> c = Block.list.get(id);
        try {
            if (c != null) {
                Constructor<? extends Block> constructor = c.getDeclaredConstructor(int.class, int.class);
                constructor.setAccessible(true);
                return constructor.newInstance(id, meta);
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            // ignore
        }
        return null;
    }
}
