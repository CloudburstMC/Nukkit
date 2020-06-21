package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class CompoundTag extends Tag implements Cloneable {

    private Map<String, Tag> tags = new HashMap<>();

    public CompoundTag() {
        super("");
    }

    public CompoundTag(final String name) {
        super(name);
    }

    public CompoundTag(final String name, final Map<String, Tag> tags) {
        super(name);
        this.tags = tags;
    }

    @Override
    public byte getId() {
        return Tag.TAG_Compound;
    }

    @Override
    public boolean equals(final Object obj) {
        if (super.equals(obj)) {
            final CompoundTag o = (CompoundTag) obj;
            return this.tags.entrySet().equals(o.tags.entrySet());
        }
        return false;
    }

    public String toString() {
        final StringJoiner joiner = new StringJoiner(",\n\t");
        this.tags.forEach((key, tag) -> joiner.add('\'' + key + "' : " + tag.toString().replace("\n", "\n\t")));
        return "CompoundTag '" + this.getName() + "' (" + this.tags.size() + " entries) {\n\t" + joiner.toString() + "\n}";
    }

    @Override
    public void print(String prefix, final PrintStream out) {
        super.print(prefix, out);
        out.println(prefix + "{");
        final String orgPrefix = prefix;
        prefix += "   ";
        for (final Tag tag : this.tags.values()) {
            tag.print(prefix, out);
        }
        out.println(orgPrefix + "}");
    }

    @Override
    public CompoundTag copy() {
        final CompoundTag tag = new CompoundTag(this.getName());
        for (final String key : this.tags.keySet()) {
            tag.put(key, this.tags.get(key).copy());
        }
        return tag;
    }

    @Override
    public Map<String, Object> parseValue() {
        final Map<String, Object> value = new HashMap<>(this.tags.size());

        for (final Map.Entry<String, Tag> entry : this.tags.entrySet()) {
            value.put(entry.getKey(), entry.getValue().parseValue());
        }

        return value;
    }

    @Override
    public void write(final NBTOutputStream dos) throws IOException {

        for (final Map.Entry<String, Tag> entry : this.tags.entrySet()) {
            Tag.writeNamedTag(entry.getValue(), entry.getKey(), dos);
        }

        dos.writeByte(Tag.TAG_End);
    }

    @Override
    public void load(final NBTInputStream dis) throws IOException {
        this.tags.clear();
        Tag tag;
        while ((tag = Tag.readNamedTag(dis)).getId() != Tag.TAG_End) {
            this.tags.put(tag.getName(), tag);
        }
    }

    public Collection<Tag> getAllTags() {
        return this.tags.values();
    }

    public CompoundTag put(final String name, final Tag tag) {
        this.tags.put(name, tag.setName(name));
        return this;
    }

    public CompoundTag putByte(final String name, final int value) {
        this.tags.put(name, new ByteTag(name, value));
        return this;
    }

    public CompoundTag putShort(final String name, final int value) {
        this.tags.put(name, new ShortTag(name, value));
        return this;
    }

    public CompoundTag putInt(final String name, final int value) {
        this.tags.put(name, new IntTag(name, value));
        return this;
    }

    public CompoundTag putLong(final String name, final long value) {
        this.tags.put(name, new LongTag(name, value));
        return this;
    }

    public CompoundTag putFloat(final String name, final float value) {
        this.tags.put(name, new FloatTag(name, value));
        return this;
    }

    public CompoundTag putDouble(final String name, final double value) {
        this.tags.put(name, new DoubleTag(name, value));
        return this;
    }

    public CompoundTag putString(final String name, final String value) {
        this.tags.put(name, new StringTag(name, value));
        return this;
    }

    public CompoundTag putByteArray(final String name, final byte[] value) {
        this.tags.put(name, new ByteArrayTag(name, value));
        return this;
    }

    public CompoundTag putIntArray(final String name, final int[] value) {
        this.tags.put(name, new IntArrayTag(name, value));
        return this;
    }

    public CompoundTag putList(final ListTag<? extends Tag> listTag) {
        this.tags.put(listTag.getName(), listTag);
        return this;
    }

    public CompoundTag putCompound(final String name, final CompoundTag value) {
        this.tags.put(name, value.setName(name));
        return this;
    }

    public CompoundTag putBoolean(final String string, final boolean val) {
        this.putByte(string, val ? 1 : 0);
        return this;
    }

    public Tag get(final String name) {
        return this.tags.get(name);
    }

    public boolean contains(final String name) {
        return this.tags.containsKey(name);
    }

    public CompoundTag remove(final String name) {
        this.tags.remove(name);
        return this;
    }

    public <T extends Tag> T removeAndGet(final String name) {
        return (T) this.tags.remove(name);
    }

    public int getByte(final String name) {
        if (!this.tags.containsKey(name)) {
            return (byte) 0;
        }
        return ((NumberTag) this.tags.get(name)).getData().intValue();
    }

    public Optional<Integer> getByteValue(final String name) {
        if (!this.tags.containsKey(name)) {
            return Optional.empty();
        }
        return Optional.of(((NumberTag) this.tags.get(name)).getData().intValue());
    }

    public int getShort(final String name) {
        if (!this.tags.containsKey(name)) {
            return 0;
        }
        return ((NumberTag) this.tags.get(name)).getData().intValue();
    }

    public int getInt(final String name) {
        if (!this.tags.containsKey(name)) {
            return 0;
        }
        return ((NumberTag) this.tags.get(name)).getData().intValue();
    }

    public Optional<Integer> getIntValue(final String name) {
        if (!this.tags.containsKey(name)) {
            return Optional.empty();
        }
        return Optional.of(((NumberTag) this.tags.get(name)).getData().intValue());
    }

    public long getLong(final String name) {
        if (!this.tags.containsKey(name)) {
            return 0;
        }
        return ((NumberTag) this.tags.get(name)).getData().longValue();
    }

    public float getFloat(final String name) {
        if (!this.tags.containsKey(name)) {
            return (float) 0;
        }
        return ((NumberTag) this.tags.get(name)).getData().floatValue();
    }

    public double getDouble(final String name) {
        if (!this.tags.containsKey(name)) {
            return 0;
        }
        return ((NumberTag) this.tags.get(name)).getData().doubleValue();
    }

    public String getString(final String name) {
        if (!this.tags.containsKey(name)) {
            return "";
        }
        final Tag tag = this.tags.get(name);
        if (tag instanceof NumberTag) {
            return String.valueOf(((NumberTag) tag).getData());
        }
        return ((StringTag) tag).data;
    }

    public Optional<String> getStringValue(final String name) {
        if (!this.tags.containsKey(name)) {
            return Optional.empty();
        }
        final Tag tag = this.tags.get(name);
        if (tag instanceof NumberTag) {
            return Optional.ofNullable(String.valueOf(((NumberTag) tag).getData()));
        }
        return Optional.ofNullable(((StringTag) tag).data);
    }

    public Optional<long[]> getLongArrayValue(final String name) {
        if (!this.tags.containsKey(name)) {
            return Optional.empty();
        }
        return Optional.of(((LongArrayTag) this.tags.get(name)).data);
    }

    public Optional<byte[]> getByteArrayValue(final String name) {
        if (!this.tags.containsKey(name)) {
            return Optional.empty();
        }
        return Optional.of(((ByteArrayTag) this.tags.get(name)).data);
    }

    public byte[] getByteArray(final String name) {
        if (!this.tags.containsKey(name)) {
            return new byte[0];
        }
        return ((ByteArrayTag) this.tags.get(name)).data;
    }

    public int[] getIntArray(final String name) {
        if (!this.tags.containsKey(name)) {
            return new int[0];
        }
        return ((IntArrayTag) this.tags.get(name)).data;
    }

    public Optional<int[]> getIntArrayValue(final String name) {
        if (!this.tags.containsKey(name)) {
            return Optional.empty();
        }
        return Optional.of(((IntArrayTag) this.tags.get(name)).data);
    }

    public Optional<CompoundTag> getCompoundValue(final String name) {
        if (!this.tags.containsKey(name)) {
            return Optional.empty();
        }
        return Optional.of((CompoundTag) this.tags.get(name));
    }

    public CompoundTag getCompound(final String name) {
        if (!this.tags.containsKey(name)) {
            return new CompoundTag(name);
        }
        return (CompoundTag) this.tags.get(name);
    }

    @SuppressWarnings("unchecked")
    public ListTag<? extends Tag> getList(final String name) {
        if (!this.tags.containsKey(name)) {
            return new ListTag<>(name);
        }
        return (ListTag<? extends Tag>) this.tags.get(name);
    }

    @SuppressWarnings("unchecked")
    public Optional<ListTag<? extends Tag>> getListValue(final String name) {
        if (!this.tags.containsKey(name)) {
            return Optional.empty();
        }
        return Optional.of((ListTag<? extends Tag>) this.tags.get(name));
    }

    @SuppressWarnings("unchecked")
    public <T extends Tag> ListTag<T> getList(final String name, final Class<T> type) {
        if (this.tags.containsKey(name)) {
            return (ListTag<T>) this.tags.get(name);
        }
        return new ListTag<>(name);
    }

    public Map<String, Tag> getTags() {
        return new HashMap<>(this.tags);
    }

    public boolean getBoolean(final String name) {
        return this.getByte(name) != 0;
    }

    public boolean isEmpty() {
        return this.tags.isEmpty();
    }

    /**
     * Check existence of NBT tag
     *
     * @param name - NBT tag Id.
     * @return - true, if tag exists
     */
    public boolean exist(final String name) {
        return this.tags.containsKey(name);
    }

    @Override
    public CompoundTag clone() {
        final CompoundTag nbt = new CompoundTag();
        this.getTags().forEach((key, value) -> nbt.put(key, value.copy()));
        return nbt;
    }

}
