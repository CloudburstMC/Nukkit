package cn.nukkit.resourcepacks;

import java.util.UUID;

public interface ResourcePack {

    String getPackName();

    UUID getPackId();

    String getPackVersion();

    int getPackSize();

    byte[] getSha256();

    byte[] getPackChunk(int off, int len);

    default String getEncryptionKey() { // Default for backwards compatibility
        return "";
    }
}
