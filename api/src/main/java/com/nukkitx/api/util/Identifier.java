package com.nukkitx.api.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import javax.annotation.Nonnull;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Identifier {
    public static final Identifier EMPTY = new Identifier("", "", ":");

    private static final Pattern PATTERN = Pattern.compile("^([a-zA-Z0-9_]+):([a-zA-Z0-9_]+)$");
    private static final ConcurrentMap<String, Identifier> VALUES = new ConcurrentHashMap<>();
    private static final char NAMESPACE_SEPARATOR = ':';

    private final String space;
    private final String name;
    private final String fullName;

    private Identifier(String space, String name, String fullName) {
        this.space = space;
        this.name = name;
        this.fullName = fullName;
    }

    public static Identifier fromString(@Nonnull String identifier) {
        Preconditions.checkNotNull(identifier, "identifier");
        Matcher matcher = PATTERN.matcher(identifier);
        Preconditions.checkArgument(matcher.find(), "Invalid identifier string");
        return from(matcher.group(1), matcher.group(2));
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

    public String getSpace() {
        return space;
    }

    public String getFullName() {
        return fullName;
    }
}
