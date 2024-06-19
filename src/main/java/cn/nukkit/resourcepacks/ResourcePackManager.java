package cn.nukkit.resourcepacks;

import cn.nukkit.Server;
import cn.nukkit.resourcepacks.loader.ResourcePackLoader;
import cn.nukkit.resourcepacks.loader.ZippedResourcePackLoader;
import cn.nukkit.utils.MainLogger;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.*;

public class ResourcePackManager {

    @Getter
    @Setter
    private int maxChunkSize = 32768; // 32kb is default

    private final Map<UUID, ResourcePack> resourcePacksById = new HashMap<>();
    private final Set<ResourcePack> resourcePacks = new HashSet<>();
    private final Set<ResourcePackLoader> loaders;

    public ResourcePackManager(Set<ResourcePackLoader> loaders) {
        this.loaders = loaders;

        this.reloadPacks();
    }

    public ResourcePackManager(ResourcePackLoader... loaders) {
        this(Sets.newHashSet(loaders));
    }

    public ResourcePackManager(File resourcePacksDir) {
        this(new ZippedResourcePackLoader(resourcePacksDir));
    }

    public ResourcePack[] getResourceStack() {
        return this.resourcePacks.toArray(ResourcePack.EMPTY_ARRAY);
    }

    public ResourcePack getPackById(UUID id) {
        return this.resourcePacksById.get(id);
    }

    public void registerPackLoader(ResourcePackLoader loader) {
        this.loaders.add(loader);
    }

    public void reloadPacks() {
        MainLogger.getLogger().debug("Loading resource packs...");

        this.resourcePacksById.clear();
        this.resourcePacks.clear();
        this.loaders.forEach(loader -> {
            List<ResourcePack> loadedPacks = loader.loadPacks();
            loadedPacks.forEach(pack -> resourcePacksById.put(pack.getPackId(), pack));
            this.resourcePacks.addAll(loadedPacks);
        });

        Server.getInstance().getLogger().info(Server.getInstance().getLanguage()
                .translateString("nukkit.resources.success", String.valueOf(this.resourcePacks.size())));
    }
}
