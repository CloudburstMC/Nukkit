package cn.nukkit.level.storage;

import cn.nukkit.utils.Identifier;

import java.util.Objects;

public class StorageType {
    private final Identifier identifier;

    private StorageType(Identifier identifier) {
        this.identifier = identifier;
    }

    public static StorageType of(String identifier) {
        return of(Identifier.fromString(identifier));
    }

    public static StorageType of(Identifier identifier) {
        Objects.requireNonNull(identifier, "identifier");
        return new StorageType(identifier);
    }

    public Identifier getIdentifier() {
        return identifier;
    }
}
