package cn.nukkit.level.format;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class FullChunk {
    //todo

    public abstract int getX();

    public abstract int getZ();

    public abstract void setX();

    public abstract void setZ();

    public abstract LevelProvider getProvider();

    public abstract void setProvider(LevelProvider provider);

}
