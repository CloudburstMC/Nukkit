/*
 * This file is licensed under the MIT License (MIT).
 *
 * Copyright (c) 2014 Daniel Ennis <http://aikar.co>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package co.aikar.timings;

import static co.aikar.timings.TimingIdentifier.DEFAULT_GROUP;
import static co.aikar.timings.TimingsManager.*;

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
