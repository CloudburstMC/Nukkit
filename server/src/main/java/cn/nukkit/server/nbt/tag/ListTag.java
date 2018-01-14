package cn.nukkit.server.nbt.tag;

import cn.nukkit.server.nbt.TagType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ListTag<T extends Tag> extends Tag<List<T>> {
    private final Class<T> tagClass;
    private final List<T> value;

    public ListTag(String name, Class<T> tagClass, List<T> value) {
        super(name);
        this.value = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(value, "value")));
        this.tagClass = tagClass;
    }

    public Class<T> getTagClass() {
        return tagClass;
    }

    @Override
    public List<T> getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), value, tagClass);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListTag that = (ListTag) o;
        return Objects.equals(value, that.value) &&
                Objects.equals(tagClass, that.tagClass) &&
                Objects.equals(getName(), that.getName());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TAG_List").append(super.toString()).append(value.size()).append(" entries of type ").append(TagType.byClass(tagClass).getTypeName()).append("\r\n{\r\n");
        for (Tag tag : value) {
            builder.append("   ").append(tag.toString().replaceAll("\r\n", "\r\n   ")).append("\r\n");
        }
        builder.append("}");
        return builder.toString();
    }
}
