package cn.nukkit.level.storage;

public class StorageTypes {

    public static final StorageType LEVELDB = StorageType.of("minecraft:leveldb");

    public static final StorageType ANVIL = StorageType.of("minecraft:anvil");

    private StorageTypes() {
        throw new UnsupportedOperationException();
    }
}
