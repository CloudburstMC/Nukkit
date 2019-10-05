package cn.nukkit.utils;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

public final class Identifier {
    public static final Identifier EMPTY = new Identifier("", "", ":");

    private static final Pattern PATTERN = Pattern.compile("^([a-zA-Z0-9_]+):([a-zA-Z0-9_]+)$");
    private static final ConcurrentMap<String, Identifier> VALUES = new ConcurrentHashMap<>();
    private static final char NAMESPACE_SEPARATOR = ':';

    static {
        VALUES.put(EMPTY.fullName, EMPTY);
    }

    private final String namespace;
    private final String name;
    private final String fullName;

    private Identifier(String namespace, String name, String fullName) {
        this.namespace = namespace;
        this.name = name;
        this.fullName = fullName;
    }

    public static Identifier fromString(String identifier) {
        Preconditions.checkNotNull(identifier, "identifier");
        String[] parts = identifier.split(":");

        if (parts.length == 1) {
            return from("minecraft", parts[0]);
        } else if (parts.length > 1) {
            return from(parts[0], parts[1]);
        }
        throw new IllegalArgumentException("Invalid identifier");
    }

    public static Identifier from(String space, String name) {
        if (Strings.isNullOrEmpty(space) || Strings.isNullOrEmpty(name)) {
            return EMPTY;
        }
        String spaceLower = space.toLowerCase();
        String nameLower = name.toLowerCase();

        final String fullName = space + NAMESPACE_SEPARATOR + name;
        Preconditions.checkArgument(PATTERN.matcher(fullName).matches(), "Identifier contains invalid characters");

        return VALUES.computeIfAbsent(fullName, s -> new Identifier(spaceLower, nameLower, fullName));
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Identifier)) return false;
        Identifier that = (Identifier) o;
        return Objects.equals(this.fullName, that.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, name);
    }

    @Override
    public String toString() {
        return fullName;
    }
}
