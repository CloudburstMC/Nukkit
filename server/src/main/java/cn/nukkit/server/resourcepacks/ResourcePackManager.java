package cn.nukkit.server.resourcepacks;

import cn.nukkit.api.resourcepack.ResourcePack;
import cn.nukkit.server.NukkitServer;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class ResourcePackManager {
    private ResourcePack[] resourcePacks;
    private Map<String, ResourcePack> resourcePacksById = new HashMap<>();

    public ResourcePackManager(NukkitServer server, Path resourcePath) throws Exception {
        if (Files.notExists(resourcePath)) {
            Files.createDirectory(resourcePath);
        } else if (!Files.isDirectory(resourcePath)) {
            throw new IllegalArgumentException(server.getLanguage()
                    .translateString("nukkit.resources.invalid-path", resourcePath.toString()));
        }

        List<ResourcePack> loadedResourcePacks = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(resourcePath, file -> Files.isRegularFile(file) && (file.toString().endsWith("zip") || file.toString().endsWith("mcpack")))) {
            stream.forEach(path ->{
                ResourcePack pack = new ZippedResourcePack(path);
                if (pack != null) {
                    loadedResourcePacks.add(pack);
                    resourcePacksById.put(pack.getPackId(), pack);
                }
            });
        } catch (Exception e) {
            log.warn(server.getLanguage().translateString("nukkit.resources.fail", pack.getName(), e.getMessage()));
        }

        for (File pack : path.listFiles()) {
            try {
                ResourcePack resourcePack = null;

                if (!pack.isDirectory()) { //directory resource packs temporarily unsupported
                    switch (Files.getFileExtension(pack.getName())) {
                        case "zip":
                        case "mcpack":
                            resourcePack = new ZippedResourcePack(pack);
                            break;
                        default:
                            log.warn(NukkitServer.getInstance().getLanguage()
                                    .translateString("nukkit.resources.unknown-format", pack.getName()));
                            break;
                    }
                }

                if (resourcePack != null) {
                    loadedResourcePacks.add(resourcePack);
                    this.resourcePacksById.put(resourcePack.getPackId(), resourcePack);
                }
            } catch (IllegalArgumentException e) {
                log.warn(NukkitServer.getInstance().getLanguage()
                        .translateString("nukkit.resources.fail", pack.getName(), e.getMessage()));
            }
        }

        this.resourcePacks = loadedResourcePacks.toArray(new ResourcePack[loadedResourcePacks.size()]);
        log.info(NukkitServer.getInstance().getLanguage()
                .translateString("nukkit.resources.success", String.valueOf(this.resourcePacks.length)));
    }

    public ResourcePack[] getResourceStack() {
        return this.resourcePacks;
    }

    public ResourcePack getPackById(String id) {
        return this.resourcePacksById.get(id);
    }
}
