package cn.nukkit.server.level.util;

import cn.nukkit.api.level.chunk.Chunk;

import java.util.Comparator;

public class AroundPointChunkComparator implements Comparator<Chunk> {
    private final int spawnX;
    private final int spawnZ;

    public AroundPointChunkComparator(int spawnX, int spawnZ) {
        this.spawnX = spawnX;
        this.spawnZ = spawnZ;
    }

    @Override
    public int compare(Chunk o1, Chunk o2) {
        return Integer.compare(distance(o1.getX(), o1.getZ()), distance(o2.getX(), o2.getZ()));
    }

    private int distance(int x, int z) {
        int dx = spawnX - x;
        int dz = spawnZ - z;
        return dx * dx + dz * dz;
    }
}
