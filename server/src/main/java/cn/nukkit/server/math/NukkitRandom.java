/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.math;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.CRC32;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class NukkitRandom {
    protected long seed;

    public NukkitRandom() {
        this(-1);
    }

    public NukkitRandom(long seeds) {
        if (seeds == -1) {
            seeds = System.currentTimeMillis() / 1000L;
        }
        this.setSeed(seeds);
    }

    public void setSeed(long seeds) {
        CRC32 crc32 = new CRC32();
        ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
        buffer.putInt((int) seeds);
        crc32.update(buffer.array());
        this.seed = crc32.getValue();
    }

    public int nextSignedInt() {
        int t = (((int) ((this.seed * 65535) + 31337) >> 8) + 1337);
        this.seed ^= t;
        return t;
    }

    public int nextInt() {
        return this.nextSignedInt() & 0x7fffffff;
    }

    public double nextDouble() {
        return (double) this.nextInt() / 0x7fffffff;
    }

    public float nextFloat() {
        return (float) this.nextInt() / 0x7fffffff;
    }

    public float nextSignedFloat() {
        return (float) this.nextInt() / 0x7fffffff;
    }

    public double nextSignedDouble() {
        return (double) this.nextSignedInt() / 0x7fffffff;
    }

    public boolean nextBoolean() {
        return (this.nextSignedInt() & 0x01) == 0;
    }

    public int nextRange() {
        return nextRange(0, 0x7fffffff);
    }

    public int nextRange(int start) {
        return nextRange(start, 0x7fffffff);
    }

    public int nextRange(int start, int end) {
        return start + (this.nextInt() % (end + 1 - start));
    }

    public int nextBoundedInt(int bound) {
        return this.nextInt() % bound;
    }
}
