package cn.nukkit.server.level;

import cn.nukkit.api.level.AdventureSettings;
import cn.nukkit.api.level.LevelSettings;
import com.flowpowered.math.vector.Vector3i;
import io.netty.util.internal.ConcurrentSet;
import lombok.Data;

import java.util.Set;

@Data
public class NukkitLevelSettings implements LevelSettings {
    private final int seed;
    private final int dimension;
    private final int generator;
    private final Set<GameRules> gameRules = new ConcurrentSet<>();
    private int worldGamemode;
    private int difficulty;
    private Vector3i spawnPosition;
    private boolean achievementsDisabled;
    private int time;
    private boolean educationLevel;
    private float rainLevel;
    private float lightningLevel;
    private boolean multiplayerGame = true;
    private boolean broadcastingToLan = true;
    private boolean broadcastingToXboxLive = true;
    private boolean commandsEnabled = true;
    private boolean texturePacksRequired;
    private boolean bonusChestEnabled;
    private boolean startingWithMap;
    private boolean trustingPlayers;
    private AdventureSettings.PlayerPermission defaultPlayerPermission;
    private int xboxLiveBroadcastMode;
}
