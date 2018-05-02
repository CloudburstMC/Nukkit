package com.nukkitx.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nukkitx.api.Configuration;
import com.nukkitx.api.level.data.Difficulty;
import com.nukkitx.api.util.GameMode;
import com.nukkitx.server.level.LevelManager;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public final class NukkitConfiguration implements Configuration {

    private NukkitGeneralConfiguration general;

    private NukkitMechanicsConfiguration mechanics;

    @JsonProperty("default-level")
    private NukkitLevelConfiguration defaultLevel;

    private NukkitNetworkConfiguration network;

    private NukkitRconConfiguration rcon;

    private NukkitTimingsConfiguration timings;

    private NukkitAdvancedConfiguration advanced;

    private Map<String, NukkitLevelConfiguration> levels;


    @Getter
    @Setter
    @ToString
    public static class NukkitGeneralConfiguration implements GeneralConfiguration {
        private String locale = "en_US";
        @JsonProperty("max-players")
        private int maximumPlayers = 20;
        private String motd = "A Nukkit Powered Server";
        @JsonProperty("sub-motd")
        private String subMotd = "https://nukkitx.com";
        @JsonProperty("xbox-auth")
        private boolean xboxAuthenticated = true;
        @JsonProperty("auto-save")
        private boolean autoSaving = true;
        @JsonProperty("force-resource-packs")
        private boolean forcingResourcePacks = false;
        @JsonProperty("white-list")
        private boolean whitelisted = false;
        @JsonProperty("achievements")
        private boolean achievementsEnabled = true;
        @JsonProperty("announce-player-achievements")
        private boolean announcingAchievements = true;
        @JsonProperty("spawn-protection")
        private int spawnProtection = 16;
        @JsonProperty("server-closed")
        private String shutdownMessage = "Server Closed";
        @JsonProperty("auto-save-interval")
        private int autoSaveInterval = 5;
    }

    @Getter
    @Setter
    @ToString
    public static class NukkitMechanicsConfiguration implements MechanicsConfiguration {
        private String difficulty = "NORMAL";
        private boolean hardcore = false;
        @JsonProperty("pvp")
        private boolean pvpEnabled = true;
        @JsonProperty("default-gamemode")
        private String defaultGamemode = "SURVIVAL";
        @JsonProperty("force-gamemode")
        private boolean gamemodeForced = false;
        @JsonProperty("max-chunk-radius")
        private int maximumChunkRadius = 16;
        @JsonProperty("view-distance")
        private int viewDistance = 10;
        @JsonProperty("spawn-animals")
        private boolean spawningAnimals = true;
        @JsonProperty("spawn-monsters")
        private boolean spawningMonsters = true;
        @JsonProperty("allow-flight")
        private boolean flightAllowed = false;

        public void setHardcore(boolean hardcore) {
            this.hardcore = hardcore;
            if (hardcore) {
                setDifficulty(Difficulty.HARD);
            }
        }

        public GameMode getDefaultGamemode() {
            return GameMode.parse(defaultGamemode);
        }

        public void setDefaultGamemode(GameMode gamemode) {
            this.defaultGamemode = gamemode.name();
        }

        public Difficulty getDifficulty() {
            return Difficulty.parse(difficulty);
        }

        public void setDifficulty(Difficulty difficulty) {
            this.difficulty = difficulty.name();
        }
    }

    @Getter
    @ToString
    public static class NukkitNetworkConfiguration implements NetworkConfiguration {
        private String address = "0.0.0.0";
        private int port = 19132;
        @JsonProperty("query-enabled")
        private boolean queryEnabled = true;
        @JsonProperty("query-plugins")
        private boolean queryingPlugins = true;
    }

    @Getter
    @ToString
    public static class NukkitRconConfiguration implements RconConfiguration {
        private boolean enabled = true;
        private String password = generatePassword();
        private String address = "127.0.0.1";
        private int port = 27015;

        public void clearPassword() {
            password = null;
        }
    }

    @Getter
    @ToString
    public static class NukkitLevelConfiguration implements LevelConfiguration {
        private String id = LevelManager.createUniqueLevelId(0);
        @JsonProperty("type")
        private String generator = "FLAT";
        private String seed = generateSeed();
        private String format = "NULL";
        @JsonProperty("generator-settings")
        private String generatorSettings = "";
        @JsonProperty("read-only")
        private boolean readOnly = false;
        @JsonProperty("load-spawn-chunks")
        private boolean loadingSpawnChunks;
    }

    @Getter
    @ToString
    public static class NukkitTimingsConfiguration implements TimingsConfiguration {
        private boolean enabled = false;
        private boolean verbose = false;
        @JsonProperty("history-interval")
        private int historyInterval = 6000;
        @JsonProperty("history-length")
        private int historyLength = 72000;
        @JsonProperty("bypass-max")
        private boolean bypassingMax = false;
        @JsonProperty("privacy")
        private boolean privacyEnabled = false;
        private List<String> ignore = new ArrayList<>();
    }

    @Getter
    @Setter
    @ToString
    public static class NukkitAdvancedConfiguration implements AdvancedConfiguration {
        @JsonProperty("debug-commands")
        private boolean debugCommandsEnabled = false;
        @JsonProperty("chunk-threads")
        private int chunkLoadThreads = 2;
        @JsonProperty("network-threads")
        private int networkThreads = -1;
        @JsonProperty("log-level")
        private String logLevel = "INFO";
        @JsonProperty("netty-leak-detector-level")
        private String resourceLeakDetectorLevel = "DISABLED";
        @JsonProperty("chunk-timeout-after-load")
        private int chunkTimeoutAfterLoad = 30;
        @JsonProperty("chunk-timeout-after-last-access")
        private int chunkTimeoutAfterLastAccess = 120;
        @JsonProperty("spawn-radius")
        private int spawnChunkRadius = 16;
        @JsonProperty("chunk-gc")
        private boolean chunkGCEnabled = true;
        @JsonProperty("blocks-ticked-per-tick")
        private int blocksTickedPerTick = 50;
    }

    public boolean populateMissingFields() {
        boolean stale = false;
        if (general == null) {
            general = new NukkitGeneralConfiguration();
            stale = true;
        }

        if (mechanics == null) {
            mechanics = new NukkitMechanicsConfiguration();
            stale = true;
        }

        if (network == null) {
            network = new NukkitNetworkConfiguration();
            stale = true;
        }

        if (rcon == null) {
            rcon = new NukkitRconConfiguration();
            stale = true;
        }

        if (advanced == null) {
            advanced = new NukkitAdvancedConfiguration();
            stale = true;
        }

        if (timings == null) {
            timings = new NukkitTimingsConfiguration();
            stale = true;
        }

        if (defaultLevel == null) {
            defaultLevel = new NukkitLevelConfiguration();
            stale = true;
        }

        if (levels == null) {
            levels = new HashMap<>();
        }

        return stale;
    }

    public static NukkitConfiguration load(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return NukkitServer.YAML_MAPPER.readValue(reader, NukkitConfiguration.class);
        }
    }

    public static NukkitConfiguration load(InputStream stream) throws IOException {
        return NukkitServer.YAML_MAPPER.readValue(stream, NukkitConfiguration.class);
    }

    public static void save(Path path, NukkitConfiguration configuration) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            NukkitServer.YAML_MAPPER.writerWithDefaultPrettyPrinter().writeValue(writer, configuration);
        }
    }

    public static NukkitConfiguration defaultConfiguration() {
        NukkitConfiguration configuration = new NukkitConfiguration();
        configuration.populateMissingFields();
        return configuration;
    }

    public static String generatePassword() {
        BigInteger integer = new BigInteger(64, ThreadLocalRandom.current());
        return integer.toString(36);
    }

    private static String generateSeed() {
        return Long.toString(ThreadLocalRandom.current().nextLong());
    }
}
