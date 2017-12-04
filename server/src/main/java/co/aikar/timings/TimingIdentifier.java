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

import java.util.ArrayDeque;
import java.util.IdentityHashMap;
import java.util.Map;

class TimingIdentifier {
    static final Map<String, TimingGroup> GROUP_MAP = new IdentityHashMap<>(64);
    static final TimingGroup DEFAULT_GROUP = getGroup("Nukkit");

    final String group;
    final String name;
    final Timing groupTiming;
    private final int hashCode;

    TimingIdentifier(String group, String name, Timing groupTiming) {
        this.group = group != null ? group.intern() : DEFAULT_GROUP.name;
        this.name = name.intern();
        this.groupTiming = groupTiming;
        this.hashCode = (31 * this.group.hashCode()) + this.name.hashCode();
    }

    static TimingGroup getGroup(String name) {
        if (name == null) {
            return DEFAULT_GROUP;
        }

        return GROUP_MAP.computeIfAbsent(name, k -> new TimingGroup(name));
    }

    @Override
    @SuppressWarnings("all")
    public boolean equals(Object o) {
        if (o == null || !(o instanceof TimingIdentifier)) {
            return false;
        }

        TimingIdentifier that = (TimingIdentifier) o;
        //Using intern() method on strings makes possible faster string comparison with ==
        return this.group == that.group && this.name == that.name;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    static class TimingGroup {
        private static int idPool = 1;
        final int id = idPool++;

        final String name;
        ArrayDeque<Timing> timings = new ArrayDeque<>(64);

        TimingGroup(String name) {
            this.name = name.intern();
        }
    }
}
