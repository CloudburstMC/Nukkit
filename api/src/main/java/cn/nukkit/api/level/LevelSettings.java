package cn.nukkit.api.level;

import cn.nukkit.api.level.data.Difficulty;
import cn.nukkit.api.level.data.Dimension;
import cn.nukkit.api.level.data.Generator;
import cn.nukkit.api.permission.PlayerPermission;
import cn.nukkit.api.util.GameMode;
import com.flowpowered.math.vector.Vector3f;

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
}
