package cn.nukkit.api;

import java.util.List;

public interface Configuration {

    GeneralConfiguration getGeneral();

    MechanicsConfiguration getMechanics();

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

        int getDifficulty();

        boolean isHardcore();

        boolean isPvpEnabled();

        String getDefaultGamemode();

        boolean isGamemodeForced();

        int getViewDistance();

        boolean isSpawningAnimals();

        boolean isSpawningMobs();

        boolean isFlightAllowed();
    }

    interface NetworkConfiguration {

        String getAddress();

        int getPort();

        boolean isQueryEnabled();
    }

    interface RconConfiguration {

        boolean isEnabled();

        String getAddress();

        int getPort();
    }

    interface LevelConfiguration {

        String getId();

        String getName();

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
