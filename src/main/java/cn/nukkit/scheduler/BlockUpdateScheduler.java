package cn.nukkit.scheduler;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author DaPorkchop_
 */
public class BlockUpdateScheduler {
    private final Level level;
    private final Long2ObjectMap<LongSet> queuedUpdates;
    private final Lock tickLock = new ReentrantLock();
    private volatile LongIterator iterator;
    private volatile long currentTick;

    public BlockUpdateScheduler(Level level, long currentTick) {
        queuedUpdates = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap<>());
        this.level = level;
        this.currentTick = currentTick;
    }

    public void threadedTick(long currentTick) {
        tickLock.lock();
        if (iterator == null) {
            LongSet queuedUpdatesThisTick = queuedUpdates.get(currentTick);
            if (queuedUpdatesThisTick == null) {
                tickLock.unlock();
                return;
            }
            iterator = queuedUpdatesThisTick.iterator();
        }

        while (iterator.hasNext()) {
            long pos = iterator.nextLong();
            tickLock.unlock();

            int x = Level.getXFrom(pos);
            int y = Level.getYFrom(pos);
            int z = Level.getZFrom(pos);
            if (level.isChunkLoaded(x >> 4, z >> 4)) {
                Block block = level.getBlock(x, y, z);

                block.onUpdate(Level.BLOCK_UPDATE_SCHEDULED);
            } else {
                //porktodo: this does nothing, we somehow need to force-enqueue these block updates for unloaded and potentially non-existant chunks
                level.scheduleUpdate(new Vector3(x, y, z), 0);
            }

            tickLock.lock();
        }

        this.currentTick = currentTick;
        tickLock.unlock();
    }

    public Long2ObjectMap<LongSet> getPendingBlockUpdates(AxisAlignedBB boundingBox) {
        Long2ObjectMap<LongSet> map = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap<>());
        synchronized (queuedUpdates) {
            queuedUpdates.forEach((tick, updates) -> {
                LongSet set = LongSets.synchronize(new LongOpenHashSet());
                updates.forEach((long entry) -> {
                    int x = Level.getXFrom(entry);
                    int z = Level.getZFrom(entry);

                    if (x >= boundingBox.getMinX() && x < boundingBox.getMaxX() && z >= boundingBox.getMinZ() && z < boundingBox.getMaxZ()) {
                        set.add(entry);
                    }
                });
                if (!set.isEmpty()) {
                    map.put((long) tick, set);
                }
            });
        }
        return map;
    }

    public boolean isBlockTickPending(Vector3 pos) {
        return contains(Level.blockHash(pos));
    }

    public boolean contains(long hash) {
        for (LongSet set : queuedUpdates.values())  {
            if (set.contains(hash)) {
                return true;
            }
        }
        return false;
    }

    public boolean add(long entry, long tick) {
        if (tick <= currentTick) {
            return false;
        }
        LongSet set = queuedUpdates.get(tick);
        if (set == null)    {
            set = LongSets.synchronize(new LongOpenHashSet());
            queuedUpdates.put(tick, set);
        }
        return set.add(entry);
    }

    public boolean remove(long entry, long tick) {
        LongSet set = queuedUpdates.get(tick);
        if (set == null)    {
            return false;
        }
        return set.remove(entry);
    }

    public boolean remove(Vector3 pos, long tick) {
        return remove(Level.blockHash(pos), tick);
    }

    public void doPostTick()    {
        synchronized (queuedUpdates)    {
            queuedUpdates.long2ObjectEntrySet().removeIf(entry -> entry.getLongKey() < currentTick);
        }

        iterator = null;
    }
}
