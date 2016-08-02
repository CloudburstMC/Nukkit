package cn.nukkit.timings;

import com.google.gson.JsonArray;

/**
 * @author Tee7even
 */
class TimingData {
    private int id;
    int count = 0;
    private int lagCount = 0;
    long totalTime = 0;
    private long lagTotalTime = 0;

    int curTickCount = 0;
    int curTickTotal = 0;

    TimingData(int id) {
        this.id = id;
    }

    TimingData(TimingData data) {
        this.id = data.id;
        this.count = data.count;
        this.lagCount = data.lagCount;
        this.totalTime = data.totalTime;
        this.lagTotalTime = data.lagTotalTime;
    }

    void add(long diff) {
        ++this.curTickCount;
        this.curTickTotal += diff;
    }

    void tick(boolean violated) {
        this.count += this.curTickCount;
        this.totalTime += this.curTickTotal;

        if (violated) {
            this.lagCount += this.curTickCount;
            this.lagTotalTime += this.curTickTotal;
        }

        this.curTickCount = 0;
        this.curTickTotal = 0;
    }

    void reset() {
        this.count = 0;
        this.lagCount = 0;
        this.totalTime = 0;
        this.lagTotalTime = 0;
        this.curTickCount = 0;
        this.curTickTotal = 0;
    }

    protected TimingData clone() {
        return new TimingData(this);
    }

    JsonArray export() {
        JsonArray json = JsonUtil.toArray(this.id, this.count, this.totalTime);
        if (this.lagCount > 0) {
            json.add(this.lagCount);
            json.add(this.lagTotalTime);
        }
        return json;
    }
}
