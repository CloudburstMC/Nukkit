package cn.nukkit.scheduler;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;

/**
 * @author DaPorkchop_
 */
public class BlockUpdateScheduler {
    private final Level level;
    private final LongSet queuedUpdates;
    //porktodo: reset this at end of tick to null
    private volatile LongIterator iterator;

    public BlockUpdateScheduler(Level level, long currentTick) {
        queuedUpdates = LongSets.synchronize(new LongArraySet());
        this.level = level;
    }

    public void threadedTick(long currentTick) {
        synchronized (queuedUpdates) {
            if (iterator == null) {
                iterator = queuedUpdates.iterator();
            }
        }

        while (true) {
            long pos;
            synchronized (iterator) {
                if (!iterator.hasNext()) {
                    break;
                }

                pos = iterator.nextLong();
            }
            int x = Level.getXFrom(pos);
            int y = Level.getYFrom(pos);
            int z = Level.getZFrom(pos);
            if (level.isChunkLoaded(x >> 4, z >> 4)) {
                Block block = level.getBlock(x, y, z);

                block.onUpdate(Level.BLOCK_UPDATE_SCHEDULED);
            } else {
                level.scheduleUpdate(new Vector3(x, y, z), 0);
            }
        }
    }

    public LongSet getPendingBlockUpdates(AxisAlignedBB boundingBox) {
        LongSet set = new LongArraySet();
        synchronized (queuedUpdates)    {
            queuedUpdates.forEach((long entry) -> {
                int x = Level.getXFrom(entry);
                int z = Level.getZFrom(entry);

                if (x >= boundingBox.getMinX() && x < boundingBox.getMaxX() && z >= boundingBox.getMinZ() && z < boundingBox.getMaxZ()) {
                    set.add(entry);
                }
            });
        }
        return set;
    }

    public boolean isBlockTickPending(Vector3 pos) {
        return queuedUpdates.contains(Level.blockHash(pos));
    }

    public boolean contains(long entry) {
        return queuedUpdates.contains(entry);
    }

    public boolean add(long entry)  {
        return queuedUpdates.add(entry);
    }

    public boolean remove(long entry) {
        return queuedUpdates.remove(entry);
    }

    public boolean remove(Vector3 pos) {
        return remove(Level.blockHash(pos));
    }
}
