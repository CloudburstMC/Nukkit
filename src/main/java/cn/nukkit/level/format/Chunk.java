package cn.nukkit.level.format;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface Chunk extends FullChunk {
    public final static byte SECTION_COUNT = 8;

    public abstract boolean isSectionEmpty(float fY);

    public abstract ChunkSection getSection(float fY);

    public abstract boolean setSection(float fY, ChunkSection section);

    public abstract ChunkSection[] getSections();

}
