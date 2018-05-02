package com.nukkitx.server.resourcepack;

import com.nukkitx.api.resourcepack.BehaviorPack;
import com.nukkitx.api.resourcepack.MinecraftPackManifest;
import com.nukkitx.server.resourcepack.loader.file.PackFile;

public class NukkitBehaviorPack extends NukkitResourcePack implements BehaviorPack {

    public NukkitBehaviorPack(MinecraftPackManifest manifest, PackFile packFile) {
        super(manifest, packFile);
    }
}
