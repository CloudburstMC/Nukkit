package cn.nukkit.server.resourcepack;

import cn.nukkit.api.resourcepack.BehaviorPack;
import cn.nukkit.api.resourcepack.MinecraftPackManifest;
import cn.nukkit.server.resourcepack.loader.file.PackFile;

public class NukkitBehaviorPack extends NukkitResourcePack implements BehaviorPack {

    public NukkitBehaviorPack(MinecraftPackManifest manifest, PackFile packFile) {
        super(manifest, packFile);
    }
}
