package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

public class ListTag<T extends Tag> extends Tag {

    public byte type;

    private List<T> list = new ArrayList<>();

    public ListTag() {
        super("");
    }

    public ListTag(final String name) {
        super(name);
    }

    public ListTag(final String name, final List<T> list) {
        super(name);
        this.setList(list);
    }

    public List<T> getList() {
        return this.list;
    }

    public void setList(final List<T> list) {
        this.list = list;
    }

    public ListTag<T> add(final T tag) {
        this.type = tag.getId();
        tag.setName("");
        this.list.add(tag);
        return this;
    }

    public ListTag<T> add(final int index, final T tag) {
        this.type = tag.getId();
        tag.setName("");

        if (index >= this.list.size()) {
            this.list.add(index, tag);
        } else {
            this.list.set(index, tag);
        }
        return this;
    }

    public T get(final int index) {
        return this.list.get(index);
    }

    public List<T> getAll() {
        return new ArrayList<>(this.list);
    }

    public void setAll(final List<T> tags) {
        this.list = new ArrayList<>(tags);
    }

    public void remove(final T tag) {
        this.list.remove(tag);
    }

    public void remove(final int index) {
        this.list.remove(index);
    }

    public void removeAll(final Collection<T> tags) {
        this.list.remove(tags);
    }

    public int size() {
        return this.list.size();
    }

    @Override
    public byte getId() {
        return Tag.TAG_List;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(final Object obj) {
        if (super.equals(obj)) {
            final ListTag o = (ListTag) obj;
            if (this.type == o.type) {
                return this.list.equals(o.list);
            }
        }
        return false;
    }

    @Override
    public String toString() {
        final StringJoiner joiner = new StringJoiner(",\n\t");
        this.list.forEach(tag -> joiner.add(tag.toString().replace("\n", "\n\t")));
        return "ListTag '" + this.getName() + "' (" + this.list.size() + " entries of type " + Tag.getTagName(this.type) + ") {\n\t" + joiner.toString() + "\n}";
    }

    @Override
    public void print(String prefix, final PrintStream out) {
        super.print(prefix, out);

        out.println(prefix + "{");
        final String orgPrefix = prefix;
        prefix += "   ";
        for (final T aList : this.list) {
            aList.print(prefix, out);
        }
        out.println(orgPrefix + "}");
    }

    @Override
    public Tag copy() {
        final ListTag<T> res = new ListTag<>(this.getName());
        res.type = this.type;
        for (final T t : this.list) {
            @SuppressWarnings("unchecked") final T copy = (T) t.copy();
            res.list.add(copy);
        }
        return res;
    }

    @Override
    public List<Object> parseValue() {
        final List<Object> value = new ArrayList<>(this.list.size());

        for (final T t : this.list) {
            value.add(t.parseValue());
        }

        return value;
    }

    @Override
    void write(final NBTOutputStream dos) throws IOException {
        if (this.list.size() > 0) {
            this.type = this.list.get(0).getId();
        } else {
            this.type = 1;
        }

        dos.writeByte(this.type);
        dos.writeInt(this.list.size());
        for (final T aList : this.list) {
            aList.write(dos);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    void load(final NBTInputStream dis) throws IOException {
        this.type = dis.readByte();
        final int size = dis.readInt();

        this.list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            final Tag tag = Tag.newTag(this.type, null);
            tag.load(dis);
            tag.setName("");
            this.list.add((T) tag);
        }
    }

}
