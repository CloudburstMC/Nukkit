package cn.nukkit.api.level;

public interface LevelData extends LevelSettings {

    String getName();

    long getRandomSeed();

    long getSavedTick();

    int getSavedTime();
}
