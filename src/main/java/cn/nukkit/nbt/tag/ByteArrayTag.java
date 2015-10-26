package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import cn.nukkit.utils.Binary;

import java.io.IOException;
import java.util.Arrays;

public class ByteArrayTag extends Tag {
    public byte[] data;

    public ByteArrayTag(String name) {
        super(name);
    }

    public ByteArrayTag(String name, byte[] data) {
        super(name);
        this.data = data;
    }

    @Override
    void write(NBTOutputStream dos) throws IOException {
        dos.writeInt(data.length);
        dos.write(data);
    }

    @Override
    void load(NBTInputStream dis) throws IOException {
        int length = dis.readInt();
        data = new byte[length];
        dis.readFully(data);
    }

    @Override
    public byte getId() {
        return TAG_Byte_Array;
    }

    @Override
    public String toString() {
        return "ByteArrayTag " + this.getName() + " (data: 0x" + Binary.bytesToHexString(data, true) + " [" + data.length + " bytes])";
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            ByteArrayTag byteArrayTag = (ByteArrayTag) obj;
            return ((data == null && byteArrayTag.data == null) || (data != null && Arrays.equals(data, byteArrayTag.data)));
        }
        return false;
    }

    @Override
    public Tag copy() {
        byte[] cp = new byte[data.length];
        System.arraycopy(data, 0, cp, 0, data.length);
        return new ByteArrayTag(getName(), cp);
    }
}
