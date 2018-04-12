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
