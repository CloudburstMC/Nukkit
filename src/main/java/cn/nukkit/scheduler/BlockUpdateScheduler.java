package cn.nukkit.scheduler;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockUpdateEntry;
import it.unimi.dsi.fastutil.longs.LongArrayList;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BlockUpdateScheduler {

    private final Level level;
    private long lastTick;
    private final Map<Long, LinkedHashSet<BlockUpdateEntry>> queuedUpdates = new ConcurrentHashMap<>();

    private Set<BlockUpdateEntry> pendingUpdates;

    public BlockUpdateScheduler(Level level, long currentTick) {
        this.lastTick = currentTick;
        this.level = level;
    }

    public synchronized void tick(long currentTick) {
        // Should only perform once, unless ticks were skipped
        if (currentTick - lastTick < Short.MAX_VALUE) {// Arbitrary
            for (long tick = lastTick + 1; tick <= currentTick; tick++) {
                perform(tick);
            }
        } else {
            LongArrayList times = new LongArrayList(queuedUpdates.keySet());
            Collections.sort(times);
            for (long tick : times) {
                if (tick <= currentTick) {
                    perform(tick);
                } else {
                    break;
                }
            }
        }
        lastTick = currentTick;
    }

    private void perform(long tick) {
        try {
            lastTick = tick;
            Set<BlockUpdateEntry> updates = pendingUpdates = queuedUpdates.remove(tick);
            if (updates != null) {
                for (BlockUpdateEntry entry : updates) {
                    if (level.isAreaLoaded(new SimpleAxisAlignedBB(entry.pos, entry.pos))) {
                        Block block = level.getBlock(entry.pos, entry.block.getLayer(), true);

                        if (Block.equals(block, entry.block, false)) {
                            block.onUpdate(Level.BLOCK_UPDATE_SCHEDULED);
                        }
                    } else {
                        level.scheduleUpdate(entry.block, entry.pos, 0);
                    }
                }
            }
        } finally {
            pendingUpdates = null;
        }
    }

    public Set<BlockUpdateEntry> getPendingBlockUpdates(AxisAlignedBB boundingBox) {
        Set<BlockUpdateEntry> set = null;

        for (Map.Entry<Long, LinkedHashSet<BlockUpdateEntry>> tickEntries : this.queuedUpdates.entrySet()) {
            LinkedHashSet<BlockUpdateEntry> tickSet = tickEntries.getValue();
            for (BlockUpdateEntry update : tickSet) {
                Vector3 pos = update.pos;

                if (pos.getX() >= boundingBox.getMinX() && pos.getX() < boundingBox.getMaxX() && pos.getZ() >= boundingBox.getMinZ() && pos.getZ() < boundingBox.getMaxZ()) {
                    if (set == null) {
                        set = new LinkedHashSet<>();
                    }

                    set.add(update);
                }
            }
        }

        return set;
    }

    public boolean isBlockTickPending(Vector3 pos, Block block) {
        Set<BlockUpdateEntry> tmpUpdates = pendingUpdates;
        if (tmpUpdates == null || tmpUpdates.isEmpty()) return false;
        return tmpUpdates.contains(new BlockUpdateEntry(pos, block));
    }

    private long getMinTime(BlockUpdateEntry entry) {
        return Math.max(entry.delay, lastTick + 1);
    }

    public void add(BlockUpdateEntry entry) {
        long time = getMinTime(entry);
        LinkedHashSet<BlockUpdateEntry> updateSet = queuedUpdates.get(time);
        if (updateSet == null) {
            LinkedHashSet<BlockUpdateEntry> tmp = queuedUpdates.putIfAbsent(time, updateSet = new LinkedHashSet<>());
            if (tmp != null) updateSet = tmp;
        }
        updateSet.add(entry);
    }

    public boolean contains(BlockUpdateEntry entry) {
        for (Map.Entry<Long, LinkedHashSet<BlockUpdateEntry>> tickUpdateSet : queuedUpdates.entrySet()) {
            if (tickUpdateSet.getValue().contains(entry)) {
                return true;
            }
        }
        return false;
    }

    public boolean remove(BlockUpdateEntry entry) {
        for (Map.Entry<Long, LinkedHashSet<BlockUpdateEntry>> tickUpdateSet : queuedUpdates.entrySet()) {
            if (tickUpdateSet.getValue().remove(entry)) {
                return true;
            }
        }
        return false;
    }

    public boolean remove(Vector3 pos) {
        for (Map.Entry<Long, LinkedHashSet<BlockUpdateEntry>> tickUpdateSet : queuedUpdates.entrySet()) {
            if (tickUpdateSet.getValue().remove(new BlockUpdateEntry(pos, null))) {
                return true;
            }
        }
        return false;
    }
}
