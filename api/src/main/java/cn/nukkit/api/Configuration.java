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

        boolean isSpawningMobs();

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
