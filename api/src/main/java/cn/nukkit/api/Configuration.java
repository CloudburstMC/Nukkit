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

package cn.nukkit.api;

import cn.nukkit.api.level.data.Difficulty;
import cn.nukkit.api.util.GameMode;

import java.util.List;

public interface Configuration {

    GeneralConfiguration getGeneral();

    MechanicsConfiguration getMechanics();

    NetworkConfiguration getNetwork();

    LevelConfiguration getDefaultLevel();

    TimingsConfiguration getTimings();

    AdvancedConfiguration getAdvanced();

    interface GeneralConfiguration {

        String getMotd();

        String getSubMotd();

        String getLocale();

        int getMaximumPlayers();

        boolean isXboxAuthenticated();

        boolean isAutoSaving();

        boolean isForcingResourcePacks();

        boolean isWhitelisted();

        boolean isAchievementsEnabled();

        boolean isAnnouncingAchievements();

        int getSpawnProtection();

        String getShutdownMessage();
    }

    interface MechanicsConfiguration {

        Difficulty getDifficulty();

        boolean isHardcore();

        boolean isPvpEnabled();

        GameMode getDefaultGamemode();

        boolean isGamemodeForced();

        int getMaximumChunkRadius();

        int getViewDistance();

        boolean isSpawningAnimals();

        boolean isSpawningMonsters();

        boolean isFlightAllowed();
    }

    interface NetworkConfiguration {

        String getAddress();

        int getPort();

        boolean isQueryEnabled();

        boolean isQueryingPlugins();
    }

    interface RconConfiguration {

        boolean isEnabled();

        String getAddress();

        int getPort();
    }

    interface LevelConfiguration {

        String getId();

        String getGenerator();

        String getSeed();

        String getFormat();

        String getGeneratorSettings();
    }

    interface TimingsConfiguration {

        boolean isEnabled();

        boolean isVerbose();

        int getHistoryInterval();

        boolean isBypassingMax();

        boolean isPrivacyEnabled();

        List<String> getIgnore();
    }

    interface AdvancedConfiguration {

        boolean isDebugCommandsEnabled();

        int getChunkLoadThreads();

        String getLogLevel();

        String getResourceLeakDetectorLevel();
    }
}
