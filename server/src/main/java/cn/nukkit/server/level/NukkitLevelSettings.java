/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.level;

import cn.nukkit.api.level.GameRules;
import cn.nukkit.api.level.LevelSettings;
import cn.nukkit.api.level.data.Difficulty;
import cn.nukkit.api.level.data.Dimension;
import cn.nukkit.api.level.data.Generator;
import cn.nukkit.api.permission.PlayerPermission;
import cn.nukkit.api.util.GameMode;
import cn.nukkit.server.permission.NukkitAbilities;
import com.flowpowered.math.vector.Vector3f;
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
