package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;

public class CompoundTag extends Tag implements Cloneable {

    private final Map<String, Tag> tags = new HashMap<>();

    public CompoundTag() {
        super("");
    }

    public CompoundTag(String name) {
        super(name);
    }

    @Override
    void write(NBTOutputStream dos) throws IOException {
        for (Map.Entry<String, Tag> entry : this.tags.entrySet()) {
            Tag.writeNamedTag(entry.getValue(), entry.getKey(), dos);
        }

        dos.writeByte(Tag.TAG_End);
    }

    @Override
    public void load(NBTInputStream dis) throws IOException {
        tags.clear();
        Tag tag;
        while ((tag = Tag.readNamedTag(dis)).getId() != Tag.TAG_End) {
            tags.put(tag.getName(), tag);
        }
    }

    public Collection<Tag> getAllTags() {
        return tags.values();
    }

    @Override
    public byte getId() {
        return TAG_Compound;
    }

    public CompoundTag put(String name, Tag tag) {
        tags.put(name, tag.setName(name));
        return this;
    }

    public CompoundTag putByte(String name, int value) {
        tags.put(name, new ByteTag(name, value));
        return this;
    }

    public CompoundTag putShort(String name, int value) {
        tags.put(name, new ShortTag(name, value));
        return this;
    }

    public CompoundTag putInt(String name, int value) {
        tags.put(name, new IntTag(name, value));
        return this;
    }

    public CompoundTag putLong(String name, long value) {
        tags.put(name, new LongTag(name, value));
        return this;
    }

    public CompoundTag putFloat(String name, float value) {
        tags.put(name, new FloatTag(name, value));
        return this;
    }

    public CompoundTag putDouble(String name, double value) {
        tags.put(name, new DoubleTag(name, value));
        return this;
    }

    public CompoundTag putString(String name, String value) {
        tags.put(name, new StringTag(name, value));
        return this;
    }

    public CompoundTag putByteArray(String name, byte[] value) {
        tags.put(name, new ByteArrayTag(name, value));
        return this;
    }

    public CompoundTag putIntArray(String name, int[] value) {
        tags.put(name, new IntArrayTag(name, value));
        return this;
    }

    public CompoundTag putList(ListTag<? extends Tag> listTag) {
        tags.put(listTag.getName(), listTag);
        return this;
    }

    public CompoundTag putCompound(String name, CompoundTag value) {
        tags.put(name, value.setName(name));
        return this;
    }

    public CompoundTag putBoolean(String string, boolean val) {
        putByte(string, val ? 1 : 0);
        return this;
    }

    public Tag get(String name) {
        return tags.get(name);
    }

    public boolean contains(String name) {
        return tags.containsKey(name);
    }

    public CompoundTag remove(String name) {
        tags.remove(name);
        return this;
    }

    public <T extends Tag> T removeAndGet(String name) {
        return (T) tags.remove(name);
    }

    public int getByte(String name) {
        Tag t = tags.get(name);
        if (t == null) return (byte) 0;
        return ((NumberTag) t).getData().intValue();
    }

    public int getShort(String name) {
        Tag t = tags.get(name);
        if (t == null) return 0;
        return ((NumberTag) t).getData().intValue();
    }

    public int getInt(String name) {
        Tag t = tags.get(name);
        if (t == null) return 0;
        return ((NumberTag) t).getData().intValue();
    }

    public long getLong(String name) {
        Tag t = tags.get(name);
        if (t == null) return 0;
        return ((NumberTag) t).getData().longValue();
    }

    public float getFloat(String name) {
        Tag t = tags.get(name);
        if (t == null) return 0f;
        return ((NumberTag) t).getData().floatValue();
    }

    public double getDouble(String name) {
        Tag t = tags.get(name);
        if (t == null) return 0;
        return ((NumberTag) t).getData().doubleValue();
    }

    public String getString(String name) {
        Tag t = tags.get(name);
        if (t == null) return "";
        if (t instanceof NumberTag) {
            return String.valueOf(((NumberTag) t).getData());
        }
        return ((StringTag) t).data;
    }

    public byte[] getByteArray(String name) {
        Tag t = tags.get(name);
        if (t == null) return new byte[0];
        return ((ByteArrayTag) t).data;
    }

    public byte[] getByteArray(String name, int defaultSize) {
        Tag t = tags.get(name);
        if (t == null) return new byte[defaultSize];
        return ((ByteArrayTag) t).data;
    }

    public int[] getIntArray(String name) {
        Tag t = tags.get(name);
        if (t == null) return new int[0];
        return ((IntArrayTag) t).data;
    }

    public CompoundTag getCompound(String name) {
        Tag t = tags.get(name);
        if (t == null) return new CompoundTag(name);
        return (CompoundTag) t;
    }

    public ListTag<? extends Tag> getList(String name) {
        Tag t = tags.get(name);
        if (t == null) return new ListTag<>(name);
        return (ListTag<? extends Tag>) t;
    }

    @SuppressWarnings("unchecked")
    public <T extends Tag> ListTag<T> getList(String name, Class<T> type) {
        Tag t = tags.get(name);
        if (t != null) {
            return (ListTag<T>) t;
        }
        return new ListTag<>(name);
    }

    public Map<String, Tag> getTags() {
        return new HashMap<>(this.tags);
    }

    @Override
    public Map<String, Object> parseValue() {
        Map<String, Object> value = new HashMap<>(this.tags.size());

        for (Entry<String, Tag> entry : this.tags.entrySet()) {
            value.put(entry.getKey(), entry.getValue().parseValue());
        }

        return value;
    }

    public boolean getBoolean(String name) {
        return getByte(name) != 0;
    }

    public boolean getBoolean(String name, boolean def) {
        Tag t = tags.get(name);
        if (t == null) return def;
        return (((NumberTag) t).getData().intValue()) != 0;
    }

    public String toString() {
        StringJoiner joiner = new StringJoiner(",\n\t");
        tags.forEach((key, tag) -> joiner.add('\'' + key + "' : " + tag.toString().replace("\n", "\n\t")));
        return "CompoundTag '" + this.getName() + "' (" + tags.size() + " entries) {\n\t" + joiner.toString() + "\n}";
    }

    public void print(String prefix, PrintStream out) {
        super.print(prefix, out);
        out.println(prefix + '{');
        String orgPrefix = prefix;
        prefix += "   ";
        for (Tag tag : tags.values()) {
            tag.print(prefix, out);
        }
        out.println(orgPrefix + '}');
    }

    public boolean isEmpty() {
        return tags.isEmpty();
    }

    public CompoundTag copy() {
        CompoundTag tag = new CompoundTag(getName());
        for (Entry<String, Tag> entry : tags.entrySet()) {
            tag.put(entry.getKey(), entry.getValue().copy());
        }
        return tag;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            CompoundTag o = (CompoundTag) obj;
            return tags.entrySet().equals(o.tags.entrySet());
        }
        return false;
    }

    /**
     * Check existence of NBT tag
     *
     * @param name - NBT tag Id.
     * @return - true, if tag exists
     */
    public boolean exist(String name) {
        return tags.containsKey(name);
    }

    @Override
    public CompoundTag clone() {
        CompoundTag nbt = new CompoundTag();
        this.getTags().forEach((key, value) -> nbt.put(key, value.copy()));
        return nbt;
    }
}