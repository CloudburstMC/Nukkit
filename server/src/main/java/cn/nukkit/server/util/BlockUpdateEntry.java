package cn.nukkit.server.util;

import cn.nukkit.server.block.Block;
import cn.nukkit.server.math.Vector3;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockUpdateEntry implements Comparable<BlockUpdateEntry> {
    private static long entryID = 0;
    public final Vector3 pos;
    public final Block block;
    public final long id;
    public int priority;
    public long delay;

    public BlockUpdateEntry(Vector3 pos, Block block) {
        this.pos = pos;
        this.block = block;
        this.id = entryID++;
    }

    public BlockUpdateEntry(Vector3 pos, Block block, long delay, int priority) {
        this.id = entryID++;
        this.pos = pos;
        this.priority = priority;
        this.delay = delay;
        this.block = block;
    }

    @Override
    public int compareTo(BlockUpdateEntry entry) {
        return this.delay < entry.delay ? -1 : (this.delay > entry.delay ? 1 : (this.priority != entry.priority ? this.priority - entry.priority : (Long.compare(this.id, entry.id))));
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BlockUpdateEntry)) {
            return false;
        } else {
            BlockUpdateEntry entry = (BlockUpdateEntry) object;
            return this.pos.equals(entry.pos) && Block.equals(this.block, entry.block, false);
        }
    }
}