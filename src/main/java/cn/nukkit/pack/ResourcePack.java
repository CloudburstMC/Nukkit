package cn.nukkit.pack;

import cn.nukkit.network.protocol.ResourcePackDataInfoPacket;
import cn.nukkit.pack.loader.PackLoader;

public class ResourcePack extends Pack {
    public static final Pack.Factory FACTORY = ResourcePack::new;

    private ResourcePack(PackLoader loader, PackManifest manifest, PackManifest.Module module) {
        super(loader, manifest, module);
    }

    @Override
    public int getType() {
        return ResourcePackDataInfoPacket.TYPE_RESOURCE;
    }

    @Override
    public String toString() {
        return "Resource" + super.toString();
    }
}
