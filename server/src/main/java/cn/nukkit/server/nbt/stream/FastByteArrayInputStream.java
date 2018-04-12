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

package cn.nukkit.server.nbt.stream;

import java.io.InputStream;

/**
 * Simple, fast and repositionable byte-array input stream.
 *
 * <p><strong>Warning</strong>: this class implements the correct semantics
 * of {@link #read(byte[], int, int)} as described in {@link java.io.InputStream}.
 * The implementation given in {@link java.io.ByteArrayInputStream} is broken,
 * but it will never be fixed because it's too late.
 *
 * @author Sebastiano Vigna
 */

public class FastByteArrayInputStream extends InputStream {

    /**
     * The array backing the input stream.
     */
    public byte[] array;

    /**
     * The first valid entry.
     */
    public int offset;

    /**
     * The number of valid bytes in {@link #array} starting from {@link #offset}.
     */
    public int length;

    /**
     * The current position as a distance from {@link #offset}.
     */
    private int position;

    /**
     * The current mark as a position, or -1 if no mark exists.
     */
    private int mark;

    /**
     * Creates a new array input stream using a given array fragment.
     *
     * @param array  the backing array.
     * @param offset the first valid entry of the array.
     * @param length the number of valid bytes.
     */
    public FastByteArrayInputStream(final byte[] array, final int offset, final int length) {
        this.array = array;
        this.offset = offset;
        this.length = length;
    }

    /**
     * Creates a new array input stream using a given array.
     *
     * @param array the backing array.
     */
    public FastByteArrayInputStream(final byte[] array) {
        this(array, 0, array.length);
    }

    public boolean markSupported() {
        return true;
    }

    public void reset() {
        position = mark;
    }

    /**
     * Closing a fast byte array input stream has no effect.
     */
    public void close() {
    }

    public void mark(final int dummy) {
        mark = position;
    }

    public int available() {
        return length - position;
    }

    public long skip(long n) {
        if (n <= length - position) {
            position += (int) n;
            return n;
        }
        n = length - position;
        position = length;
        return n;
    }

    public int read() {
        if (length == position) return -1;
        return array[offset + position++] & 0xFF;
    }

    /**
     * Reads bytes from this byte-array input stream as
     * specified in {@link java.io.InputStream#read(byte[], int, int)}.
     * Note that the implementation given in {@link java.io.ByteArrayInputStream#read(byte[], int, int)}
     * will return -1 on a zero-length read at EOF, contrarily to the specification. We won't.
     */

    public int read(final byte b[], final int offset, final int length) {
        if (this.length == this.position) return length == 0 ? 0 : -1;
        final int n = Math.min(length, this.length - this.position);
        System.arraycopy(array, this.offset + this.position, b, offset, n);
        this.position += n;
        return n;
    }

    public long position() {
        return position;
    }

    public void position(final long newPosition) {
        position = (int) Math.min(newPosition, length);
    }

    public long length() {
        return length;
    }
}