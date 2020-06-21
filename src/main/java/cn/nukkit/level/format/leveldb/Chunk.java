package cn.nukkit.level.format.leveldb;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.anvil.palette.BiomePalette;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.leveldb.key.EntitiesKey;
import cn.nukkit.level.format.leveldb.key.ExtraDataKey;
import cn.nukkit.level.format.leveldb.key.TilesKey;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.BinaryStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Chunk extends BaseFullChunk {

    public static final int DATA_LENGTH = 16384 * (2 + 1 + 1 + 1) + 256 + 1024;

    protected boolean isLightPopulated = false;

    protected boolean isPopulated = false;

    protected boolean isGenerated = false;

    public Chunk(final LevelProvider level, final int chunkX, final int chunkZ, final byte[] terrain) {
        this(level, chunkX, chunkZ, terrain, null);
    }

    public Chunk(final Class<? extends LevelProvider> providerClass, final int chunkX, final int chunkZ, final byte[] terrain) {
        this(null, chunkX, chunkZ, terrain, null);
        this.providerClass = providerClass;
    }

    public Chunk(final LevelProvider level, final int chunkX, final int chunkZ, final byte[] terrain, final List<CompoundTag> entityData) {
        this(level, chunkX, chunkZ, terrain, entityData, null);
    }

    public Chunk(final LevelProvider level, final int chunkX, final int chunkZ, final byte[] terrain, final List<CompoundTag> entityData, final List<CompoundTag> tileData) {
        this(level, chunkX, chunkZ, terrain, entityData, tileData, null);
    }

    public Chunk(final LevelProvider level, final int chunkX, final int chunkZ, final byte[] terrain, final List<CompoundTag> entityData, final List<CompoundTag> tileData, final Map<Integer, Integer> extraData) {
        final ByteBuffer buffer = ByteBuffer.wrap(terrain).order(ByteOrder.BIG_ENDIAN);

        final byte[] blocks = new byte[32768];
        buffer.get(blocks);

        final byte[] data = new byte[16384];
        buffer.get(data);

        final byte[] skyLight = new byte[16384];
        buffer.get(skyLight);

        final byte[] blockLight = new byte[16384];
        buffer.get(blockLight);

        final byte[] heightMap = new byte[256];
        for (int i = 0; i < 256; i++) {
            heightMap[i] = buffer.get();
        }

        final int[] biomeColors = new int[256];
        for (int i = 0; i < 256; i++) {
            biomeColors[i] = buffer.getInt();
        }

        this.provider = level;
        if (level != null) {
            this.providerClass = level.getClass();
        }

        this.setPosition(chunkX, chunkZ);

        this.blocks = blocks;
        this.data = data;
        this.skyLight = skyLight;
        this.blockLight = blockLight;

        this.biomes = new byte[16 * 16];
        if (biomeColors.length == 256) {
            final BiomePalette palette = new BiomePalette(biomeColors);
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    this.biomes[x << 4 | z] = (byte) (palette.get(x, z) >> 24);
                }
            }
        }

        if (heightMap.length == 256) {
            this.heightMap = heightMap;
        } else {
            final byte[] bytes = new byte[256];
            Arrays.fill(bytes, (byte) 256);
            this.heightMap = bytes;
        }

        this.NBTentities = entityData;
        this.NBTtiles = tileData;
        this.extraData = extraData;
    }

    public static Chunk fromBinary(final byte[] data) {
        return Chunk.fromBinary(data, null);
    }

    public static Chunk fromBinary(final byte[] data, final LevelProvider provider) {
        try {

            final int chunkX = Binary.readLInt(new byte[]{data[0], data[1], data[2], data[3]});
            final int chunkZ = Binary.readLInt(new byte[]{data[4], data[5], data[6], data[7]});
            final byte[] chunkData = Binary.subBytes(data, 8, data.length - 1);

            final int flags = data[data.length - 1];

            final List<CompoundTag> entities = new ArrayList<>();
            final List<CompoundTag> tiles = new ArrayList<>();

            final Map<Integer, Integer> extraDataMap = new HashMap<>();

            if (provider instanceof LevelDB) {
                final byte[] entityData = ((LevelDB) provider).getDatabase().get(EntitiesKey.create(chunkX, chunkZ).toArray());
                if (entityData != null && entityData.length > 0) {
                    try (final NBTInputStream nbtInputStream = new NBTInputStream(new ByteArrayInputStream(entityData), ByteOrder.LITTLE_ENDIAN)) {
                        while (nbtInputStream.available() > 0) {
                            final Tag tag = Tag.readNamedTag(nbtInputStream);
                            if (!(tag instanceof CompoundTag)) {
                                throw new IOException("Root tag must be a named compound tag");
                            }
                            entities.add((CompoundTag) tag);
                        }
                    }
                }

                final byte[] tileData = ((LevelDB) provider).getDatabase().get(TilesKey.create(chunkX, chunkZ).toArray());
                if (tileData != null && tileData.length > 0) {
                    try (final NBTInputStream nbtInputStream = new NBTInputStream(new ByteArrayInputStream(tileData), ByteOrder.LITTLE_ENDIAN)) {
                        while (nbtInputStream.available() > 0) {
                            final Tag tag = Tag.readNamedTag(nbtInputStream);
                            if (!(tag instanceof CompoundTag)) {
                                throw new IOException("Root tag must be a named compound tag");
                            }
                            tiles.add((CompoundTag) tag);
                        }
                    }
                }

                final byte[] extraData = ((LevelDB) provider).getDatabase().get(ExtraDataKey.create(chunkX, chunkZ).toArray());
                if (extraData != null && extraData.length > 0) {
                    final BinaryStream stream = new BinaryStream(tileData);
                    final int count = stream.getInt();
                    for (int i = 0; i < count; ++i) {
                        final int key = stream.getInt();
                        final int value = stream.getShort();
                        extraDataMap.put(key, value);
                    }
                }

                /*if (!entities.isEmpty() || !blockEntities.isEmpty()) {
                    CompoundTag ct = new CompoundTag();
                    ListTag<CompoundTag> entityList = new ListTag<>("entities");
                    ListTag<CompoundTag> tileList = new ListTag<>("blockEntities");

                    entityList.list = entities;
                    tileList.list = blockEntities;
                    ct.putList(entityList);
                    ct.putList(tileList);
                    NBTIO.write(ct, new File(Nukkit.DATA_PATH + chunkX + "_" + chunkZ + ".dat"));
                }*/

                final Chunk chunk = new Chunk(provider, chunkX, chunkZ, chunkData, entities, tiles, extraDataMap);

                if ((flags & 0x01) > 0) {
                    chunk.setGenerated();
                }

                if ((flags & 0x02) > 0) {
                    chunk.setPopulated();
                }

                if ((flags & 0x04) > 0) {
                    chunk.setLightPopulated();
                }
                return chunk;
            }
        } catch (final Exception e) {
            Server.getInstance().getLogger().logException(e);
        }
        return null;
    }

    public static Chunk fromFastBinary(final byte[] data) {
        return Chunk.fromFastBinary(data, null);
    }

    public static Chunk fromFastBinary(final byte[] data, final LevelProvider provider) {
        return Chunk.fromBinary(data, provider);
    }

    public static Chunk getEmptyChunk(final int chunkX, final int chunkZ) {
        return Chunk.getEmptyChunk(chunkX, chunkZ, null);
    }

    public static Chunk getEmptyChunk(final int chunkX, final int chunkZ, final LevelProvider provider) {
        try {
            final Chunk chunk;
            if (provider != null) {
                chunk = new Chunk(provider, chunkX, chunkZ, new byte[Chunk.DATA_LENGTH]);
            } else {
                chunk = new Chunk(LevelDB.class, chunkX, chunkZ, new byte[Chunk.DATA_LENGTH]);
            }

            final byte[] skyLight = new byte[16384];
            Arrays.fill(skyLight, (byte) 0xff);
            chunk.skyLight = skyLight;
            return chunk;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getFullBlock(final int x, final int y, final int z) {
        final int i = x << 11 | z << 7 | y;
        final int block = this.blocks[i] & 0xff;
        final int data = this.data[i >> 1] & 0xff;
        if ((y & 1) == 0) {
            return block << 4 | data & 0x0f;
        } else {
            return block << 4 | data >> 4;
        }
    }

    @Override
    public Block getAndSetBlock(final int x, final int y, final int z, final Block block) {
        int i = x << 11 | z << 7 | y;
        boolean changed = false;
        final byte id = (byte) block.getId();

        final byte previousId = this.blocks[i];

        if (previousId != id) {
            this.blocks[i] = id;
            changed = true;
        }

        final int previousData;
        i >>= 1;
        final int old = this.data[i] & 0xff;
        if ((y & 1) == 0) {
            previousData = old & 0x0f;
            if (Block.hasMeta[block.getId()]) {
                this.data[i] = (byte) (old & 0xf0 | block.getDamage() & 0x0f);
                if (block.getDamage() != previousData) {
                    changed = true;
                }
            }
        } else {
            previousData = old >> 4;
            if (Block.hasMeta[block.getId()]) {
                this.data[i] = (byte) ((block.getDamage() & 0x0f) << 4 | old & 0x0f);
                if (block.getDamage() != previousData) {
                    changed = true;
                }
            }
        }

        if (changed) {
            this.setChanged();
        }
        return Block.get(previousId, previousData);
    }

    @Override
    public boolean setBlock(final int x, final int y, final int z, final int blockId) {
        return this.setBlock(x, y, z, blockId, 0);
    }

    @Override
    public boolean setBlock(final int x, final int y, final int z, final int blockId, final int meta) {
        int i = x << 11 | z << 7 | y;
        boolean changed = false;
        final byte id = (byte) blockId;
        if (this.blocks[i] != id) {
            this.blocks[i] = id;
            changed = true;
        }

        if (Block.hasMeta[blockId]) {
            i >>= 1;
            final int old = this.data[i] & 0xff;
            if ((y & 1) == 0) {
                this.data[i] = (byte) (old & 0xf0 | meta & 0x0f);
                if ((old & 0x0f) != meta) {
                    changed = true;
                }
            } else {
                this.data[i] = (byte) ((meta & 0x0f) << 4 | old & 0x0f);
                if (meta != old >> 4) {
                    changed = true;
                }
            }
        }

        if (changed) {
            this.setChanged();
        }
        return changed;
    }

    @Override
    public int getBlockId(final int x, final int y, final int z) {
        return this.blocks[x << 11 | z << 7 | y] & 0xff;
    }

    @Override
    public void setBlockId(final int x, final int y, final int z, final int id) {
        this.blocks[x << 11 | z << 7 | y] = (byte) id;
        this.setChanged();
    }

    @Override
    public int getBlockData(final int x, final int y, final int z) {
        final int b = this.data[x << 10 | z << 6 | y >> 1] & 0xff;
        if ((y & 1) == 0) {
            return b & 0x0f;
        } else {
            return b >> 4;
        }
    }

    @Override
    public void setBlockData(final int x, final int y, final int z, final int data) {
        final int i = x << 10 | z << 6 | y >> 1;
        final int old = this.data[i] & 0xff;
        if ((y & 1) == 0) {
            this.data[i] = (byte) (old & 0xf0 | data & 0x0f);
        } else {
            this.data[i] = (byte) ((data & 0x0f) << 4 | old & 0x0f);
        }
        this.setChanged();
    }

    @Override
    public int getBlockSkyLight(final int x, final int y, final int z) {
        final int sl = this.skyLight[x << 10 | z << 6 | y >> 1] & 0xff;
        if ((y & 1) == 0) {
            return sl & 0x0f;
        } else {
            return sl >> 4;
        }
    }

    @Override
    public void setBlockSkyLight(final int x, final int y, final int z, final int level) {
        final int i = x << 10 | z << 6 | y >> 1;
        final int old = this.skyLight[i] & 0xff;
        if ((y & 1) == 0) {
            this.skyLight[i] = (byte) (old & 0xf0 | level & 0x0f);
        } else {
            this.skyLight[i] = (byte) ((level & 0x0f) << 4 | old & 0x0f);
        }
        this.setChanged();
    }

    @Override
    public int getBlockLight(final int x, final int y, final int z) {
        final int b = this.blockLight[x << 10 | z << 6 | y >> 1] & 0xff;
        if ((y & 1) == 0) {
            return b & 0x0f;
        } else {
            return b >> 4;
        }
    }

    @Override
    public void setBlockLight(final int x, final int y, final int z, final int level) {
        final int i = x << 10 | z << 6 | y >> 1;
        final int old = this.blockLight[i] & 0xff;
        if ((y & 1) == 0) {
            this.blockLight[i] = (byte) (old & 0xf0 | level & 0x0f);
        } else {
            this.blockLight[i] = (byte) ((level & 0x0f) << 4 | old & 0x0f);
        }
        this.setChanged();
    }

    @Override
    public boolean isPopulated() {
        return this.isPopulated;
    }

    @Override
    public void setPopulated(final boolean value) {
        this.isPopulated = true;
    }

    @Override
    public void setPopulated() {
        this.setPopulated(true);
    }

    @Override
    public boolean isGenerated() {
        return this.isGenerated;
    }

    @Override
    public void setGenerated(final boolean value) {
        this.isGenerated = true;
    }

    @Override
    public void setGenerated() {
        this.setGenerated(true);
    }

    @Override
    public byte[] toBinary() {
        return this.toBinary(false);
    }

    @Override
    public boolean isLightPopulated() {
        return this.isLightPopulated;
    }

    @Override
    public void setLightPopulated(final boolean value) {
        this.isLightPopulated = value;
    }

    @Override
    public void setLightPopulated() {
        this.setLightPopulated(true);
    }

    @Override
    public byte[] toFastBinary() {
        return this.toBinary(false);
    }

    public byte[] toBinary(final boolean saveExtra) {
        try {
            final LevelProvider provider = this.getProvider();

            if (saveExtra && provider instanceof LevelDB) {

                final List<CompoundTag> entities = new ArrayList<>();

                for (final Entity entity : this.getEntities().values()) {
                    if (!(entity instanceof Player) && !entity.closed) {
                        entity.saveNBT();
                        entities.add(entity.namedTag);
                    }
                }

                final EntitiesKey entitiesKey = EntitiesKey.create(this.getX(), this.getZ());
                if (!entities.isEmpty()) {
                    ((LevelDB) provider).getDatabase().put(entitiesKey.toArray(), NBTIO.write(entities));
                } else {
                    ((LevelDB) provider).getDatabase().delete(entitiesKey.toArray());
                }

                final List<CompoundTag> tiles = new ArrayList<>();

                for (final BlockEntity blockEntity : this.getBlockEntities().values()) {
                    if (!blockEntity.closed) {
                        blockEntity.saveNBT();
                        tiles.add(blockEntity.namedTag);
                    }
                }

                final TilesKey tilesKey = TilesKey.create(this.getX(), this.getZ());
                if (!tiles.isEmpty()) {
                    ((LevelDB) provider).getDatabase().put(tilesKey.toArray(), NBTIO.write(tiles));
                } else {
                    ((LevelDB) provider).getDatabase().delete(tilesKey.toArray());
                }

                final ExtraDataKey extraDataKey = ExtraDataKey.create(this.getX(), this.getZ());
                if (!this.getBlockExtraDataArray().isEmpty()) {
                    final BinaryStream extraData = new BinaryStream();
                    final Map<Integer, Integer> extraDataArray = this.getBlockExtraDataArray();
                    extraData.putInt(extraDataArray.size());
                    for (final Integer key : extraDataArray.keySet()) {
                        extraData.putInt(key);
                        extraData.putShort(extraDataArray.get(key));
                    }
                    ((LevelDB) provider).getDatabase().put(extraDataKey.toArray(), extraData.getBuffer());
                } else {
                    ((LevelDB) provider).getDatabase().delete(extraDataKey.toArray());
                }

            }

            final byte[] heightMap = this.getHeightMapArray();

            final byte[] biomeColors = new byte[this.biomes.length * 4];
            for (int i = 0; i < this.biomes.length; i++) {
                final byte[] bytes = Binary.writeInt(this.biomes[i] << 24);
                biomeColors[i * 4] = bytes[0];
                biomeColors[i * 4 + 1] = bytes[1];
                biomeColors[i * 4 + 2] = bytes[2];
                biomeColors[i * 4 + 3] = bytes[3];
            }

            return Binary.appendBytes(
                Binary.writeLInt(this.getX()),
                Binary.writeLInt(this.getZ()),
                this.getBlockIdArray(),
                this.getBlockDataArray(),
                this.getBlockSkyLightArray(),
                this.getBlockLightArray(),
                heightMap,
                biomeColors,
                new byte[]{(byte) (((this.isLightPopulated ? 0x04 : 0) | (this.isPopulated() ? 0x02 : 0) | (this.isGenerated() ? 0x01 : 0)) & 0xff)}
            );
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}
