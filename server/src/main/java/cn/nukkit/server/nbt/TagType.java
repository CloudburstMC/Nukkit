package cn.nukkit.server.nbt;

import cn.nukkit.server.nbt.tag.*;

import java.util.HashMap;
import java.util.Map;

public enum TagType {

    END(EndTag.class),
    BYTE(ByteTag.class),
    SHORT(ShortTag.class),
    INT(IntTag.class),
    LONG(LongTag.class),
    FLOAT(FloatTag.class),
    DOUBLE(DoubleTag.class),
    BYTE_ARRAY(ByteArrayTag.class),
    STRING(StringTag.class),
    LIST(ListTag.class),
    COMPOUND(CompoundTag.class),
    INT_ARRAY(IntArrayTag.class);

    private static final TagType[] BY_ID;
    private static final Map<Class<? extends Tag>, TagType> BY_CLASS = new HashMap<>();

    static {
        BY_ID = values();
        for (TagType type: BY_ID) {
            BY_CLASS.put(type.getTagClass(), type);
        }
    }

    private final Class<? extends Tag> tagClass;

    TagType(Class<? extends Tag> tagClass) {
        this.tagClass = tagClass;
    }

    public static TagType byId(int id) {
        if (id >= 0 && id < BY_ID.length) {
            return BY_ID[id];
        } else {
            throw new IndexOutOfBoundsException("Tag type id must be greater than 0 and less than " + (BY_ID.length - 1));
        }
    }

    public static TagType byClass(Class<? extends Tag> tagClass) {
        TagType type = BY_CLASS.get(tagClass);
        if (type == null) {
            throw new IllegalArgumentException("Tag of class " + tagClass + " does not exist");
        }
        return type;
    }

    public Class<? extends Tag> getTagClass() {
        return tagClass;
    }

    public int getId() {
        return this.ordinal();
    }

    public String getTypeName() {
        return "TAG_" + name();
    }
}
