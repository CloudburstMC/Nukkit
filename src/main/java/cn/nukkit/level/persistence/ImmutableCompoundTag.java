package cn.nukkit.level.persistence;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.tag.*;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Map;

public class ImmutableCompoundTag extends CompoundTag {

    public static final CompoundTag EMPTY = new ImmutableCompoundTag(new CompoundTag());

    public static CompoundTag of(CompoundTag tag) {
        return new ImmutableCompoundTag(tag);
    }

    private final CompoundTag delegate;

    private ImmutableCompoundTag(CompoundTag delegate) {
        this.delegate = delegate;
    }

    @Override
    public void load(NBTInputStream dis) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompoundTag put(String name, Tag tag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompoundTag putByte(String name, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompoundTag putShort(String name, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompoundTag putInt(String name, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompoundTag putLong(String name, long value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompoundTag putFloat(String name, float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompoundTag putDouble(String name, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompoundTag putString(String name, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompoundTag putByteArray(String name, byte[] value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompoundTag putIntArray(String name, int[] value) {
        throw new UnsupportedOperationException();
    }
    @Override

    public CompoundTag putList(ListTag<? extends Tag> listTag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompoundTag putCompound(String name, CompoundTag value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompoundTag putBoolean(String string, boolean val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompoundTag remove(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Tag> T removeAndGet(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(String name) {
        return this.delegate.contains(name);
    }

    @Override
    public Collection<Tag> getAllTags() {
        return this.delegate.getAllTags();
    }

    @Override
    public int getByte(String name) {
        return this.delegate.getByte(name);
    }

    @Override
    public int getShort(String name) {
        return this.delegate.getShort(name);
    }

    @Override
    public int getInt(String name) {
        return this.delegate.getInt(name);
    }
    @Override
    public long getLong(String name) {
        return this.delegate.getLong(name);
    }

    @Override
    public float getFloat(String name) {
        return this.delegate.getFloat(name);
    }

    @Override
    public double getDouble(String name) {
        return this.delegate.getDouble(name);
    }

    @Override
    public String getString(String name) {
        return this.delegate.getString(name);
    }

    @Override
    public byte[] getByteArray(String name) {
        return this.delegate.getByteArray(name);
    }

    @Override
    public byte[] getByteArray(String name, int defaultSize) {
        return this.delegate.getByteArray(name, defaultSize);
    }

    @Override
    public int[] getIntArray(String name) {
        return this.delegate.getIntArray(name);
    }

    @Override
    public CompoundTag getCompound(String name) {
        return this.delegate.getCompound(name);
    }

    @Override
    public ListTag<? extends Tag> getList(String name) {
        return this.delegate.getList(name);
    }

    @Override
    public <T extends Tag> ListTag<T> getList(String name, Class<T> type) {
        return this.delegate.getList(name, type);
    }

    @Override
    public Map<String, Tag> getTags() {
        return this.delegate.getTags();
    }

    @Override
    public Map<String, Object> parseValue() {
        return this.delegate.parseValue();
    }

    @Override
    public boolean getBoolean(String name) {
        return this.delegate.getBoolean(name);
    }

    @Override
    public boolean getBoolean(String name, boolean def) {
        return this.delegate.getBoolean(name, def);
    }

    @Override
    public String toString() {
        return this.delegate.toString();
    }

    @Override
    public void print(PrintStream out) {
        this.delegate.print(out);
    }

    @Override
    public void print(String prefix, PrintStream out) {
        this.delegate.print(prefix, out);
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public CompoundTag copy() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ImmutableCompoundTag) {
            return this.delegate.equals(((ImmutableCompoundTag) obj).delegate);
        }
        return this.delegate.equals(obj);
    }

    @Override
    public boolean exist(String name) {
        return this.delegate.exist(name);
    }

    @Override
    public CompoundTag clone() {
        return this;
    }
}
