package cn.nukkit.level.format.generic;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.anvil.Anvil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * author: MagicDroidX
 * Nukkit Project
 */

public abstract class BaseChunk extends BaseFullChunk implements Chunk {

    private static final byte[] EMPTY_ID_ARRAY = new byte[4096];
    private static final byte[] EMPTY_DATA_ARRAY = new byte[2048];

    protected ChunkSection[] sections;
    private final Anvil anvil;

    public BaseChunk(Anvil provider) {
        this.anvil = provider;
    }

    @Override
    public BaseChunk clone() {
        BaseChunk chunk = (BaseChunk) super.clone();
        if (this.biomes != null) chunk.biomes = this.biomes.clone();
        chunk.heightMap = this.getHeightMapArray().clone();
        if (sections != null && sections[0] != null) {
            chunk.sections = new ChunkSection[sections.length];
            for (int i = 0; i < sections.length; i++) {
                chunk.sections[i] = sections[i].copy();
            }
        }
        return chunk;
    }

    private void removeInvalidTile(int x, int y, int z) {
        BlockEntity entity = getTile(x, y, z);
        if (entity != null && !entity.isBlockEntityValid()) {
            removeBlockEntity(entity);
        }
    }

    @Override
    public int getFullBlock(int x, int y, int z) {
        return this.sections[y >> 4].getFullBlock(x, y & 0x0f, z);
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId) {
        return this.setBlock(x, y, z, blockId, 0);
    }

    @Override
    public Block getAndSetBlock(int x, int y, int z, Block block) {
        int Y = y >> 4;
        setChanged();

        checkSection(Y);

        try {
            return this.sections[Y].getAndSetBlock(x, y & 0x0f, z, block);
        } finally {
            removeInvalidTile(x, y, z);
        }
    }

    @Override
    public boolean setFullBlockId(int x, int y, int z, int fullId) {
        int Y = y >> 4;
        setChanged();

        checkSection(Y);

        try {
            return this.sections[Y].setFullBlockId(x, y & 0x0f, z, fullId);
        } finally {
            removeInvalidTile(x, y, z);
        }
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId, int meta) {
        int Y = y >> 4;
        setChanged();

        checkSection(Y);

        try {
            return this.sections[Y].setBlock(x, y & 0x0f, z, blockId, meta);
        } finally {
            removeInvalidTile(x, y, z);
        }
    }

    @Override
    public void setBlockId(int x, int y, int z, int id) {
        int Y = y >> 4;
        setChanged();

        checkSection(Y);

        this.sections[Y].setBlockId(x, y & 0x0f, z, id);
        removeInvalidTile(x, y, z);
    }

    @Override
    public int getBlockId(int x, int y, int z) {
        return this.sections[y >> 4].getBlockId(x, y & 0x0f, z);
    }

    @Override
    public int getBlockData(int x, int y, int z) {
        return this.sections[y >> 4].getBlockData(x, y & 0x0f, z);
    }

    @Override
    public void setBlockData(int x, int y, int z, int data) {
        int Y = y >> 4;
        setChanged();

        checkSection(Y);

        this.sections[Y].setBlockData(x, y & 0x0f, z, data);
        removeInvalidTile(x, y, z);
    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        return this.sections[y >> 4].getBlockSkyLight(x, y & 0x0f, z);
    }

    @Override
    public void setBlockSkyLight(int x, int y, int z, int level) {
        int Y = y >> 4;
        setChanged();

        checkSection(Y);

        this.sections[Y].setBlockSkyLight(x, y & 0x0f, z, level);
        removeInvalidTile(x, y, z);
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        return this.sections[y >> 4].getBlockLight(x, y & 0x0f, z);
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        int Y = y >> 4;
        setChanged();

        checkSection(Y);

        this.sections[Y].setBlockLight(x, y & 0x0f, z, level);
        removeInvalidTile(x, y, z);
    }

    @Override
    public boolean isSectionEmpty(float fY) {
        return this.sections[(int) fY] instanceof EmptyChunkSection;
    }

    @Override
    public ChunkSection getSection(float fY) {
        return this.sections[(int) fY];
    }

    @Override
    public boolean setSection(float fY, ChunkSection section) {
        if (Arrays.equals(EMPTY_ID_ARRAY, section.getIdArray()) && Arrays.equals(EMPTY_DATA_ARRAY, section.getDataArray())) {
            this.sections[(int) fY] = EmptyChunkSection.EMPTY[(int) fY];
        } else {
            this.sections[(int) fY] = section;
        }
        setChanged();
        return true;
    }

    private void setInternalSection(float fY, ChunkSection section) {
        this.sections[(int) fY] = section;
        setChanged();
    }

    @Override
    public boolean load() throws IOException {
        return this.load(true);
    }

    @Override
    public boolean load(boolean generate) throws IOException {
        return this.getProvider() != null && this.getProvider().getChunk(this.getX(), this.getZ(), true) != null;
    }

    @Override
    public byte[] getBlockIdArray() {
        ByteBuffer buffer = ByteBuffer.allocate(4096 * SECTION_COUNT);
        for (int y = 0; y < SECTION_COUNT; y++) {
            buffer.put(this.sections[y].getIdArray());
        }
        return buffer.array();
    }

    @Override
    public byte[] getBlockDataArray() {
        ByteBuffer buffer = ByteBuffer.allocate(2048 * SECTION_COUNT);
        for (int y = 0; y < SECTION_COUNT; y++) {
            buffer.put(this.sections[y].getDataArray());
        }
        return buffer.array();
    }

    @Override
    public byte[] getBlockSkyLightArray() {
        ByteBuffer buffer = ByteBuffer.allocate(2048 * SECTION_COUNT);
        for (int y = 0; y < SECTION_COUNT; y++) {
            buffer.put(this.sections[y].getSkyLightArray());
        }
        return buffer.array();
    }

    @Override
    public byte[] getBlockLightArray() {
        ByteBuffer buffer = ByteBuffer.allocate(2048 * SECTION_COUNT);
        for (int y = 0; y < SECTION_COUNT; y++) {
            buffer.put(this.sections[y].getLightArray());
        }
        return buffer.array();
    }

    @Override
    public ChunkSection[] getSections() {
        return sections;
    }

    @Override
    public byte[] getHeightMapArray() {
        return this.heightMap;
    }

    @Override
    public LevelProvider getProvider() {
        return this.provider;
    }

    private void checkSection(int y) {
        if (this.sections[y] instanceof EmptyChunkSection) {
            this.sections[y] = anvil.createChunkSection(y);
        }
    }
}
