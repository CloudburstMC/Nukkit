package cn.nukkit.level.format.leveldb.structure;

import cn.nukkit.block.Block;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.generic.BaseChunk;
import cn.nukkit.level.util.BitArrayVersion;
import cn.nukkit.level.util.PalettedBlockStorage;
import cn.nukkit.nbt.tag.CompoundTag;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LevelDBChunk extends BaseChunk {

    @Getter
    @Setter
    private int state = ChunkBuilder.STATE_NEW;

    private PalettedBlockStorage[] biomes3d;

    private final Lock writeLock = new ReentrantLock();

    private final DimensionData dimensionData;

    public LevelDBChunk(Class<? extends LevelProvider> providerClass) {
        this(null, new LevelDBChunkSection[0], null, null, null);
        this.providerClass = providerClass;
    }

    public LevelDBChunk(LevelProvider provider, LevelDBChunkSection[] sections) {
        this(provider, sections, null, null, null);
    }

    public LevelDBChunk(LevelProvider provider, LevelDBChunkSection[] sections, Int2IntMap extraData, byte[] biomes, int[] heightMap) {
        this.provider = provider;
        if (provider != null) {
            this.providerClass = provider.getClass();
        }

        this.dimensionData = provider == null ? DimensionData.LEGACY_DIMENSION : provider.getLevel().getDimensionData();
        int offset = dimensionData.getSectionOffset();
        int lowestSection = dimensionData.getMinHeight() >> 4;
        int highestSection = dimensionData.getMaxHeight() >> 4;

        this.sections = new ChunkSection[dimensionData.getHeight() >> 4];
        for (int y = lowestSection; y <= highestSection; y++) {
            int index = y + offset;
            if (index < sections.length && sections[index] != null) {
                this.sections[index] = sections[index];
            } else {
                this.sections[index] = new LevelDBChunkSection(y);
            }
        }

        if (extraData != null && !extraData.isEmpty()) {
            this.extraData = extraData;
        }

        if (biomes == null) {
            int biomePalettes = this.provider == null ? this.sections.length : dimensionData.getHeight() >> 4;
            this.biomes3d = new PalettedBlockStorage[biomePalettes];
            this.biomes = new byte[256];
        } else {
            this.biomes = biomes;
        }

        this.heightMap = new byte[256];
        if (heightMap == null || heightMap.length != 256) {
            Arrays.fill(this.heightMap, (byte) 255);
        } else {
            for (int i = 0; i < heightMap.length; i++) {
                this.heightMap[i] = (byte) heightMap[i];
            }
        }
    }

    public void setNbtBlockEntities(List<CompoundTag> blockEntities) {
        this.NBTtiles = blockEntities;
    }

    public void setNbtEntities(List<CompoundTag> entities) {
        this.NBTentities = entities;
    }

    public void setBiomes3d(PalettedBlockStorage[] biomes3d) {
        this.biomes3d = biomes3d;
    }

    public void setBiomes3d(int y, PalettedBlockStorage biomes3d) {
        if (!this.has3dBiomes()) {
            this.convertBiomesTo3d(this.biomes);
        }

        int index = y + this.getSectionOffset();
        if (index >= this.biomes3d.length) {
            index = 0;
        }

        this.biomes3d[index] = biomes3d;
    }

    @Override
    public void setBiomeIdArray(byte[] biomeIdArray) {
        super.setBiomeIdArray(biomeIdArray);
        if (this.has3dBiomes()) {
            this.convertBiomesTo3d(biomeIdArray);
        }
    }

    @Override
    public boolean has3dBiomes() {
        return this.biomes3d != null && this.biomes3d.length > 0;
    }

    @Override
    public PalettedBlockStorage getBiomeStorage(int y) {
        if (!this.has3dBiomes()) {
            throw new IllegalStateException("Chunk does not have 3D biomes");
        }

        int index = y + this.getSectionOffset();
        if (index >= this.biomes3d.length) {
            index = 0; // TODO: log
        }

        if (this.biomes3d[index] == null) {
            for (int i = index; i >= 0 ; i--) {
                if (this.biomes3d[i] != null) {
                    this.biomes3d[index] = this.biomes3d[i].copy();
                    break;
                }
            }

            if (this.biomes3d[index] == null) {
                this.biomes3d[index] = PalettedBlockStorage.createWithDefaultState(BitArrayVersion.V0, 0);
            }
        }
        return this.biomes3d[index];
    }

    private void convertBiomesTo3d(byte[] biomeIdArray) {
        PalettedBlockStorage biomesStorage = PalettedBlockStorage.createWithDefaultState(BitArrayVersion.V0, Biome.getBiomeIdOrCorrect(biomeIdArray[0] & 0xFF));
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int biomeId = biomeIdArray[(x << 4) | z] & 0xFF;
                for (int y = 0; y < 16; y++) {
                    biomesStorage.setBlock(x, y, z, biomeId);
                }
            }
        }

        int biomePalettes = this.provider == null ? this.sections.length : provider.getLevel().getDimensionData().getHeight() >> 4;
        PalettedBlockStorage[] biomes = new PalettedBlockStorage[biomePalettes];

        for (int i = 0; i < biomePalettes; i++) {
            biomes[i] = biomesStorage.copy();
        }
        this.biomes3d = biomes;
        this.setChanged();
    }

    @Override
    public int getSectionOffset() {
        return this.dimensionData.getSectionOffset();
    }

    @Override
    public boolean isGenerated() {
        return this.getState() >= ChunkBuilder.STATE_GENERATED;
    }

    @Override
    public void setGenerated() {
        this.setGenerated(true);
    }

    @Override
    public void setGenerated(boolean value) {
        if (this.isGenerated() == value) {
            return;
        }
        this.setChanged();

        if (value) {
            this.setState(Math.max(this.getState(), ChunkBuilder.STATE_GENERATED));
        } else {
            this.setState(ChunkBuilder.STATE_NEW);
        }
    }

    @Override
    public boolean isPopulated() {
        return this.state >= ChunkBuilder.STATE_POPULATED;
    }

    @Override
    public void setPopulated() {
        this.setPopulated(true);
    }

    @Override
    public void setPopulated(boolean value) {
        if (this.isPopulated() == value) {
            return;
        }
        this.setChanged();

        if (value) {
            this.setState(Math.max(this.getState(), ChunkBuilder.STATE_POPULATED));
        } else {
            this.setState(Math.max(this.getState(), ChunkBuilder.STATE_NEW));
        }
    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        ChunkSection section0 = this.getSection(y >> 4);
        if (!(section0 instanceof LevelDBChunkSection)) {
            return section0.getBlockSkyLight(x, y & 0x0f, z);
        }

        LevelDBChunkSection section = (LevelDBChunkSection) section0;
        if (section.skyLight != null) {
            return section.getBlockSkyLight(x, y & 0x0f, z);
        } else if (!section.hasSkyLight) {
            return 0;
        } else {
            int height = this.getHighestBlockAt(x, z);
            if (height < y) {
                return 15;
            } else if (height == y) {
                return Block.isBlockTransparentById(this.getBlockId(x, y, z)) ? 15 : 0;
            } else {
                return section.getBlockSkyLight(x, y & 0x0f, z);
            }
        }
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        ChunkSection section0 = this.getSection(y >> 4);
        if (!(section0 instanceof LevelDBChunkSection)) {
            return section0.getBlockLight(x, y & 0x0f, z);
        }

        LevelDBChunkSection section = (LevelDBChunkSection) section0;
        if (section.blockLight != null) {
            return section.getBlockLight(x, y & 0x0f, z);
        } else if (!section.hasBlockLight) {
            return 0;
        } else {
            return section.getBlockLight(x, y & 0x0f, z);
        }
    }

    @Override
    public boolean compress() {
        boolean result = super.compress();
        for (ChunkSection section : this.getSections()) {
            if (section instanceof LevelDBChunkSection && !section.isEmpty()) {
                result |= ((LevelDBChunkSection) section).compress();
            }
        }
        return result;
    }

    @Override
    @Deprecated
    public byte[] toFastBinary() {
        return new byte[0];
    }

    @Override
    @Deprecated
    public byte[] toBinary() {
        return new byte[0];
    }

    @Override
    public LevelDBChunk clone() {
        LevelDBChunk chunk = (LevelDBChunk) super.clone();
        if (this.has3dBiomes()) {
            PalettedBlockStorage[] biomes = new PalettedBlockStorage[this.biomes3d.length];
            for (int i = 0; i < this.biomes3d.length; i++) {
                biomes[i] = this.biomes3d[i].copy();
            }
            chunk.setBiomes3d(biomes);
        }
        return chunk;
    }

    @Override
    public LevelDBChunk cloneForChunkSending() {
        LevelDBChunk chunk = (LevelDBChunk) super.cloneForChunkSending();
        if (this.has3dBiomes()) {
            PalettedBlockStorage[] biomes = new PalettedBlockStorage[this.biomes3d.length];
            for (int i = 0; i < this.biomes3d.length; i++) {
                PalettedBlockStorage storage = this.biomes3d[i];
                if (storage != null) {
                    biomes[i] = storage.copy();
                }
            }
            chunk.setBiomes3d(biomes);
        }
        return chunk;
    }

    @Override
    public int getBiomeId(int x, int z) {
        return this.getBiomeId(x, 0, z);
    }

    @Override
    public int getBiomeId(int x, int y, int z) {
        if (this.has3dBiomes()) {
            return this.getBiomeStorage(y >> 4).getBlock(x, y & 0x0f, z);
        } else {
            return super.getBiomeId(x, z);
        }
    }

    @Override
    public void setBiomeId(int x, int z, byte biomeId) {
        this.setBiomeId(x, z, (int) biomeId);
    }

    @Override
    public void setBiomeId(int x, int y, int z, byte biomeId) {
        this.setBiomeId(x, y, z, ((int) biomeId) & 0xff);
    }

    @Override
    public void setBiomeId(int x, int z, int biomeId) {
        for (int i = 0; i < this.sections.length; i++) {
            int y = (i - this.getSectionOffset()) << 4;
            for (int yy = 0; yy < 16; yy++) {
                this.setBiomeId(x, y + yy, z, biomeId);
            }
        }
    }

    @Override
    public void setBiomeId(int x, int y, int z, int biomeId) {
        if (!this.has3dBiomes()) {
            this.convertBiomesTo3d(this.biomes);
        }

        this.getBiomeStorage(y >> 4).setBlock(x, y & 0x0f, z, biomeId);
        this.setChanged();
    }

    public Lock writeLock() {
        return this.writeLock;
    }
}
