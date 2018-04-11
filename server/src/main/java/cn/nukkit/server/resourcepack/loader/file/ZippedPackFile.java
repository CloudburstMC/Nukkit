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

package cn.nukkit.server.resourcepack.loader.file;

import com.voxelwind.server.jni.hash.VoxelwindHash;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ZippedPackFile extends PackFile {
    private Path file;
    private transient volatile byte[] sha256;
    private transient volatile boolean hashed;
    private transient volatile boolean sizeCalculated;
    private transient volatile long size;

    public ZippedPackFile(Path file) {
        this.file = file;
        sha256 = null;
        hashed = false;
        size = 0L;
        sizeCalculated = false;
    }

    @Override
    public long getPackSize() {
        if (!sizeCalculated) {
            try {
                size = Files.size(file);
                sizeCalculated = true;
            } catch (IOException e) {
                throw new RuntimeException("Could not calculate pack size", e);
            }
        }
        return size;
    }

    public int getCompressedSize() {
        // Because it's already zipped. This will be different for unpacked resources.
        return Math.toIntExact(getPackSize());
    }

    @Override
    public byte[] getSha256() {
        if (!hashed) {
            VoxelwindHash hash = hashLocal.get();
            ByteBuf bytes = null;
            try {
                bytes = PooledByteBufAllocator.DEFAULT.directBuffer(Math.toIntExact(Files.size(file))); // Hopefully there is not a resource pack big enough to need a long...
                bytes.writeBytes(Files.readAllBytes(file));
                hash.update(bytes);
                sha256 = hash.digest();
            } catch (Exception e) {
                throw new RuntimeException("Could not calculate pack hash", e);
            } finally {
                if (bytes != null) {
                    bytes.release();
                }
            }
        }
        return sha256;
    }

    @Override
    public byte[] getPackChunk(int chunkIndex) {
        int off = chunkIndex * CHUNK_SIZE;
        byte[] chunk;
        if (getPackSize() - off > CHUNK_SIZE) {
            chunk = new byte[CHUNK_SIZE];
        } else {
            chunk = new byte[(int)(getPackSize() - off)];
        }

        try (InputStream fis = Files.newInputStream(file)) {
            fis.read(chunk, off, CHUNK_SIZE);
        } catch (Exception e) {
            throw new RuntimeException("Unable to read pack chunk", e);
        }

        return chunk;
    }

    @Override
    public int getChunkCount() {
        return (int) Math.ceil(getPackSize() / (float) CHUNK_SIZE);
    }
}
