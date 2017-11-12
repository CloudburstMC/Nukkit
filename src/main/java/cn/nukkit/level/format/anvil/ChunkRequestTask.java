package cn.nukkit.level.format.anvil;

import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.level.Level;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.network.protocol.PlayerProtocol;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChunkRequestTask extends AsyncTask {
    protected final int levelId;

    protected final byte[] chunk;
    protected final int chunkX;
    protected final int chunkZ;

    protected byte[] blockEntities;
    protected byte[] blockEntities113;

    public ChunkRequestTask(Level level, Chunk chunk) {
        this.levelId = level.getId();
        this.chunk = chunk.toFastBinary();
        this.chunkX = chunk.getX();
        this.chunkZ = chunk.getZ();

        byte[] buffer = new byte[0];
        byte[] buffer113 = new byte[0];

        for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
            if (blockEntity instanceof BlockEntitySpawnable) {
                try {
                    buffer = Binary.appendBytes(buffer, NBTIO.write(((BlockEntitySpawnable) blockEntity)
                            .getSpawnCompound(PlayerProtocol.PLAYER_PROTOCOL_130), ByteOrder.BIG_ENDIAN, true));
                    buffer113 = Binary.appendBytes(buffer, NBTIO.write(((BlockEntitySpawnable) blockEntity)
                            .getSpawnCompound(PlayerProtocol.PLAYER_PROTOCOL_113), ByteOrder.BIG_ENDIAN, true));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }

        this.blockEntities = buffer;
        this.blockEntities113 = buffer113;
    }

    @Override
    public void onRun() {
        Chunk chunk = Chunk.fromFastBinary(this.chunk);
        byte[] ids = chunk.getBlockIdArray();
        byte[] meta = chunk.getBlockDataArray();
        byte[] blockLight = chunk.getBlockLightArray();
        byte[] skyLight = chunk.getBlockSkyLightArray();
        int[] heightMap = chunk.getHeightMapArray();
        int[] biomeColors = chunk.getBiomeColorArray();
        ByteBuffer buffer = ByteBuffer.allocate(
                16 * 16 * (128 + 64 + 64 + 64)
                        + 256
                        + 256
                        + this.blockEntities.length
        );
        ByteBuffer buffer113 = ByteBuffer.allocate(
                16 * 16 * (128 + 64 + 64 + 64)
                        + 256
                        + 256
                        + 256
                        + 256
                        + this.blockEntities113.length
        );

        ByteBuffer orderedIds = ByteBuffer.allocate(16 * 16 * 128);
        ByteBuffer orderedData = ByteBuffer.allocate(16 * 16 * 64);
        ByteBuffer orderedSkyLight = ByteBuffer.allocate(16 * 16 * 64);
        ByteBuffer orderedLight = ByteBuffer.allocate(16 * 16 * 64);

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                orderedIds.put(this.getColumn(ids, x, z));
                orderedData.put(this.getHalfColumn(meta, x, z));
                orderedSkyLight.put(this.getHalfColumn(skyLight, x, z));
                orderedLight.put(this.getHalfColumn(blockLight, x, z));
            }
        }

        ByteBuffer orderedHeightMap = ByteBuffer.allocate(heightMap.length);
        for (int i : heightMap) {
            orderedHeightMap.put((byte) (i & 0xff));
        }
        ByteBuffer orderedBiomeColors = ByteBuffer.allocate(biomeColors.length * 4);
        for (int i : biomeColors) {
            orderedBiomeColors.put(Binary.writeInt(i));
        }

        this.setResult(
                new byte[][]{buffer
                        .put(orderedIds)
                        .put(orderedData)
                        .put(orderedHeightMap)
                        .put(orderedBiomeColors)
                        .put(this.blockEntities)
                        .array(),
                           buffer113
                        .put(orderedIds)
                        .put(orderedData)
                        .put(orderedHeightMap)
                        .put(orderedBiomeColors)
                        .put(orderedSkyLight)
                        .put(orderedLight)
                        .put(this.blockEntities113)
                        .array()}
        );
    }

    public byte[] getColumn(byte[] data, int x, int z) {
        byte[] column = new byte[128];
        int i = (z << 4) + x;
        for (int y = 0; y < 128; ++y) {
            column[y] = data[(y << 8) + i];
        }
        return column;
    }

    public byte[] getHalfColumn(byte[] data, int x, int z) {
        byte[] column = new byte[64];
        int i = (z << 3) + (x >> 1);
        if ((x & 1) == 0) {
            for (int y = 0; y < 128; y += 2) {
                column[(y / 2)] = (byte) ((byte) (data[(y << 7) + i] & 0x0f) | (byte) (((data[((y + 1) << 7) + i] & 0x0f) & 0xff) << 4));
            }
        } else {
            for (int y = 0; y < 128; y += 2) {
                column[(y / 2)] = (byte) ((byte) (((data[(y << 7) + i] & 0xf0) & 0xff) >> 4) | (byte) (data[((y + 1) << 7) + i] & 0xf0));
            }
        }
        return column;
    }

    @Override
    public void onCompletion(Server server) {
        Level level = server.getLevel(this.levelId);
        if (level != null && this.hasResult()) {
            byte[][] result = (byte[][]) this.getResult();
            level.chunkRequestCallback(this.chunkX, this.chunkZ, result[1], result[0]);
        }
    }
}
