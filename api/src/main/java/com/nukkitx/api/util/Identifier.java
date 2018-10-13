package com.nukkitx.api.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
@ParametersAreNonnullByDefault
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Identifier {
    private static final Map<String, Identifier> IDENTIFIERS = new ConcurrentHashMap<>();
    private static final char NAMESPACE_SEPARATOR = ':';

    private final String fullName;
    private final String namespace;
    private final String name;

    public static Identifier get(String fullName) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(fullName), "fullName");
        fullName = lower(fullName);
        String[] parts = fullName.split(Character.toString(NAMESPACE_SEPARATOR));
        Preconditions.checkArgument(parts.length == 2, "Invalid fullName");

        return IDENTIFIERS.computeIfAbsent(fullName, s -> new Identifier(s, parts[0], parts[1]));
    }

    public static Identifier get(final String namespace, final String name) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(namespace), "namespace");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "name");
        final String namespaceLower = lower(namespace);
        final String nameLower = lower(name);

        final String fullName = namespaceLower + NAMESPACE_SEPARATOR + nameLower;

        return IDENTIFIERS.computeIfAbsent(fullName, s -> new Identifier(s, namespaceLower, nameLower));
    }

    public boolean isRegistered(String namespace, String name) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(namespace), "namespace");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "name");
        namespace = lower(namespace);
        name = lower(namespace);

        final String fullName = namespace + NAMESPACE_SEPARATOR + name;

        return IDENTIFIERS.containsKey(fullName);
    }

    public boolean isRegistered(String fullName) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(fullName), "fullName");
        fullName = lower(fullName);

        return IDENTIFIERS.containsKey(fullName);
    }

    public static List<Identifier> getNamespace(final String namespace) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(namespace), "namespace");

        return IDENTIFIERS.values().stream()
                .filter(identifier -> identifier.namespace.equalsIgnoreCase(namespace))
                .collect(Collectors.toList());
    }

    public static List<Identifier> getAll() {
        return ImmutableList.copyOf(IDENTIFIERS.values());
    }

    private static String lower(String string) {
        return string.toLowerCase(Locale.US);
    }

    @Override
    public String toString() {
        return fullName;
    }
}
