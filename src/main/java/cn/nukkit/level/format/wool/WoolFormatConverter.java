package cn.nukkit.level.format.wool;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.anvil.util.BlockStorage;
import cn.nukkit.level.format.anvil.util.NibbleArray;
import cn.nukkit.level.format.wool.ex.CorruptedWorldException;
import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import cn.nukkit.nbt.tag.*;
import com.github.luben.zstd.Zstd;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public class WoolFormatConverter {
    public static final byte VERSION = 0x01;
    public static final byte[] HEADER = new byte[]{116,114,112};

    /*
    public static void main(String[] args) throws IOException {
        File file = new File("C:\\Users\\ozgur\\Desktop\\TRPixel-Nukkit\\Test\\wool-test\\worlds\\world\\world.wool");
        RandomAccessFile randomAccessFile = new RandomAccessFile(file,"rw");
        randomAccessFile.seek(0);

        byte[] s = new byte[(int) randomAccessFile.length()];
        randomAccessFile.readFully(s);
        DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(s));

        try {
            byte[] fileHeader = new byte[WoolFormatConverter.HEADER.length];
            dataStream.read(fileHeader);

            if (!Arrays.equals(WoolFormatConverter.HEADER, fileHeader)) {
                throw new CorruptedWorldException("asd");
            }

            // File version
            byte version = dataStream.readByte();

            // Chunk
            short minX = dataStream.readShort();
            short minZ = dataStream.readShort();

            short width = dataStream.readShort();
            short depth = dataStream.readShort();

            if (width <= 0 || depth <= 0) {
                throw new CorruptedWorldException("asd");
            }

            int bitmaskSize = (int) Math.ceil((width * depth) / 8.0D);

            byte[] chunkBitmask = new byte[bitmaskSize];
            dataStream.read(chunkBitmask);
            BitSet chunkBitset = BitSet.valueOf(chunkBitmask);

            int compressedChunkDataLength = dataStream.readInt();
            int chunkDataLength = dataStream.readInt();
            byte[] compressedChunkData = new byte[compressedChunkDataLength];
            byte[] chunkData = new byte[chunkDataLength];

            dataStream.read(compressedChunkData);


            // Tile Entities
            byte[] compressedTileEntities = new byte[0];
            byte[] tileEntities = new byte[0];

            boolean hasTiles = dataStream.readBoolean();

            if (hasTiles){
                int compressedTileEntitiesLength = dataStream.readInt();
                int tileEntitiesLength = dataStream.readInt();
                compressedTileEntities = new byte[compressedTileEntitiesLength];
                tileEntities = new byte[tileEntitiesLength];

                dataStream.read(compressedTileEntities);

            }

            // Entities
            byte[] compressedEntities = new byte[0];
            byte[] entities = new byte[0];

            boolean hasEntities = dataStream.readBoolean();

            if (hasEntities) {
                int compressedEntitiesLength = dataStream.readInt();
                int entitiesLength = dataStream.readInt();
                compressedEntities = new byte[compressedEntitiesLength];
                entities = new byte[entitiesLength];

                dataStream.read(compressedEntities);
            }


            // Level Data NBT tag
            int compressedLevelDataLength = dataStream.readInt();
            int levelDataLength = dataStream.readInt();
            byte[] compressedLevelData = new byte[compressedLevelDataLength];
            byte[] levelData = new byte[levelDataLength];

            dataStream.read(compressedLevelData);

            // Data decompression
            Zstd.decompress(chunkData, compressedChunkData);
            Zstd.decompress(tileEntities, compressedTileEntities);
            Zstd.decompress(entities, compressedEntities);
            Zstd.decompress(levelData, compressedLevelData);

            // Chunk deserialization
            Map<Long, WoolChunk> chunks = WoolFormatConverter.readChunks(null, minX, minZ, width, depth, chunkBitset, chunkData);

            HashSet<WoolChunk> init = new HashSet<>();

            // Entity deserialization
            CompoundTag entitiesCompound = readCompoundTag(entities);

            if (entitiesCompound != null) {
                ListTag<CompoundTag> entitiesList = (ListTag<CompoundTag>) entitiesCompound.get("entities");

                for (CompoundTag entityCompound : entitiesList.getAll()) {
                    ListTag<DoubleTag> listTag = (ListTag<DoubleTag>) entityCompound.get("Pos").getAsListTag().get();

                    int chunkX = WoolFormatConverter.floor(listTag.get(0).data) >> 4;
                    int chunkZ = WoolFormatConverter.floor(listTag.get(2).data) >> 4;
                    long chunkKey = ((long) chunkZ) * Integer.MAX_VALUE + ((long) chunkX);
                    WoolChunk chunk = chunks.get(chunkKey);

                    if (chunk == null) {
                        throw new CorruptedWorldException("asd");
                    }

                    chunk.getNBTentities().add(entityCompound);
                    init.add(chunk);
                }
            }

            // Tile Entity deserialization
            CompoundTag tileEntitiesCompound = readCompoundTag(tileEntities);

            if (tileEntitiesCompound != null) {
                ListTag<CompoundTag> tileEntitiesList = (ListTag<CompoundTag>) tileEntitiesCompound.get("tiles");

                for (CompoundTag tileEntityCompound : tileEntitiesList.getAll()) {
                    int chunkX = ((IntTag) tileEntityCompound.get("x")).data >> 4;
                    int chunkZ = ((IntTag) tileEntityCompound.get("z")).data >> 4;
                    long chunkKey = ((long) chunkZ) * Integer.MAX_VALUE + ((long) chunkX);
                    WoolChunk chunk = chunks.get(chunkKey);

                    if (chunk == null) {
                        throw new CorruptedWorldException("asd");
                    }

                    chunk.getNBTtiles().add(tileEntityCompound);
                    init.add(chunk);
                }
            }

            // LevelData
            CompoundTag levelDataCompound = readCompoundTag(levelData);

            if (levelDataCompound == null) levelDataCompound = new CompoundTag("", new HashMap<>());

            for (WoolChunk chunk : chunks.values()) {
                System.out.println(chunk);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
     */

    public static CompoundTag readCompoundTag(byte[] serializedCompound) throws IOException {
        if (serializedCompound.length == 0) {
            return null;
        }

        NBTInputStream stream = new NBTInputStream(new ByteArrayInputStream(serializedCompound), NBTInputStream.NO_COMPRESSION, ByteOrder.BIG_ENDIAN);

        return (CompoundTag) stream.readTag();
    }

    public static void writeBitSetAsBytes(DataOutputStream outStream, BitSet set, int fixedSize) throws IOException {
        byte[] array = set.toByteArray();
        outStream.write(array);

        int chunkMaskPadding = fixedSize - array.length;

        for (int i = 0; i < chunkMaskPadding; i++) {
            outStream.write(0);
        }
    }

    public static int floor(double num) {
        final int floor = (int) num;
        return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }

    public static Map<Long, WoolChunk> readChunks(LevelProvider provider, int minX, int minZ, int width, int depth, BitSet chunkBitset, byte[] chunkData) throws IOException {
        DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(chunkData));
        Map<Long, WoolChunk> chunkMap = new HashMap<>();

        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < width; x++) {
                int bitsetIndex = z * width + x;

                if (chunkBitset.get(bitsetIndex)) {
                    // Biome array
                    byte[] byteBiomes = new byte[256];
                    dataStream.read(byteBiomes);

                    // Height Maps
                    byte[] heightMap = new byte[256];
                    dataStream.read(heightMap);

                    // Chunk Sections
                    WoolChunkSection[] chunkSectionArray = new WoolChunkSection[16];
                    byte[] sectionBitmask = new byte[2];
                    dataStream.read(sectionBitmask);
                    BitSet sectionBitset = BitSet.valueOf(sectionBitmask);

                    for (int i = 0; i < 16; i++) {
                        if (sectionBitset.get(i)) {
                            // Block Light Nibble Array
                            byte[] blockLightArray = new byte[2048];

                            if (dataStream.readBoolean()) {
                                dataStream.read(blockLightArray);
                            } else {
                                blockLightArray = null;
                            }

                            // Sky Light Array
                            byte[] skyLightArray = new byte[2048];

                            if (dataStream.readBoolean()) {
                                dataStream.read(skyLightArray);
                            } else {
                                skyLightArray = null;
                            }

                            // Block data
                            byte[] blockArray;
                            NibbleArray dataArray;

                            blockArray = new byte[4096];
                            dataStream.read(blockArray);

                            // Block Data Nibble Array
                            byte[] dataByteArray = new byte[2048];
                            dataStream.read(dataByteArray);
                            dataArray = new NibbleArray((dataByteArray));

                            chunkSectionArray[i] = new WoolChunkSection(i,new BlockStorage(blockArray,dataArray), blockLightArray, skyLightArray);
                        }
                    }

                    chunkMap.put(((long) minZ + z) * Integer.MAX_VALUE + ((long) minX + x), new WoolChunk(provider,minX + x, minZ + z,
                            chunkSectionArray, heightMap, byteBiomes, new ArrayList<>(), new ArrayList<>()));
                }
            }
        }

        return chunkMap;
    }

    public static int[] toIntArray(byte[] buf) {
        ByteBuffer buffer = ByteBuffer.wrap(buf).order(ByteOrder.BIG_ENDIAN);
        int[] ret = new int[buf.length / 4];

        buffer.asIntBuffer().get(ret);

        return ret;
    }

    public static byte[] serializeChunk(WoolChunk chunk) throws IOException {
        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(outByteStream);

        // Biomes
        byte[] biomes = chunk.getBiomeIdArray();


        for (int i = 0; i < 256; i++) {
            outStream.writeByte(biomes[i]);
        }


        // Height Maps
        byte[] heightMap = chunk.getHeightMapArray();

        for (int i = 0; i < 256; i++) {
            outStream.writeByte(heightMap[i]);
        }

        // Chunk sections
        ChunkSection[] sections = chunk.getSections();
        BitSet sectionBitmask = new BitSet(16);

        for (int i = 0; i < sections.length; i++) {
            sectionBitmask.set(i, sections[i] != null);
        }

        writeBitSetAsBytes(outStream, sectionBitmask, 2);

        for (ChunkSection section : sections) {
            // Block Light
            boolean hasBlockLight = section.getLightArray() != null && section.getLightArray().length != 0;
            outStream.writeBoolean(hasBlockLight);

            if (hasBlockLight) {
                outStream.write(section.getLightArray());
            }

            // Sky Light
            boolean hasSkyLight = section.getSkyLightArray() != null && section.getSkyLightArray().length != 0;
            outStream.writeBoolean(hasSkyLight);

            if (hasSkyLight) {
                outStream.write(section.getSkyLightArray());
            }

            outStream.write(section.getStorage().getBlockIds());
            outStream.write(section.getStorage().getBlockData());
        }
        return outByteStream.toByteArray();
    }
    public static byte[] serializeChunks(List<WoolChunk> chunks) throws IOException {
        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(outByteStream);

        for (WoolChunk chunk : chunks) {
            outStream.write(serializeChunk(chunk));
        }

        return outByteStream.toByteArray();
    }

    public static byte[] serializeCompoundTag(CompoundTag tag) throws IOException {
        if (tag == null || tag.getAllTags().isEmpty()) {
            return new byte[0];
        }

        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        NBTOutputStream outStream = new NBTOutputStream(outByteStream, ByteOrder.BIG_ENDIAN);
        outStream.writeTag(tag);

        return outByteStream.toByteArray();
    }


    public static byte[] loadWoolWorld(File file) throws IOException {
        RandomAccessFile rand = new RandomAccessFile(file, "rw");
        rand.seek(0);
        try {
            byte[] serializedWorld = new byte[(int) rand.length()];
            rand.readFully(serializedWorld);
            return serializedWorld;

        } finally {
            rand.close();
        }
    }
}
