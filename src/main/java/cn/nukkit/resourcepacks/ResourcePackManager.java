package cn.nukkit.resourcepacks;

import cn.nukkit.Server;
import com.google.common.io.Files;

import java.io.File;
import java.util.*;

public class ResourcePackManager {
    private final Map<UUID, ResourcePack> packsById = new HashMap<>();
    private final ResourcePack[] resourcePacks;
    private final ResourcePack[] behaviorPacks;

    public ResourcePackManager(File resourcePacksPath, File behaviorPacksPath) {
        if (!resourcePacksPath.exists()) {
            resourcePacksPath.mkdirs();
        } else if (!resourcePacksPath.isDirectory()) {
            throw new IllegalArgumentException(Server.getInstance().getLanguage()
                    .translateString("nukkit.resources.invalid-path", resourcePacksPath.getName()));
        }

        if (!behaviorPacksPath.exists()) {
            behaviorPacksPath.mkdirs();
        } else if (!behaviorPacksPath.isDirectory()) {
            throw new IllegalArgumentException(Server.getInstance().getLanguage()
                    .translateString("nukkit.resources.invalid-path", behaviorPacksPath.getName()));
        }

        List<ResourcePack> loadedResourcePacks = new ArrayList<>();
        List<ResourcePack> loadedBehaviorPacks = new ArrayList<>();
        for (File pack : resourcePacksPath.listFiles()) {
            try {
                ResourcePack resourcePack = null;

                if (!pack.isDirectory()) { //directory resource packs temporarily unsupported
                    switch (Files.getFileExtension(pack.getName())) {
                        case "zip":
                        case "mcpack":
                            resourcePack = new ZippedResourcePack(pack);
                            break;
                        default:
                            Server.getInstance().getLogger().warning(Server.getInstance().getLanguage()
                                    .translateString("nukkit.resources.unknown-format", pack.getName()));
                            break;
                    }
                }

                if (resourcePack != null) {
                    if (!resourcePack.getType().equals(ResourcePack.Type.RESOURCE_PACK)) {
                        Server.getInstance().getLogger().warning(Server.getInstance().getLanguage()
                                .translateString("nukkit.resources.invalid-type-resource", pack.getName(), resourcePack.getPackName()));

                        continue;
                    }

                    loadedResourcePacks.add(resourcePack);
                    this.packsById.put(resourcePack.getPackId(), resourcePack);

                    Server.getInstance().getLogger().info(Server.getInstance().getLanguage()
                            .translateString("nukkit.resources.loaded-resource-pack", resourcePack.getPackName()));
                }
            } catch (IllegalArgumentException e) {
                Server.getInstance().getLogger().warning(Server.getInstance().getLanguage()
                        .translateString("nukkit.resources.fail", pack.getName(), e.getMessage()));

                e.printStackTrace();
            }
        }

        for (File pack : behaviorPacksPath.listFiles()) {
            try {
                ResourcePack behaviorPack = null;

                if (!pack.isDirectory()) { //directory resource packs temporarily unsupported
                    switch (Files.getFileExtension(pack.getName())) {
                        case "zip":
                        case "mcpack":
                            behaviorPack = new ZippedResourcePack(pack);
                            break;
                        default:
                            Server.getInstance().getLogger().warning(Server.getInstance().getLanguage()
                                    .translateString("nukkit.resources.unknown-format", pack.getName()));
                            break;
                    }
                }

                if (behaviorPack != null) {
                    if (!behaviorPack.getType().equals(ResourcePack.Type.BEHAVIOR_PACK)) {
                        Server.getInstance().getLogger().warning(Server.getInstance().getLanguage()
                                .translateString("nukkit.resources.invalid-type-behavior", pack.getName(), behaviorPack.getPackName()));

                        continue;
                    }

                    loadedBehaviorPacks.add(behaviorPack);
                    this.packsById.put(behaviorPack.getPackId(), behaviorPack);

                    Server.getInstance().getLogger().info(Server.getInstance().getLanguage()
                            .translateString("nukkit.resources.loaded-behavior-pack", behaviorPack.getPackName()));
                }
            } catch (IllegalArgumentException e) {
                Server.getInstance().getLogger().warning(Server.getInstance().getLanguage()
                        .translateString("nukkit.resources.fail", pack.getName(), e.getMessage()));
            }
        }

        this.resourcePacks = loadedResourcePacks.toArray(new ResourcePack[0]);
        this.behaviorPacks = loadedBehaviorPacks.toArray(new ResourcePack[0]);

        Server.getInstance().getLogger().info(Server.getInstance().getLanguage()
                .translateString("nukkit.resources.success", String.valueOf(this.resourcePacks.length + this.behaviorPacks.length)));
    }

    public ResourcePack[] getResourcePacks() {
        return this.resourcePacks;
    }

    public ResourcePack[] getBehaviorPacks() {
        return this.behaviorPacks;
    }

    public ResourcePack getPackById(UUID id) {
        return this.packsById.get(id);
    }
}
