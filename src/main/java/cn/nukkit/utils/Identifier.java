package cn.nukkit.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import net.daporkchop.lib.common.ref.Ref;
import net.daporkchop.lib.common.ref.ThreadRef;
import net.daporkchop.lib.random.impl.FastPRandom;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;

//@JsonDeserialize(using = Identifier.Deserializer.class)
@JsonSerialize(using = ToStringSerializer.class)
public final class Identifier implements Comparable<Identifier> {
    private static final char NAMESPACE_SEPARATOR = ':';

    public static final Identifier EMPTY = new Identifier("", "", String.valueOf(NAMESPACE_SEPARATOR));

    private static final Ref<Matcher> MATCHER_CACHE = ThreadRef.regex("^(?>minecraft:)?(?>([a-z0-9_]*)" + NAMESPACE_SEPARATOR + ")?([a-zA-Z0-9_]*)$");

    private static final Lock READ_LOCK;
    private static final Lock WRITE_LOCK;

    private static final Map<String, Identifier> VALUES = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    static {
        ReadWriteLock lock = new ReentrantReadWriteLock();
        READ_LOCK = lock.readLock();
        WRITE_LOCK = lock.writeLock();
    }

    private final String namespace;
    private final String name;
    private final String fullName;

    private final transient int hashCode;

    private Identifier(String namespace, String name, String fullName) {
        this.namespace = namespace;
        this.name = name;
        this.fullName = fullName;

        this.hashCode = FastPRandom.mix32(fullName.chars().asLongStream().reduce(0L, (a, b) -> FastPRandom.mix64(a + b)));
    }

    @JsonCreator
    public static Identifier fromString(String identifier) {
        if (Preconditions.checkNotNull(identifier, "identifier").isEmpty()) {
            //check for empty before using matcher
            return EMPTY;
        }
        Matcher matcher = MATCHER_CACHE.get().reset(identifier);
        Preconditions.checkArgument(matcher.find(), "Invalid identifier: \"%s\"", identifier);

        Identifier id;
        READ_LOCK.lock();
        try {
            id = VALUES.get(identifier);
        } finally {
            READ_LOCK.unlock();
        }

        if (id == null) {
            String namespace = matcher.group(1);
            String name = matcher.group(2);
            String fullName = namespace == null && !identifier.startsWith("minecraft:") ? "minecraft:" + name : identifier;

            //create new identifier instance
            WRITE_LOCK.lock();
            try {
                //try get again in case identifier was created while obtaining write lock
                if ((id = VALUES.get(identifier)) == null) {
                    id = new Identifier(namespace == null ? "minecraft" : namespace, name, fullName);
                    if (namespace == null) {
                        //also put it into the map without minecraft: in the key to facilitate faster lookups when the prefix is omitted
                        VALUES.put(name, id);
                    }
                    VALUES.put(fullName, id);
                }
            } finally {
                WRITE_LOCK.unlock();
            }
        }
        return id;
    }

    public static Identifier from(String space, String name) {
        if (Strings.isNullOrEmpty(space)) {
            if (Strings.isNullOrEmpty(name))    {
                return EMPTY;
            } else {
                //assume minecraft namespace
                return fromString(name);
            }
        }
        return fromString(space + NAMESPACE_SEPARATOR + name);
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public int compareTo(Identifier o) {
        return this.fullName.compareTo(o.fullName);
    }

    @Override
    public int hashCode() {
        //provides significantly better hash distribution, good for hash tables
        return this.hashCode;
    }

    static final class Deserializer extends JsonDeserializer<Identifier>    {
        @Override
        public Identifier deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return Identifier.fromString(p.getText());
        }
    }
}
