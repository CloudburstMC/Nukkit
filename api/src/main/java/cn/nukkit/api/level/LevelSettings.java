package cn.nukkit.api.level;

import cn.nukkit.api.GameMode;
import cn.nukkit.api.level.gamerule.GameRule;
import com.flowpowered.math.vector.Vector3f;

import java.util.Collection;

public interface LevelSettings {

    int getSeed();

    int getDimension();

    int getGenerator();

    GameMode getWorldGamemode();

    int getDifficulty();

    Vector3f getSpawnPosition();

    boolean isAchievementsDisabled();

    int getTime();

    boolean isEducationLevel();

    float getRainLevel();

    float getLightningLevel();

    boolean isMultiplayerGame();

    boolean isBroadcastingToLan();

    boolean isBroadcastingToXboxLive();

    boolean isCommandsEnabled();

    boolean isTexturePacksRequired();

    Collection<GameRule> getGameRules();

    boolean isBonusChestEnabled();

    boolean isStartingWithMap();

    boolean isTrustingPlayers();

    AdventureSettings.PlayerPermission getDefaultPlayerPermission();

    int getXboxLiveBroadcastMode();
}
