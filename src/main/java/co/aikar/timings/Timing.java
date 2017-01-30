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

import java.util.HashMap;
import java.util.Map;

public class Timing implements AutoCloseable {
    private static int idPool = 1;
    final int id = idPool++;

    final String name;
    private final boolean verbose;

    final Map<Integer, TimingData> children = new HashMap<>();
    private Timing parent;

    private final Timing groupTiming;
    final TimingData record;

    private long start = 0;
    private int timingDepth = 0;
    private boolean added;
    boolean timed;
    boolean enabled;

    Timing(TimingIdentifier id) {
        if (id.name.startsWith("##")) {
            this.verbose = true;
            this.name = id.name.substring(3);
        } else {
            this.name = id.name;
            this.verbose = false;
        }

        this.record = new TimingData(this.id);
        this.groupTiming = id.groupTiming;

        TimingIdentifier.getGroup(id.group).timings.add(this);
        this.checkEnabled();
    }

    final void checkEnabled() {
        this.enabled = Timings.isTimingsEnabled() && (!this.verbose || Timings.isVerboseEnabled());
    }

    void tick(boolean violated) {
        if (this.timingDepth != 0 || this.record.curTickCount == 0) {
            this.timingDepth = 0;
            this.start = 0;
            return;
        }

        this.record.tick(violated);
        for (TimingData data : this.children.values()) {
            data.tick(violated);
        }
    }

    public Timing startTiming() {
        if (!this.enabled) {
            return this;
        }

        if (++this.timingDepth == 1) {
            this.start = System.nanoTime();
            this.parent = TimingsManager.CURRENT;
            TimingsManager.CURRENT = this;
        }

        return this;
    }

    public void stopTiming() {
        if (!this.enabled) {
            return;
        }

        if (--this.timingDepth == 0 && this.start != 0) {
            this.addDiff(System.nanoTime() - this.start);
            this.start = 0;
        }
    }

    public void abort() {
        if (this.enabled && this.timingDepth > 0) {
            this.start = 0;
        }
    }

    void addDiff(long diff) {
        if (TimingsManager.CURRENT == this) {
            TimingsManager.CURRENT = this.parent;
            if (this.parent != null) {
                if (!this.parent.children.containsKey(this.id))
                    this.parent.children.put(this.id, new TimingData(this.id));
                this.parent.children.get(this.id).add(diff);
            }
        }

        this.record.add(diff);
        if (!this.added) {
            this.added = true;
            this.timed = true;
            TimingsManager.TIMINGS.add(this);
        }

        if (this.groupTiming != null) {
            this.groupTiming.addDiff(diff);

            if (!this.groupTiming.children.containsKey(this.id))
                this.groupTiming.children.put(this.id, new TimingData(this.id));
            this.groupTiming.children.get(this.id).add(diff);
        }
    }

    void reset(boolean full) {
        this.record.reset();
        if (full) {
            this.timed = false;
        }
        this.start = 0;
        this.timingDepth = 0;
        this.added = false;
        this.children.clear();
        this.checkEnabled();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Timing && this == o);
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    //For try-with-resources
    @Override
    public void close() {
        this.stopTiming();
    }

    boolean isSpecial() {
        return this == Timings.fullServerTickTimer || this == Timings.timingsTickTimer;
    }
}
