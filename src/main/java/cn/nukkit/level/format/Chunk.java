package cn.nukkit.level.format;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Chunk extends FullChunk {
    public final static byte SECTION_COUNT = 8;

    public abstract boolean isSectionEmpty(int fY);

    public abstract ChunkSection getSection(int fY);

    public abstract boolean setSection(int fY, ChunkSection section);

    public abstract ChunkSection[] getSections();

}
