package com.nukkitx.server.level;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.level.LevelData;
import com.nukkitx.api.level.data.Difficulty;
import com.nukkitx.api.level.data.Dimension;
import com.nukkitx.api.level.data.Generator;
import com.nukkitx.api.permission.PlayerPermission;
import com.nukkitx.api.util.GameMode;
import com.nukkitx.server.permission.NukkitAbilities;
import lombok.Data;

import java.util.concurrent.ThreadLocalRandom;

@Data
public abstract class NukkitLevelData implements LevelData {
    protected static NukkitLevelData defaults;

    // Level Settings
    private final int seed;
    private final long savedTick;
    private final int savedTime;
    private final Generator generator;
    private final Dimension dimension;
    private final NukkitGameRules gameRules = NukkitGameRules.newGameRules();
    private GameMode gameMode = GameMode.SURVIVAL;
    private Difficulty difficulty = Difficulty.NORMAL;
    private Vector3f defaultSpawn;
    private boolean achievementsDisabled = false;
    private boolean eduWorld = false;
    private boolean eduFeaturesEnabled = true;
    private float rainLevel = 0;
    private float lightningLevel = 0;
    private boolean forceGameType = false;
    private boolean multiplayerGame = true;
    private boolean broadcastingToLan = true;
    private boolean broadcastingToXBL = true;
    private boolean commandsEnabled = true;
    private boolean texturepacksRequired = false;
    private boolean bonusChestEnabled = false;
    private boolean startingWithMap = false;
    private boolean trustingPlayers = false;
    private NukkitAbilities defaultAbilities;
    private PlayerPermission defaultPlayerPermission = PlayerPermission.OPERATOR;
    private int XBLBroadcastMode = 3;
    private int serverChunkTickRange = 8; // Simulated render distance.
    private boolean broadcastingToPlatform = true;
    private int platformBroadcastMode = 0;
    private boolean intentOnXBLBroadcast = true;
    private boolean behaviorPackLocked = false;
    private boolean resourcePackLocked = false;
    private boolean fromLockedWorldTemplate = false;
    private boolean usingMsaGamertagsOnly = true;

    // Level Data
    private String name;
    private final long randomSeed;
    private boolean immutableWorld = false;


    protected NukkitLevelData() {
        this.randomSeed = ThreadLocalRandom.current().nextLong();
        this.seed = LevelManager.parseSeed(randomSeed);
        this.generator = Generator.OVERWORLD;
        this.savedTick = 0;
        this.savedTime = 0;
        this.dimension = Dimension.OVERWORLD;
    }

    protected NukkitLevelData(long randomSeed, long tick, long time, Generator generator) {
        this.randomSeed = randomSeed;
        this.seed = LevelManager.parseSeed(randomSeed);
        this.savedTick = tick;
        this.savedTime = (int) time;
        this.generator = generator;
        this.dimension = Dimension.OVERWORLD;
    }

    public int getTime() {
        return savedTime;
    }
}
