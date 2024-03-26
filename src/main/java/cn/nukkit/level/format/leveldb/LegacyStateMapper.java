package cn.nukkit.level.format.leveldb;

public interface LegacyStateMapper {
    int legacyToRuntime(int legacyId, int meta);

    int runtimeToLegacyId(int runtimeId);

    int runtimeToLegacyData(int runtimeId);

    int runtimeToFullId(int runtimeId);
}
