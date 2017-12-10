package cn.nukkit.api.resourcepack;

public interface ResourcePack {
    String getPackName();

    String getPackId();

    String getPackVersion();

    long getPackSize();

    byte[] getSha256();

    byte[] getPackChunk(int off, int len);
}
