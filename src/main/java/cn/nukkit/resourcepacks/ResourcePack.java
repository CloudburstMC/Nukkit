package cn.nukkit.resourcepacks;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

import java.util.UUID;

public interface ResourcePack {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    ResourcePack[] EMPTY_ARRAY = new ResourcePack[0];
    
    String getPackName();

    UUID getPackId();

    String getPackVersion();

    int getPackSize();

    byte[] getSha256();

    byte[] getPackChunk(int off, int len);
}
