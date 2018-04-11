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

package cn.nukkit.server.level.provider.anvil;

import cn.nukkit.api.level.Level;
import cn.nukkit.api.level.chunk.Chunk;
import cn.nukkit.server.level.provider.ChunkProvider;
import cn.nukkit.server.nbt.NBTIO;
import cn.nukkit.server.nbt.stream.NBTInputStream;
import cn.nukkit.server.nbt.tag.CompoundTag;
import cn.nukkit.server.nbt.tag.Tag;
import lombok.Value;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class AnvilChunkProvider implements ChunkProvider {
    private static final int CAPACITY = 262144;
    private final Path basePath;
    private final Map<RegionXZ, AnvilRegionFile> regionFiles = new HashMap<>();

    public AnvilChunkProvider(Path basePath) {
        this.basePath = basePath;
    }

    @Override
    public CompletableFuture<Chunk> createChunk(Level level, int x, int z, Executor executor) {
        RegionXZ rXZ = RegionXZ.fromChunkXZ(x, z);
        InRegionXZ irXZ = new InRegionXZ(x - rXZ.getX() * 32, z - rXZ.getZ() * 32);

        CompletableFuture<Chunk> chunkFuture = new CompletableFuture<>();
        executor.execute(() -> {
            try {
                AnvilRegionFile regionFile;
                synchronized (regionFiles) {
                    regionFile = regionFiles.get(rXZ);
                    if (regionFile == null) {
                        Path regionFilePath = basePath.resolve("region").resolve("r." + rXZ.x + '.' + rXZ.z + ".mca");
                        try {
                            regionFile = new AnvilRegionFile(regionFilePath);
                            regionFiles.put(rXZ, regionFile);
                        } catch (NoSuchFileException e) {
                            chunkFuture.complete(null);
                            return;
                        }
                    }
                }

                if (regionFile.hasChunk(irXZ.x, irXZ.z)) {
                    Tag<?> tag;
                    try (NBTInputStream reader = NBTIO.createReader(regionFile.readChunk(irXZ.x, irXZ.z))) {
                        tag = reader.readTag();
                    }

                    Map<String, Tag<?>> compoundValue = ((CompoundTag) tag).getValue();
                    Map<String, Tag<?>> levelMap = ((CompoundTag) compoundValue.get("Level")).getValue();

                    chunkFuture.complete(AnvilConversion.convertChunkToNukkit(levelMap, level));
                } else {
                    chunkFuture.complete(null);
                }
            } catch (Exception e) {
                chunkFuture.completeExceptionally(e);
            }
        });

        return chunkFuture;
    }

    @Override
    public void saveChunk(Chunk chunk, Executor executor) {
        /* TODO: Implement chunk saving
        RegionXZ rXZ = RegionXZ.fromChunkXZ(chunk.getX(), chunk.getZ());
        InRegionXZ irXZ = new InRegionXZ(chunk.getX() - rXZ.getX() * 32, chunk.getZ() - rXZ.getZ() * 32);

        executor.execute(() -> {
            try {
                AnvilRegionFile regionFile;
                synchronized (regionFiles) {
                    regionFile = regionFiles.get(rXZ);
                    if (regionFile == null) {
                        Path regionFilePath = basePath.resolve("region").resolve("r." + rXZ.x + '.' + rXZ.z + ".mca");
                        try {
                            regionFile = new AnvilRegionFile(regionFilePath);
                            regionFiles.put(rXZ, regionFile);
                        } catch (NoSuchFileException e) {
                            return;
                        }
                    }
                }
                Map<String, Tag<?>> levelMap = AnvilConversion.convertChunkToAnvil(chunk);
                regionFile.writeChunk(chunk.getX(), chunk.getZ(), );
            } catch (Exception e) {

            }
        });*/
    }

    @Value
    private static class RegionXZ {
        private final int x;
        private final int z;

        public static RegionXZ fromChunkXZ(int x, int z) {
            return new RegionXZ(x >> 5, z >> 5);
        }
    }

    @Value
    private static class InRegionXZ {
        private final int x;
        private final int z;
    }
}
