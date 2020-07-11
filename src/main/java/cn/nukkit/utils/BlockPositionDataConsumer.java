package cn.nukkit.utils;

@FunctionalInterface
public interface BlockPositionDataConsumer<D> {
    void accept(int x, int y, int z, D data);
}
