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

import javax.annotation.Nonnull;
import java.io.*;

public class LittleEndianDataOutputStream implements DataOutput, Closeable {
    private final DataOutputStream stream;

    public LittleEndianDataOutputStream(OutputStream stream) {
        this.stream = new DataOutputStream(stream);
    }

    public LittleEndianDataOutputStream(DataOutputStream stream) {
        this.stream = stream;
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    @Override
    public void write(int bytes) throws IOException {
        stream.write(bytes);
    }

    @Override
    public void write(@Nonnull byte[] bytes) throws IOException {
        stream.write(bytes);
    }

    @Override
    public void write(@Nonnull byte[] bytes, int offset, int length) throws IOException {
        stream.write(bytes, offset, length);
    }

    @Override
    public void writeBoolean(boolean value) throws IOException {
        stream.writeBoolean(value);
    }

    @Override
    public void writeByte(int value) throws IOException {
        stream.writeByte(value);
    }

    @Override
    public void writeShort(int value) throws IOException {
        stream.writeShort(Integer.reverseBytes(value) >> 16);
    }

    @Override
    public void writeChar(int value) throws IOException {
        stream.writeChar(Character.reverseBytes((char) value));
    }

    @Override
    public void writeInt(int value) throws IOException {
        stream.writeInt(Integer.reverseBytes(value));
    }

    @Override
    public void writeLong(long value) throws IOException {
        stream.writeLong(Long.reverseBytes(value));
    }

    @Override
    public void writeFloat(float value) throws IOException {
        writeInt(Float.floatToIntBits(value));
    }

    @Override
    public void writeDouble(double value) throws IOException {
        writeLong(Double.doubleToLongBits(value));
    }

    @Override
    public void writeBytes(@Nonnull String string) throws IOException {
        stream.writeBytes(string);
    }

    @Override
    public void writeChars(@Nonnull String string) throws IOException {
        stream.writeChars(string);
    }

    @Override
    public void writeUTF(@Nonnull String string) throws IOException {
        stream.writeUTF(string);
    }
}
