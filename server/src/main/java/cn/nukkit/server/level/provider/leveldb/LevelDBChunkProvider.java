package cn.nukkit.server.level.provider.leveldb;

import cn.nukkit.api.level.Level;
import cn.nukkit.api.level.chunk.Chunk;
import cn.nukkit.server.level.chunk.ChunkSection;
import cn.nukkit.server.level.chunk.SectionedChunk;
import cn.nukkit.server.level.provider.ChunkProvider;
import cn.nukkit.server.level.provider.LegacyChunkConverter;
import com.google.common.base.Preconditions;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class LevelDBChunkProvider implements ChunkProvider, Closeable {
    private static final byte TAG_BLOCK_ENTITY = 0x01;
    private static final byte TAG_ENTITY = 0x02;
    private static final byte TAG_PENDING_TICK = 0x03;
    private static final byte TAG_BLOCK_EXTRA_DATA = 0x04;
    private static final byte TAG_BIOME_STATE = 0x05;
    private static final byte TAG_STATE_FINALISATION = 0x06;
    private static final byte TAG_DATA_2D = 0x2d;
    private static final byte TAG_CHUNK_SECTION = 0x2f;
    private static final byte TAG_VERSION = 0x76;
    private static final int BLOCK_ARRAY_SIZE = 4096;
    private static final int NIBBLE_ARRAY_SIZE = 2048;

    private static final byte CHUNK_SECTION_COUNT = 16;

    private final DB db;

    public LevelDBChunkProvider(Path basePath) {
        File leveldb = basePath.resolve("db").toFile();
        try {
            db = Iq80DBFactory.factory.open(leveldb, new Options().createIfMissing(true));
        } catch (IOException e) {
            throw new RuntimeException("Unable to initialize world", e);
        }
    }

    @Override
    public CompletableFuture<Chunk> createChunk(Level level, int x, int z, Executor executor) {
        CompletableFuture<Chunk> chunkFuture = new CompletableFuture<>();

        executor.execute(() -> {

            try {
                byte[] version = this.db.get(getKey(x, z, TAG_VERSION));
                if (version == null || version.length != 1 || version[0] < 4) {
                    chunkFuture.complete(null);
                    return;
                }
                ChunkSection[] chunkSections = new ChunkSection[CHUNK_SECTION_COUNT];
                for (byte y = 0; y < CHUNK_SECTION_COUNT; y++) {
                    ByteBuffer buffer = ByteBuffer.wrap(db.get(getKey(x, z, TAG_CHUNK_SECTION, y)));
                    byte chunkSectionVersion = buffer.get();
                    switch (chunkSectionVersion) {
                        case 1:
                            break;
                        case 0:
                            Preconditions.checkArgument(buffer.remaining() >= (BLOCK_ARRAY_SIZE + NIBBLE_ARRAY_SIZE), "Buffer too small for blockIDs and blockData");
                            byte[] blockIds = new byte[BLOCK_ARRAY_SIZE];
                            byte[] blockData = new byte[NIBBLE_ARRAY_SIZE];
                            byte[] skyLight = new byte[NIBBLE_ARRAY_SIZE];
                            byte[] blockLight = new byte[NIBBLE_ARRAY_SIZE];
                            buffer.get(blockIds);
                            buffer.get(blockData);
                            if (buffer.remaining() >= BLOCK_ARRAY_SIZE) {
                                buffer.get(skyLight);
                                buffer.get(blockLight);
                            }
                            chunkSections[y] = LegacyChunkConverter.convertFromLegacy(blockIds, blockData, skyLight, blockLight);
                            break;
                    }
                }


                SectionedChunk sectionedChunk = new SectionedChunk(chunkSections, x, z, level);
                chunkFuture.complete(sectionedChunk);
            } catch (Exception e) {
                chunkFuture.completeExceptionally(e);
            }
        });
        return chunkFuture;
    }

    @Override
    public void saveChunk(Chunk chunk, Executor executor) {
        //TODO
    }

    @Override
    public void close() throws IOException {
        db.close();
    }

    private static byte[] getKey(int x, int z, byte tag) {
        return new byte[]{
                (byte) (x & 0xff),
                (byte) ((x >>> 8) & 0xff),
                (byte) ((x >>> 16) & 0xff),
                (byte) ((x >>> 24) & 0xff),
                (byte) (z & 0xff),
                (byte) ((z >>> 8) & 0xff),
                (byte) ((z >>> 16) & 0xff),
                (byte) ((z >>> 24) & 0xff),
                tag
        };
    }

    private static byte[] getKey(int x, int z, byte tag, byte y) {
        return new byte[]{
                (byte) (x & 0xff),
                (byte) ((x >>> 8) & 0xff),
                (byte) ((x >>> 16) & 0xff),
                (byte) ((x >>> 24) & 0xff),
                (byte) (z & 0xff),
                (byte) ((z >>> 8) & 0xff),
                (byte) ((z >>> 16) & 0xff),
                (byte) ((z >>> 24) & 0xff),
                tag,
                y
        };
    }
}
