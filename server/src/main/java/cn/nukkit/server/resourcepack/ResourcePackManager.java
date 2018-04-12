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

package cn.nukkit.server.resourcepack;

import cn.nukkit.api.resourcepack.BehaviorPack;
import cn.nukkit.api.resourcepack.MinecraftPackManifest;
import cn.nukkit.api.resourcepack.PackLoader;
import cn.nukkit.api.resourcepack.ResourcePack;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.console.TranslatableMessage;
import cn.nukkit.server.math.graph.DirectedAcyclicGraph;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.netty.util.internal.ConcurrentSet;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
public class ResourcePackManager {
    private final NukkitServer server;
    private final BiMap<Class<? extends PackLoader>, PackLoader> loaders = HashBiMap.create();
    private Set<ResourcePack> resourcePacks = new ConcurrentSet<>();
    private Set<BehaviorPack> behaviorPacks = new ConcurrentSet<>();
    private Map<UUID, ResourcePack> packsById = new ConcurrentHashMap<>();

    public ResourcePackManager(NukkitServer server) {
        this.server = server;
    }

    public <T extends PackLoader> void registerPluginLoader(Class<T> loaderClass, T loaderInstance) {
        Preconditions.checkNotNull(loaderClass, "loaderClass");
        Preconditions.checkNotNull(loaderInstance, "loaderInstance");
        if (loaders.containsKey(loaderClass)) {
            throw new IllegalArgumentException("The plugin loader has already been registered.");
        }
        loaders.put(loaderClass, loaderInstance);
    }

    public <T extends PackLoader> void unregisterPluginLoader(Class<T> loaderClass) {
        Preconditions.checkNotNull(loaderClass, "loaderClass");
        if (!loaders.containsKey(loaderClass)) {
            throw new IllegalArgumentException("The plugin loader was never registered.");
        }
        loaders.remove(loaderClass);
    }

    public void loadResourcePacks(Path resourceDirectory, Collection<Class<? extends PackLoader>> loaderClasses) throws IOException {
        Preconditions.checkNotNull(resourceDirectory, "directory");
        Preconditions.checkArgument(Files.isDirectory(resourceDirectory), "provided path isn't a directory");
        Collection<PackLoader> packLoaders = getPluginLoaders(loaderClasses);
        List<MinecraftPackManifest> found = new ArrayList<>();
        Map<MinecraftPackManifest, PackLoader> foundResourceMap = new HashMap<>();
        for (PackLoader loader: packLoaders) {
            final boolean getDirectories;
            Optional<String[]> optionalExtensions = loader.getPluginFileExtension();
            String[] extensions;
            if (optionalExtensions.isPresent()) {
                extensions = optionalExtensions.get();
                getDirectories = false;
            } else {
                extensions = new String[] {""};
                getDirectories = true;
            }

            for (String extension: extensions) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(resourceDirectory, p ->
                        (Files.isRegularFile(p) && p.toString().endsWith(extension) && !getDirectories) || getDirectories && Files.isDirectory(p))) {
                    for (Path path : stream) {
                        try {
                            MinecraftPackManifest manifest = loader.loadManifest(path);
                            found.add(manifest);
                            foundResourceMap.put(manifest, loader);
                        } catch (Exception e) {
                            log.error("Unable to enumerate resource pack {}", path, e);
                        }
                    }
                }
            }
        }

        if (found.isEmpty()) {
            // No resource packs found.
            return;
        }

        for (MinecraftPackManifest pack : found) {
            // Verify version is compatible.
            if (pack.getHeader().getMinimumSupportedMinecraftVersion() != null &&
                    NukkitServer.MINECRAFT_VERSION.isCompatiblePatch(pack.getHeader().getMinimumSupportedMinecraftVersion())) {
                log.error("Pack is not compaible with server.");
                continue;
            }

            if (packsById.containsKey(pack.getHeader().getUuid())) {
                log.error(TranslatableMessage.of("nukkit.plugin.duplicateError", pack.getHeader().getName()));
                continue;
            }


            // Now actually create the plugin.
            ResourcePack resourcePack;
            try {
                resourcePack = foundResourceMap.get(pack).createResourcePack(pack);
            } catch (Exception e) {
                log.error("Can't create resource {}", pack.getHeader().getName(), e);
                continue;
            }

            packsById.put(resourcePack.getId(), resourcePack);
            resourcePacks.add(resourcePack);
        }
    }

    public void loadBehaviorPacks(Path behaviorPath) {

    }

    public ResourcePack[] getResourceStack() {
        return resourcePacks.toArray(new ResourcePack[resourcePacks.size()]);
    }

    public Optional<ResourcePack> getPackById(UUID id) {
        return Optional.ofNullable(packsById.get(id));
    }

    private List<MinecraftPackManifest> sortPackDependencies(List<MinecraftPackManifest> manifests) {
        // Create the graph.
        DirectedAcyclicGraph<MinecraftPackManifest> graph = new DirectedAcyclicGraph<>();

        // Add edges
        for (MinecraftPackManifest manifest : manifests) {
            graph.add(manifest);
            for (MinecraftPackManifest.Dependency dependency : manifest.getDependencies()) {
                Optional<MinecraftPackManifest> in = manifests.stream().filter(m -> m.getHeader().getUuid().equals(dependency.getUuid())).findFirst();
                in.ifPresent(packManifest -> graph.addEdges(manifest, packManifest));
            }
        }

        return DirectedAcyclicGraph.sort(graph);
    }

    @Nonnull
    private Collection<PackLoader> getPluginLoaders(@Nullable Collection<Class<? extends PackLoader>> loaderClasses) {
        final Collection<PackLoader> pluginLoaders = new ArrayList<>();
        if (loaderClasses == null) {
            pluginLoaders.addAll(loaders.values());
        } else {
            loaderClasses.forEach(loaderClass -> {
                if (loaders.containsKey(loaderClass)) {
                    pluginLoaders.add(loaders.get(loaderClass));
                }
            });
        }
        return pluginLoaders;
    }
}
