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

package cn.nukkit.server.level.generator;

import cn.nukkit.api.level.chunk.generator.ChunkGenerator;
import cn.nukkit.api.level.chunk.generator.ChunkGeneratorFactory;
import cn.nukkit.api.level.chunk.generator.ChunkGeneratorRegistry;
import cn.nukkit.server.NukkitServer;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class NukkitChunkGeneratorRegistry implements ChunkGeneratorRegistry {
    private final ConcurrentMap<String, ChunkGeneratorFactory> generators = new ConcurrentHashMap<>();
    private final NukkitServer server;

    /**
     * Nukkit Chunk Generator Registry
     * Registers the default chunk generators
     * @param server the NukkitServer instance
     */
    public NukkitChunkGeneratorRegistry(NukkitServer server) {
        this.server = server;

        // Register default chunk generators.
        register("FLAT", new FlatChunkGeneratorFactory());
        register("NORMAL", new NormalChunkGeneratorFactory()); // Overworld Generator
        register("NETHER", new NetherChunkGeneratorFactory()); // Nether Generator
        register("ENDER", new EnderChunkGeneratorFactory()); // End Generator
    }

    @Override
    public Optional<ChunkGenerator> getChunkGenerator(@Nonnull String name) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "Null or empty Generator name");
        ChunkGeneratorFactory factory = generators.get(name.toUpperCase());
        if (factory == null) {
            return Optional.empty();
        } else {
            return Optional.of(factory.createChunkGenerator(server));
        }
    }

    @Override
    public ChunkGenerator ensureAndGetChunkGenerator(@Nonnull String name) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "Null or empty Generator name");
        return generators.get(name.toUpperCase()).createChunkGenerator(server);
    }

    @Override
    public void register(@Nonnull String name, @Nonnull ChunkGeneratorFactory factory) throws RuntimeException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "Null or empty Generator name.");
        Preconditions.checkNotNull(factory, "generatorFactory");

        if (generators.putIfAbsent(name.toUpperCase(), factory) != null) {
            throw new IllegalStateException("Generator " + name + " has already been defined.");
        }
    }

    @Override
    public void deregister(@Nonnull String name) throws RuntimeException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "Null or empty Generator name");

        if (generators.remove(name) == null) {
            throw new IllegalStateException("No such registered chunk generator");
        }
    }
}
