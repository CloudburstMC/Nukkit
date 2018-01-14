package cn.nukkit.server.network;

import com.google.common.base.Preconditions;
import jdk.internal.jline.internal.Nullable;
import org.apache.logging.log4j.util.Strings;

import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class PacketType {
    public static final PacketType UNKNOWN = new PacketType("UNKNOWN", (short) 0);
    private static final ConcurrentMap<String, PacketType> TYPES = new ConcurrentHashMap<>();
    private static final AtomicInteger packetNum = new AtomicInteger(1);
    private final String name;
    private final short intPacketType;

    private PacketType(String name, short intPacketType) {
        if (Strings.isEmpty(name)) {
            throw new IllegalArgumentException("Illegal null or empty PacketType name.");
        } else {
            this.name = name;
            this.intPacketType = intPacketType;
            if (TYPES.putIfAbsent(name, this) != null) {
                throw new IllegalStateException("PacketType " + name + " has already been defined.");
            }
        }
    }

    public static PacketType forName(String name) {
        PacketType type = TYPES.get(name);
        if (type != null) {
            return type;
        } else {
            try {
                if (packetNum.get() > Short.MAX_VALUE) {
                    throw new StackOverflowError("Registered over 32767 PacketTypes!");
                }

                return new PacketType(name, (short) packetNum.getAndIncrement());
            } catch (IllegalStateException var4) {
                return TYPES.get(name);
            }
        }
    }

    public static PacketType getPacketType(String name) {
        return TYPES.get(name);
    }

    @Nullable
    public static PacketType toPacketType(String sArg) {
        return toPacketType(sArg, null);
    }

    public static PacketType toPacketType(String name, PacketType defaultPacketType) {
        if (name == null) {
            return defaultPacketType;
        } else {
            PacketType type = TYPES.get(toUpperCase(name));
            return type == null ? defaultPacketType : type;
        }
    }

    private static String toUpperCase(String name) {
        return name.toUpperCase(Locale.ENGLISH);
    }

    public static PacketType[] values() {
        Collection<PacketType> values = TYPES.values();
        return values.toArray(new PacketType[values.size()]);
    }

    public static PacketType valueOf(String name) {
        Preconditions.checkNotNull(name, "name");
        String typeName = toUpperCase(name);
        PacketType type = TYPES.get(typeName);
        if (type != null) {
            return type;
        } else {
            throw new IllegalArgumentException("Unknown type constant [" + typeName + "].");
        }
    }

    public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name) {
        return Enum.valueOf(enumType, name);
    }

    public short intPacketType() {
        return this.intPacketType;
    }

    @Override
    public PacketType clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof PacketType && other == this;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    public String name() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    protected Object readResolve() {
        return valueOf(name);
    }
}