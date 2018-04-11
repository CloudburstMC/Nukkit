/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.level.provider.leveldb;

import cn.nukkit.api.level.Level;
import cn.nukkit.api.level.LevelException;
import cn.nukkit.api.level.chunk.Chunk;
import cn.nukkit.server.level.chunk.ChunkSection;
import cn.nukkit.server.level.chunk.SectionedChunk;
import cn.nukkit.server.level.provider.ChunkProvider;
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
    private static final int BLOCKID_ARRAY_SIZE = 4096;
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
                if (version == null || version.length != 1) {
                    chunkFuture.complete(null);
                    return;
                }
                if (version[0] < 4) {
                    throw new LevelException("Incompatible chunk version found.");
                }
                ChunkSection[] chunkSections = new ChunkSection[CHUNK_SECTION_COUNT];
                for (byte y = 0; y < CHUNK_SECTION_COUNT; y++) {
                    byte[] chunkSection = this.db.get(getKey(x, z, TAG_CHUNK_SECTION, y));
                    if (chunkSection == null || chunkSection.length < BLOCKID_ARRAY_SIZE + NIBBLE_ARRAY_SIZE) {
                        continue;
                    }
                    ByteBuffer buffer = ByteBuffer.wrap(chunkSection);
                    buffer.get(); // Chunk version.
                    byte[] blockIds = new byte[BLOCKID_ARRAY_SIZE];
                    byte[] blockData = new byte[NIBBLE_ARRAY_SIZE];
                    byte[] skyLight = new byte[NIBBLE_ARRAY_SIZE];
                    byte[] blockLight = new byte[NIBBLE_ARRAY_SIZE];
                    buffer.get(blockIds);
                    buffer.get(blockData);
                    if (buffer.remaining() >= NIBBLE_ARRAY_SIZE * 2) {
                        buffer.get(skyLight);
                        buffer.get(blockLight);
                    }
                    chunkSections[y] = new ChunkSection(blockIds, blockData, skyLight, blockLight);
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
