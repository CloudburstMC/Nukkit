package cn.nukkit.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ListTag<T extends Tag> extends Tag {
    public List<T> list = new ArrayList<>();
    public byte type;

    public ListTag() {
        super("");
    }

    public ListTag(String name) {
        super(name);
    }

    void write(DataOutput dos) throws IOException {
        if (list.size() > 0) type = list.get(0).getId();
        else type = 1;

        dos.writeByte(type);
        dos.writeInt(list.size());
        for (T aList : list) aList.write(dos);
    }

    @SuppressWarnings("unchecked")
    void load(DataInput dis) throws IOException {
        type = dis.readByte();
        int size = dis.readInt();

        list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Tag tag = Tag.newTag(type, null);
            tag.load(dis);
            list.add((T) tag);
        }
    }

    public byte getId() {
        return TAG_List;
    }

    public String toString() {
        return "" + list.size() + " entries of type " + Tag.getTagName(type);
    }

    public void print(String prefix, PrintStream out) {
        super.print(prefix, out);

        out.println(prefix + "{");
        String orgPrefix = prefix;
        prefix += "   ";
        for (T aList : list) aList.print(prefix, out);
        out.println(orgPrefix + "}");
    }

    public void add(T tag) {
        type = tag.getId();
        list.add(tag);
    }

    public T get(int index) {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

    @Override
    public Tag copy() {
        ListTag<T> res = new ListTag<>(getName());
        res.type = type;
        for (T t : list) {
            @SuppressWarnings("unchecked")
            T copy = (T) t.copy();
            res.list.add(copy);
        }
        return res;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            ListTag o = (ListTag) obj;
            if (type == o.type) {
                return list.equals(o.list);
            }
        }
        return false;
    }

}
