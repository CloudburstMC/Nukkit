package cn.nukkit.level.format.anvil;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.nbt.NbtIo;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.tile.Spawnable;
import cn.nukkit.tile.Tile;
import cn.nukkit.utils.Binary;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChunkRequestTask extends AsyncTask {
    protected int levelId;

    protected byte[] chunk;
    protected int chunkX;
    protected int chunkZ;

    protected byte[] tiles;

    public ChunkRequestTask(Level level, Chunk chunk) throws IOException {
        this.levelId = level.getId();
        this.chunk = chunk.toFastBinary();
        this.chunkX = chunk.getX();
        this.chunkZ = chunk.getZ();

        String hex = "";
        for (Tile tile : chunk.getTiles().values()) {
            if (tile instanceof Spawnable) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream outputStream = new DataOutputStream(baos);
                NbtIo.writeCompressed(((Spawnable) tile).getSpawnCompound(), outputStream);
                hex += Binary.bytesToHexString(baos.toByteArray());
            }
        }
        this.tiles = Binary.hexStringToBytes(hex);
    }

    @Override
    public void onRun() {
        Chunk chunk = Chunk.fromFastBinary(this.chunk);
        byte[] ids = chunk.getBlockIdArray();
        byte[] meta = chunk.getBlockDataArray();
        byte[] blockLight = chunk.getBlockLightArray();
        byte[] skyLight = chunk.getBlockSkyLightArray();

        StringBuilder orderedIds = new StringBuilder();
        StringBuilder orderedData = new StringBuilder();
        StringBuilder orderedSkyLight = new StringBuilder();
        StringBuilder orderedLight = new StringBuilder();

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                orderedIds.append(Binary.bytesToHexString(this.getColumn(ids, x, z)));
                orderedData.append(Binary.bytesToHexString(this.getHalfColumn(meta, x, z)));
                orderedSkyLight.append(Binary.bytesToHexString(this.getHalfColumn(skyLight, x, z)));
                orderedLight.append(Binary.bytesToHexString(this.getHalfColumn(blockLight, x, z)));
            }
        }

        StringBuilder heightMap = new StringBuilder();
        for (int i : chunk.getHeightMapArray()) {
            String hex = Integer.toHexString(i & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            heightMap.append(hex);
        }
        StringBuilder biomeColors = new StringBuilder();
        for (int i : chunk.getBiomeColorArray()) {
            biomeColors.append(Binary.bytesToHexString(Binary.writeInt(i)));
        }
        this.setResult(Binary.hexStringToBytes(orderedIds.toString() + orderedData.toString() + orderedSkyLight.toString() + orderedLight.toString() + heightMap.toString() + biomeColors.toString() + Binary.bytesToHexString(this.tiles)));
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
                column[(y / 2)] = (byte) ((byte) (data[(y << 7) + i] & 0x0f) | (byte) ((((data[((y + 1) << 7) + i] & 0x0f) >> 24) & 0xff) << 4));
            }
        } else {
            for (int y = 0; y < 128; y += 2) {
                column[(y / 2)] = (byte) ((byte) ((((data[(y << 7) + i] & 0xf0) >> 24) & 0xff) >> 4) | (byte) (data[((y + 1) << 7) + i] & 0xf0));
            }
        }
        return column;
    }

    @Override
    public void onCompletion(Server server) {
        Level level = server.getLevel(this.levelId);
        if (level != null && this.hasResult()) {
            level.chunkRequestCallback(this.chunkX, this.chunkZ, (byte[]) this.getResult());
        }
    }
}
