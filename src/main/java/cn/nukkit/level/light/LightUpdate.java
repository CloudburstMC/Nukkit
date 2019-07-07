package cn.nukkit.level.light;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.generic.BaseChunk;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;

import java.util.*;

/**
 * author: dktapps
 */
public abstract class LightUpdate {

    protected ChunkManager level;

    protected Map<BlockVector3, Integer> updateNodes = new HashMap<>();

    protected Queue<BlockVector3> spreadQueue;

    protected Set<BlockVector3> spreadVisited = new HashSet<>();

    protected Queue<Entry> removalQueue;

    protected Set<BlockVector3> removalVisited = new HashSet<>();

    public BaseChunk currentChunk;

    public ChunkSection currentSection;

    protected int currentX;
    protected int currentY;
    protected int currentZ;

    public LightUpdate(ChunkManager level) {
        this.level = level;
        this.removalQueue = new ArrayDeque<>();
        this.spreadQueue = new ArrayDeque<>();
    }

    abstract protected int getLight(int x, int y, int z);

    abstract protected void setLight(int x, int y, int z, int level);

    public void setAndUpdateLight(int x, int y, int z, int newLevel) {
        this.updateNodes.put(new BlockVector3(x, y, z), newLevel);
    }

    private void prepareNodes() {
        for (Map.Entry<BlockVector3, Integer> entry : updateNodes.entrySet()) {
            BlockVector3 pos = entry.getKey();
            int newLevel = entry.getValue();

            if (this.moveTo(pos)) {
                int oldLevel = this.getLight(pos.x, pos.y, pos.z);

                if (oldLevel != newLevel) {
                    this.setLight(pos.x, pos.y, pos.z, newLevel);

                    if (oldLevel < newLevel) {
                        this.spreadVisited.add(pos);
                        this.spreadQueue.add(pos);
                    } else { //light removed
                        this.removalVisited.add(pos);
                        this.removalQueue.add(new Entry(pos, oldLevel));
                    }
                }
            }
        }
    }

    public void execute() {
        prepareNodes();

        while (!this.removalQueue.isEmpty()) {
            Entry entry = this.removalQueue.poll();

            for (BlockFace face : BlockFace.values()) {
                BlockVector3 pos = entry.pos.getSide(face);

                if (moveTo(pos)) {
                    this.computeRemoveLight(pos.x, pos.y, pos.z, entry.oldLight);
                }
            }
        }

        while (!spreadQueue.isEmpty()) {
            BlockVector3 pos = this.spreadQueue.poll();
            int x = pos.x;
            int y = pos.y;
            int z = pos.z;

            if (!moveTo(x, y, z)) {
                continue;
            }

            int newAdjacentLight = this.getLight(x, y, z);
            if (newAdjacentLight <= 0) {
                continue;
            }

            for (BlockFace face : BlockFace.values()) {
                BlockVector3 side = pos.getSide(face);

                if (moveTo(side)) {
                    this.computeSpreadLight(side.x, side.y, side.z, newAdjacentLight);
                }
            }
        }
    }

    protected void computeRemoveLight(int x, int y, int z, int oldAdjacentLevel) {
        int current = this.getLight(x, y, z);

        BlockVector3 index;
        if (current != 0 && current < oldAdjacentLevel) {
            this.setLight(x, y, z, 0);

            if (!removalVisited.contains(index = new BlockVector3(x, y, z))) {
                removalVisited.add(index);
                if (current > 1) {
                    this.removalQueue.add(new Entry(index, current));
                }
            }
        } else if (current >= oldAdjacentLevel) {
            if (!spreadVisited.contains(index = new BlockVector3(x, y, z))) {
                spreadVisited.add(index);
                spreadQueue.add(index);
            }
        }
    }

    protected void computeSpreadLight(int x, int y, int z, int newAdjacentLevel) {
        int current = this.getLight(x, y, z);
        int potentialLight = newAdjacentLevel - Block.lightFilter[this.currentSection.getBlockId(x & 0x0f, y & 0x0f, z & 0x0f)];

        if (current < potentialLight) {
            this.setLight(x, y, z, potentialLight);

            BlockVector3 index;
            if (!spreadVisited.contains(index = new BlockVector3(x, y, z))) {
                spreadVisited.add(index);
                spreadQueue.add(index);
            }
        }
    }

    private class Entry {

        public BlockVector3 pos;

        public Integer oldLight;

        public Entry(BlockVector3 pos) {
            this(pos, null);
        }

        public Entry(BlockVector3 pos, Integer light) {
            this.pos = pos;
            this.oldLight = light;
        }
    }

    protected boolean moveTo(BlockVector3 pos) {
        return moveTo(pos.x, pos.y, pos.z);
    }

    protected boolean moveTo(int x, int y, int z) {
        if (this.currentChunk == null || this.currentX != (x >> 4) || this.currentZ != (z >> 4)) {
            this.currentX = x >> 4;
            this.currentZ = z >> 4;
            this.currentSection = null;

            this.currentChunk = (BaseChunk) this.level.getChunk(this.currentX, this.currentZ);
            if (this.currentChunk == null) {
                return false;
            }
        }

        if (this.currentSection == null || this.currentY != (y >> 4)) {
            this.currentY = y >> 4;

            this.currentSection = this.currentChunk.getSection(y >> 4);

            return !(this.currentSection instanceof EmptyChunkSection);
        }

        return true;
    }

}