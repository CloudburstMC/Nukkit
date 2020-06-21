package cn.nukkit.nbt.tag;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TagType {

    TAG_End(0, EndTag.class),
    TAG_Byte(1, ByteTag.class),
    TAG_Short(2, ByteTag.class),
    TAG_Int(3, IntTag.class),
    TAG_Long(4, LongTag.class),
    TAG_Float(5, FloatTag.class),
    TAG_Double(6, DoubleTag.class),
    TAG_Byte_Array(7, ByteArrayTag.class),
    TAG_String(8, StringTag.class),
    TAG_List(9, ListTag.class),
    TAG_Compound(10, CompoundTag.class),
    TAG_Int_Array(11, IntArrayTag.class),
    TAG_Long_Array(12, LongArrayTag.class),
    TAG_Short_Array(13, ShortArrayTag.class);

    private final int typeId;

    private final Class<? extends Tag> clazz;

    public static TagType getById(final byte id) {
        return TagType.getById((int) id);
    }

    public static TagType getById(final int id) {
        return Arrays.stream(TagType.values())
            .filter(tagType -> tagType.typeId == id)
            .findFirst()
            .orElse(TagType.TAG_End);
    }
}
