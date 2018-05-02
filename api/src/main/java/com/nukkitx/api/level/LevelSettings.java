package com.nukkitx.api.level;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.level.data.Difficulty;
import com.nukkitx.api.level.data.Dimension;
import com.nukkitx.api.level.data.Generator;
import com.nukkitx.api.permission.PlayerPermission;
import com.nukkitx.api.util.GameMode;

public interface LevelSettings {

    int getSeed();

    Generator getGenerator();

    Dimension getDimension();

    GameMode getGameMode();

    Difficulty getDifficulty();

    Vector3f getDefaultSpawn();

    void setDefaultSpawn(Vector3f defaultSpawn);

    boolean isAchievementsDisabled();

    int getTime();

    boolean isEduWorld();

    boolean isEduFeaturesEnabled();

    float getRainLevel();

    float getLightningLevel();

    boolean isMultiplayerGame();

    boolean isBroadcastingToLan();

    boolean isBroadcastingToXBL();

    boolean isCommandsEnabled();

    boolean isTexturepacksRequired();

    GameRules getGameRules();

    boolean isBonusChestEnabled();

    boolean isStartingWithMap();

    boolean isTrustingPlayers();

    PlayerPermission getDefaultPlayerPermission();

    int getXBLBroadcastMode();

    int getServerChunkTickRange();

    boolean isBroadcastingToPlatform();

    int getPlatformBroadcastMode();

    boolean isIntentOnXBLBroadcast();

    boolean isBehaviorPackLocked();

    boolean isResourcePackLocked();

    boolean isFromLockedWorldTemplate();
}
