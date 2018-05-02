package com.nukkitx.server.level;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.level.GameRules;
import com.nukkitx.api.level.LevelSettings;
import com.nukkitx.api.level.data.Difficulty;
import com.nukkitx.api.level.data.Dimension;
import com.nukkitx.api.level.data.Generator;
import com.nukkitx.api.permission.PlayerPermission;
import com.nukkitx.api.util.GameMode;
import com.nukkitx.server.permission.NukkitAbilities;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public abstract class NukkitLevelSettings implements LevelSettings {
    private final int seed;
    private final Dimension dimension;
    private final Generator generator;
    private final GameRules gameRules;
    private GameMode gameMode;
    private Difficulty difficulty;
    private Vector3f defaultSpawn;
    private boolean achievementsDisabled;
    private int time;
    private boolean eduWorld;
    private float rainLevel;
    private float lightningLevel;
    private boolean forceGameType;
    private boolean multiplayerGame;
    private boolean broadcastingToLan;
    private boolean broadcastingToXBL;
    private boolean commandsEnabled;
    private boolean texturepacksRequired;
    private boolean bonusChestEnabled;
    private boolean startingWithMap;
    private boolean trustingPlayers;
    private NukkitAbilities defaultAbilities;
    private PlayerPermission defaultPlayerPermission;
    private int XBLBroadcastMode;
    private int serverChunkTickRange;
    private boolean broadcastingToPlatform;
    private int platformBroadcastMode;
    private boolean broadcastingXBLWithIntent;
    private boolean immutableWorld;

    public boolean achievementsDisabledOnLoad() {
        boolean enabled = !achievementsDisabled;

        if (!achievementsDisabled) {
            enabled = !commandsEnabled;
        }

        if (enabled) {
            return gameMode == GameMode.CREATIVE;
        }
        return true;
    }
}
