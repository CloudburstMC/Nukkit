package cn.nukkit.timings;

import static cn.nukkit.timings.TimingIdentifier.DEFAULT_GROUP;
import static cn.nukkit.timings.TimingsManager.*;

/**
 * @author Tee7even
 */
public class FullServerTickTiming extends Timing {
    private static final TimingIdentifier IDENTIFIER = new TimingIdentifier(DEFAULT_GROUP.name, "Full Server Tick", null);
    final TimingData minuteData;
    double avgFreeMemory = -1D;
    double avgUsedMemory = -1D;

    FullServerTickTiming() {
        super(IDENTIFIER);
        this.minuteData = new TimingData(this.id);

        TIMING_MAP.put(IDENTIFIER, this);
    }

    @Override
    public Timing startTiming() {
        if (TimingsManager.needsFullReset) {
            TimingsManager.resetTimings();
        } else if (TimingsManager.needsRecheckEnabled) {
            TimingsManager.recheckEnabled();
        }
        super.startTiming();
        return this;
    }

    @Override
    public void stopTiming() {
        super.stopTiming();
        if (!this.enabled) {
            return;
        }

        if (TimingsHistory.timedTicks % 20 == 0) {
            final Runtime runtime = Runtime.getRuntime();
            double usedMemory = runtime.totalMemory() - runtime.freeMemory();
            double freeMemory = runtime.maxMemory() - usedMemory;

            if (this.avgFreeMemory == -1) {
                this.avgFreeMemory = freeMemory;
            } else {
                this.avgFreeMemory = (this.avgFreeMemory * (59 / 60D)) + (freeMemory * (1 / 60D));
            }

            if (this.avgUsedMemory == -1) {
                this.avgUsedMemory = usedMemory;
            } else {
                this.avgUsedMemory = (this.avgUsedMemory * (59 / 60D)) + (usedMemory * (1 / 60D));
            }
        }

        long start = System.nanoTime();
        TimingsManager.tick();
        long diff = System.nanoTime() - start;

        CURRENT = Timings.timingsTickTimer;
        Timings.timingsTickTimer.addDiff(diff);
        //addDiff for timingsTickTimer incremented this, bring it back down to 1 per tick.
        this.record.curTickCount--;
        this.minuteData.curTickTotal = this.record.curTickTotal;
        this.minuteData.curTickCount = 1;
        boolean violated = isViolated();
        this.minuteData.tick(violated);
        Timings.timingsTickTimer.tick(violated);
        tick(violated);

        if (TimingsHistory.timedTicks % 1200 == 0) {
            MINUTE_REPORTS.add(new TimingsHistory.MinuteReport());
            TimingsHistory.resetTicks(false);
            this.minuteData.reset();
        }

        if (TimingsHistory.timedTicks % Timings.getHistoryInterval() == 0) {
            TimingsManager.HISTORY.add(new TimingsHistory());
            TimingsManager.resetTimings();
        }
    }

    boolean isViolated() {
        return this.record.curTickTotal > 50000000;
    }
}
