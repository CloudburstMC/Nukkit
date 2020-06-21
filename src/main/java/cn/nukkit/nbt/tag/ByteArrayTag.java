package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import cn.nukkit.utils.Binary;
import java.io.IOException;
import java.util.Arrays;

public class ByteArrayTag extends Tag {

    public byte[] data;

    public ByteArrayTag(final String name) {
        super(name);
    }

    public ByteArrayTag(final String name, final byte[] data) {
        super(name);
        this.data = data;
    }

    public byte[] getData() {
        return this.data;
    }

    @Override
    public byte getId() {
        return Tag.TAG_Byte_Array;
    }

    @Override
    public boolean equals(final Object obj) {
        if (super.equals(obj)) {
            final ByteArrayTag byteArrayTag = (ByteArrayTag) obj;
            return this.data == null && byteArrayTag.data == null || this.data != null && Arrays.equals(this.data, byteArrayTag.data);
        }
        return false;
    }

    @Override
    public String toString() {
        return "ByteArrayTag " + this.getName() + " (data: 0x" + Binary.bytesToHexString(this.data, true) + " [" + this.data.length + " bytes])";
    }

    @Override
    public Tag copy() {
        final byte[] cp = new byte[this.data.length];
        System.arraycopy(this.data, 0, cp, 0, this.data.length);
        return new ByteArrayTag(this.getName(), cp);
    }

    @Override
    public byte[] parseValue() {
        return this.data;
    }

    @Override
    void write(final NBTOutputStream dos) throws IOException {
        if (this.data == null) {
            dos.writeInt(0);
            return;
        }
        dos.writeInt(this.data.length);
        dos.write(this.data);
    }

    @Override
    void load(final NBTInputStream dis) throws IOException {
        final int length = dis.readInt();
        this.data = new byte[length];
        dis.readFully(this.data);
    }

}
