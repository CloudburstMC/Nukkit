package cn.nukkit.resourcepacks;

import cn.nukkit.Server;
import com.google.common.io.Files;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourcePackManager {
    private ResourcePack[] resourcePacks;
    private Map<String, ResourcePack> resourcePacksById = new HashMap<>();

    public ResourcePackManager(File path) {
        if (!path.exists()) {
            path.mkdirs();
        } else if (!path.isDirectory()) {
            throw new IllegalArgumentException(Server.getInstance().getLanguage()
                    .translateString("nukkit.resources.invalid-path", path.getName()));
        }

        List<ResourcePack> loadedResourcePacks = new ArrayList<>();
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
                            Server.getInstance().getLogger().warning(Server.getInstance().getLanguage()
                                    .translateString("nukkit.resources.unknown-format", pack.getName()));
                            break;
                    }
                }

                if (resourcePack != null) {
                    loadedResourcePacks.add(resourcePack);
                    this.resourcePacksById.put(resourcePack.getPackId(), resourcePack);
                }
            } catch (IllegalArgumentException e) {
                Server.getInstance().getLogger().warning(Server.getInstance().getLanguage()
                        .translateString("nukkit.resources.fail", pack.getName(), e.getMessage()));
            }
        }

        this.resourcePacks = loadedResourcePacks.toArray(new ResourcePack[loadedResourcePacks.size()]);
        Server.getInstance().getLogger().info(Server.getInstance().getLanguage()
                .translateString("nukkit.resources.success", String.valueOf(this.resourcePacks.length)));
    }

    public ResourcePack[] getResourceStack() {
        return this.resourcePacks;
    }

    public ResourcePack getPackById(String id) {
        return this.resourcePacksById.get(id);
    }
}
