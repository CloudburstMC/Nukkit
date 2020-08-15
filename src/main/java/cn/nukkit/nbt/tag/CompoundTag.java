package cn.nukkit.nbt.tag;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.Map.Entry;

public class CompoundTag extends Tag implements Cloneable {
    private final Map<String, Tag> tags;

    public CompoundTag() {
        this("");
    }

    public CompoundTag(String name) {
        this(name, new HashMap<>());
    }

    public CompoundTag(Map<String, Tag> tags) {
        this("", tags);
    }

    public CompoundTag(String name, Map<String, Tag> tags) {
        super(name);
        this.tags = tags;
    }

    @Override
    public void write(NBTOutputStream dos) throws IOException {

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
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean containsCompound(String name) {
        return tags.get(name) instanceof CompoundTag;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean containsString(String name) {
        return tags.get(name) instanceof StringTag;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean containsIntArray(String name) {
        return tags.get(name) instanceof IntArrayTag;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean containsByteArray(String name) {
        return tags.get(name) instanceof ByteArrayTag;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean containsNumber(String name) {
        return tags.get(name) instanceof NumberTag;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean containsList(String name) {
        return tags.get(name) instanceof ListTag;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean containsList(String name, byte type) {
        Tag tag = tags.get(name);
        if (!(tag instanceof ListTag)) {
            return false;
        }
        ListTag<?> list = (ListTag<?>)tag;
        byte listType = list.type;
        return listType == 0 || listType == type;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean containsByte(String name) {
        return tags.get(name) instanceof ByteTag;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean containsShort(String name) {
        return tags.get(name) instanceof ShortTag;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean containsInt(String name) {
        return tags.get(name) instanceof IntTag;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean containsDouble(String name) {
        return tags.get(name) instanceof DoubleTag;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean containsFloat(String name) {
        return tags.get(name) instanceof FloatTag;
    }

    public CompoundTag remove(String name) {
        tags.remove(name);
        return this;
    }

    public <T extends Tag> T removeAndGet(String name) {
        return (T) tags.remove(name);
    }


    public int getByte(String name) {
        if (!tags.containsKey(name)) return (byte) 0;
        return ((NumberTag) tags.get(name)).getData().intValue();
    }

    public int getShort(String name) {
        if (!tags.containsKey(name)) return 0;
        return ((NumberTag) tags.get(name)).getData().intValue();
    }

    public int getInt(String name) {
        if (!tags.containsKey(name)) return 0;
        return ((NumberTag) tags.get(name)).getData().intValue();
    }

    public long getLong(String name) {
        if (!tags.containsKey(name)) return 0;
        return ((NumberTag) tags.get(name)).getData().longValue();
    }

    public float getFloat(String name) {
        if (!tags.containsKey(name)) return (float) 0;
        return ((NumberTag) tags.get(name)).getData().floatValue();
    }

    public double getDouble(String name) {
        if (!tags.containsKey(name)) return 0;
        return ((NumberTag) tags.get(name)).getData().doubleValue();
    }

    public String getString(String name) {
        if (!tags.containsKey(name)) return "";
        Tag tag = tags.get(name);
        if (tag instanceof NumberTag) {
            return String.valueOf(((NumberTag) tag).getData());
        }
        return ((StringTag) tag).data;
    }

    public byte[] getByteArray(String name) {
        if (!tags.containsKey(name)) return new byte[0];
        return ((ByteArrayTag) tags.get(name)).data;
    }

    public int[] getIntArray(String name) {
        if (!tags.containsKey(name)) return new int[0];
        return ((IntArrayTag) tags.get(name)).data;
    }

    public CompoundTag getCompound(String name) {
        if (!tags.containsKey(name)) return new CompoundTag(name);
        return (CompoundTag) tags.get(name);
    }

    public ListTag<? extends Tag> getList(String name) {
        if (!tags.containsKey(name)) return new ListTag<>(name);
        return (ListTag<? extends Tag>) tags.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T extends Tag> ListTag<T> getList(String name, Class<T> type) {
        if (tags.containsKey(name)) {
            return (ListTag<T>) tags.get(name);
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

    public String toString() {
        StringJoiner joiner = new StringJoiner(",\n\t");
        tags.forEach((key, tag) -> joiner.add('\'' + key + "' : " + tag.toString().replace("\n", "\n\t")));
        return "CompoundTag '" + this.getName() + "' (" + tags.size() + " entries) {\n\t" + joiner.toString() + "\n}";
    }

    public void print(String prefix, PrintStream out) {
        super.print(prefix, out);
        out.println(prefix + "{");
        String orgPrefix = prefix;
        prefix += "   ";
        for (Tag tag : tags.values()) {
            tag.print(prefix, out);
        }
        out.println(orgPrefix + "}");
    }

    public boolean isEmpty() {
        return tags.isEmpty();
    }

    public CompoundTag copy() {
        CompoundTag tag = new CompoundTag(getName());
        for (String key : tags.keySet()) {
            tag.put(key, tags.get(key).copy());
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
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tags);
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
