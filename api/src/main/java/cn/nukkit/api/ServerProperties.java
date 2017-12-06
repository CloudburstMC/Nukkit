package cn.nukkit.api;

public interface ServerProperties {

    String getMotd();

    void setMotd(String motd);

    String getSubMotd();

    void setSubMotd(String subMotd);

    int getServerPort();

    String getServerAddress();

    int getViewDistance();

    void setViewDistance(int viewDistance);

    boolean isWhitelistEnabled();

    void setWhitelistEnabled(boolean whitelistEnabled);

    boolean isAchievementsEnabled();

    void setAchievementsEnabled(boolean achievementsEnabled);

    boolean isAchievementsAnnounced();

    void setAchievementsAnnounced( boolean achievementsAnnounced);

    int getSpawnProtection();

    void setSpawnProtection(int spawnProtection);

    int getMaxPlayers();

    void setMaxPlayers(int maxPlayers);

    boolean isFlightEnabled();

    void setFlightEnabled(boolean flightEnabled);

    boolean isAnimalSpawningEnabled();

    void setAnimalSpawningEnabled(boolean animalSpawningEnabled);

    boolean isMobSpawningEnabled();

    void setMobSpawningEnabled(boolean mobSpawningEnabled);

    String getDefaultGamemode();

    void setDefaultGamemode(String defaultGamemode);

    boolean isGamemodeForced();

    void setGamemodeForced(boolean gamemodeForced);

    boolean isHardcore();

    void setHardcore(boolean hardcore);

    boolean isPvpEnabled();

    void setPvpEnabled(boolean pvpEnabled);

    int getDifficulty();

    void setDifficulty(int difficulty);

    String getGeneratorSettings();

    String getLevelName();

    String getLevelSeed();

    String getLevelType();

    boolean isQueryEnabled();

    boolean isAutoSaveEnabled();

    boolean isResourcesForced();

    void setResourcesForced(boolean resourcesForced);

    boolean isBugReportEnabled();
}
