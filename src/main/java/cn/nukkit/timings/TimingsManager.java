package cn.nukkit.timings;

import cn.nukkit.Server;

import java.util.*;

/**
 * @author Tee7even
 */
public class TimingsManager {
    static final Map<TimingIdentifier, Timing> TIMING_MAP = Collections.synchronizedMap(new HashMap<>(256, 0.5f));

    static final Queue<Timing> TIMINGS = new ArrayDeque<>();
    static final ArrayDeque<TimingsHistory.MinuteReport> MINUTE_REPORTS = new ArrayDeque<>();

    static Queue<TimingsHistory> HISTORY = new BoundedQueue<>(12);

    static Timing CURRENT;

    static long timingStart = 0;
    static long historyStart = 0;
    static boolean needsFullReset = false;
    static boolean needsRecheckEnabled = false;

    static void reset() {
        needsFullReset = true;
    }

    /**
     * Called every tick to count the number of times a timer caused TPS loss.
     */
    static void tick() {
        if (Timings.isTimingsEnabled()) {
            boolean violated = Timings.fullServerTickTimer.isViolated();

            for (Timing timing : TIMINGS) {
                if (timing.isSpecial()) {
                    // Called manually
                    continue;
                }

                timing.tick(violated);
            }

            TimingsHistory.playerTicks += Server.getInstance().getOnlinePlayers().size();
            TimingsHistory.timedTicks++;
        }
    }

    static void recheckEnabled() {
        synchronized (TIMING_MAP) {
            TIMING_MAP.values().forEach(Timing::checkEnabled);
        }

        needsRecheckEnabled = false;
    }

    static void resetTimings() {
        if (needsFullReset) {
            // Full resets need to re-check every handlers enabled state
            // Timing map can be modified from async so we must sync on it.
            synchronized (TIMING_MAP) {
                for (Timing timing : TIMING_MAP.values()) {
                    timing.reset(true);
                }
            }

            HISTORY.clear();
            needsFullReset = false;
            needsRecheckEnabled = false;
            timingStart = System.currentTimeMillis();
        } else {
            // Soft resets only need to act on timings that have done something
            // Handlers can only be modified on main thread.
            for (Timing timing : TIMINGS) {
                timing.reset(false);
            }
        }

        TIMINGS.clear();
        MINUTE_REPORTS.clear();

        TimingsHistory.resetTicks(true);
        historyStart = System.currentTimeMillis();
    }

    static Timing getTiming(String name) {
        return getTiming(null, name, null);
    }

    static Timing getTiming(String group, String name, Timing groupTiming) {
        TimingIdentifier id = new TimingIdentifier(group, name, groupTiming);
        return TIMING_MAP.computeIfAbsent(id, k -> new Timing(id));
    }

    static final class BoundedQueue<E> extends LinkedList<E> {
        final int maxSize;

        BoundedQueue(int maxSize) {
            if (maxSize <= 0) {
                throw new IllegalArgumentException("maxSize must be greater than zero");
            }

            this.maxSize = maxSize;
        }

        @Override
        public boolean add(E e) {
            if (this.size() == maxSize) {
                this.remove();
            }

            return super.add(e);
        }
    }
}
