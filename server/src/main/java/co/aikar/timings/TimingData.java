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

import cn.nukkit.server.timings.JsonUtil;
import com.google.gson.JsonArray;

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
