package cn.nukkit.resourcepacks;

import cn.nukkit.Server;
import com.google.common.io.Files;

import java.io.File;
import java.util.*;

public class ResourcePackManager {
    private final Map<UUID, ResourcePack> packsById = new HashMap<>();
    private final ResourcePack[] resourcePacks;
    private final ResourcePack[] behaviourPacks;

    public ResourcePackManager(File resourcePacksPath, File behaviourPacksPath) {
        if (!resourcePacksPath.exists()) {
            resourcePacksPath.mkdirs();
        } else if (!resourcePacksPath.isDirectory()) {
            throw new IllegalArgumentException(Server.getInstance().getLanguage()
                    .translateString("nukkit.resources.invalid-path", resourcePacksPath.getName()));
        }

        if (!behaviourPacksPath.exists()) {
            behaviourPacksPath.mkdirs();
        } else if (!behaviourPacksPath.isDirectory()) {
            throw new IllegalArgumentException(Server.getInstance().getLanguage()
                    .translateString("nukkit.resources.invalid-path", behaviourPacksPath.getName()));
        }

        List<ResourcePack> loadedResourcePacks = new ArrayList<>();
        List<ResourcePack> loadedBehaviourPacks = new ArrayList<>();
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

        for (File pack : behaviourPacksPath.listFiles()) {
            try {
                ResourcePack behaviourPack = null;

                if (!pack.isDirectory()) { //directory resource packs temporarily unsupported
                    switch (Files.getFileExtension(pack.getName())) {
                        case "zip":
                        case "mcpack":
                            behaviourPack = new ZippedResourcePack(pack);
                            break;
                        default:
                            Server.getInstance().getLogger().warning(Server.getInstance().getLanguage()
                                    .translateString("nukkit.resources.unknown-format", pack.getName()));
                            break;
                    }
                }

                if (behaviourPack != null) {
                    if (!behaviourPack.getType().equals(ResourcePack.Type.BEHAVIOUR_PACK)) {
                        Server.getInstance().getLogger().warning(Server.getInstance().getLanguage()
                                .translateString("nukkit.resources.invalid-type-behaviour", pack.getName(), behaviourPack.getPackName()));

                        continue;
                    }

                    loadedBehaviourPacks.add(behaviourPack);
                    this.packsById.put(behaviourPack.getPackId(), behaviourPack);

                    Server.getInstance().getLogger().info(Server.getInstance().getLanguage()
                            .translateString("nukkit.resources.loaded-behaviour-pack", behaviourPack.getPackName()));
                }
            } catch (IllegalArgumentException e) {
                Server.getInstance().getLogger().warning(Server.getInstance().getLanguage()
                        .translateString("nukkit.resources.fail", pack.getName(), e.getMessage()));
            }
        }

        this.resourcePacks = loadedResourcePacks.toArray(new ResourcePack[0]);
        this.behaviourPacks = loadedBehaviourPacks.toArray(new ResourcePack[0]);

        Server.getInstance().getLogger().info(Server.getInstance().getLanguage()
                .translateString("nukkit.resources.success", String.valueOf(this.resourcePacks.length + this.behaviourPacks.length)));
    }

    public ResourcePack[] getResourcePacks() {
        return this.resourcePacks;
    }

    public ResourcePack[] getBehaviourPacks() {
        return this.behaviourPacks;
    }

    public ResourcePack getPackById(UUID id) {
        return this.packsById.get(id);
    }
}
