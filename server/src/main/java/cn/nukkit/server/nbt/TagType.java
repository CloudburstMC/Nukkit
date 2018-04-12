/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

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
        return ordinal();
    }

    public String getTypeName() {
        return "TAG_" + name();
    }
}
