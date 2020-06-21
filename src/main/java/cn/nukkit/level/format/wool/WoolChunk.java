package cn.nukkit.level.format.wool;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.LevelProviderManager;
import cn.nukkit.level.format.anvil.Anvil;
import cn.nukkit.level.format.anvil.palette.BiomePalette;
import cn.nukkit.level.format.generic.BaseChunk;
import cn.nukkit.level.format.generic.BaseRegionLoader;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.BlockUpdateEntry;
import cn.nukkit.utils.ChunkException;
import cn.nukkit.utils.Zlib;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.ByteOrder;
import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class WoolChunk extends BaseChunk {

    public WoolChunk(LevelProvider provider, int x, int z, ChunkSection[] sections, byte[] heightMap, byte[] biomes, List<CompoundTag> entities, List<CompoundTag> tiles) {
        this.provider = provider;
        this.providerClass = provider.getClass();
        this.x = x;
        this.z = z;
        this.sections = sections;
        this.heightMap = heightMap;
        this.biomes = biomes;
        this.NBTentities = entities;
        this.NBTtiles = tiles;
    }

    public WoolChunk(final Class<? extends LevelProvider> providerClass) {
        this.providerClass = providerClass;

        this.biomes = new byte[256];
        this.sections = new cn.nukkit.level.format.ChunkSection[16];
        System.arraycopy(EmptyChunkSection.EMPTY, 0, this.sections, 0, 16);
    }

    public WoolChunk(final LevelProvider level) {
        this(level != null ? level.getClass() : null);
        this.provider = level;
    }

    public static WoolChunk getEmptyChunk(final int chunkX, final int chunkZ) {
        return WoolChunk.getEmptyChunk(chunkX, chunkZ, null);
    }

    public static WoolChunk getEmptyChunk(final int chunkX, final int chunkZ, final LevelProvider provider) {
        try {
            final WoolChunk chunk;
            if (provider != null) {
                chunk = new WoolChunk(provider);
            } else {
                chunk = new WoolChunk(WoolFormat.class);
            }

            chunk.setPosition(chunkX, chunkZ);

/*            for (int i = 0; i < 16; i++) {
                chunk.setSection(i,new WoolChunkSection(i));
            }*/

            chunk.heightMap = new byte[256];

            return chunk;
        } catch (final Exception e) {
            return null;
        }
    }

    @Override
    public boolean unload() throws Exception {
        return false;
    }

    @Override
    public boolean unload(boolean save) {
        return false;
    }

    @Override
    public boolean unload(boolean save, boolean safe) {
        return false;
    }

    @Override
    public WoolChunk clone() {
        return (WoolChunk) super.clone();
    }

    @Override
    public int getBlockSkyLight(final int x, final int y, final int z) {
        final cn.nukkit.level.format.ChunkSection section = this.sections[y >> 4];
        if (section instanceof WoolChunkSection) {
            final WoolChunkSection anvilSection = (WoolChunkSection) section;
            if (anvilSection.skyLight != null) {
                return section.getBlockSkyLight(x, y & 0x0f, z);
            } else {
                return 0;
            }
        } else {
            return section.getBlockSkyLight(x, y & 0x0f, z);
        }
    }

    @Override
    public int getBlockLight(final int x, final int y, final int z) {
        final cn.nukkit.level.format.ChunkSection section = this.sections[y >> 4];
        if (section instanceof WoolChunkSection) {
            final WoolChunkSection anvilSection = (WoolChunkSection) section;
            if (anvilSection.blockLight != null) {
                return section.getBlockLight(x, y & 0x0f, z);
            } else {
                return 0;
            }
        } else {
            return section.getBlockLight(x, y & 0x0f, z);
        }
    }

    @Override
    public boolean isPopulated() {
        return true;
    }

    @Override
    public void setPopulated(final boolean value) {
    }

    @Override
    public void setPopulated() {
    }

    @Override
    public boolean isGenerated() {
        return true;
    }

    @Override
    public void setGenerated(final boolean value) {
    }

    @Override
    public void setGenerated() {
    }

    @Override
    public byte[] toBinary() {
        try {
            return WoolFormatConverter.serializeChunk(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public byte[] toFastBinary() {
        return toBinary();
    }

}
