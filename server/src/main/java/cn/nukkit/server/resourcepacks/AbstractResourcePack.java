package cn.nukkit.server.resourcepacks;

import cn.nukkit.api.resourcepack.ResourcePack;

import java.util.List;

public abstract class AbstractResourcePack implements ResourcePack {
    protected NukkitResourcePackPackManifest manifest;

    @Override
    public String getPackName() {
        return manifest.getHeader().getName();
    }

    @Override
    public String getPackId() {
        return manifest.getHeader().getUuid();
    }

    @Override
    public String getPackVersion() {
        List<String> versions = manifest.getHeader().getVersion();

        return String.join(".", versions.get(0), versions.get(1), versions.get(2));
    }
}
