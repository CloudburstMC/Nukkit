package cn.nukkit.server;

import cn.nukkit.api.ServerProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Random;

@Getter
@ToString
public class NukkitServerProperties implements ServerProperties {
    private volatile boolean valueChanged = false;
    @JsonProperty("motd")
    private String motd;
    @JsonProperty("sub-motd")
    private String subMotd;
    @JsonProperty("server-port")
    private int serverPort = 19132;
    @JsonProperty("server-ip")
    private String serverAddress;
    @JsonProperty("view-distance")
    private int viewDistance = 10;
    @JsonProperty("white-list")
    private boolean whitelistEnabled = false;
    @JsonProperty("achievements")
    private boolean achievementsEnabled = true;
    @JsonProperty("announce-player-achievements")
    private boolean achievementsAnnounced = true;
    @JsonProperty("spawn-protection")
    private int spawnProtection = 16;
    @JsonProperty("max-players")
    private int maxPlayers = 20;
    @JsonProperty("allow-flight")
    private boolean flightEnabled = false;
    @JsonProperty("spawn-animals")
    private boolean animalSpawningEnabled = true;
    @JsonProperty("spawn-mobs")
    private boolean mobSpawningEnabled = true;
    @JsonProperty("gamemode")
    private String defaultGamemode;
    @JsonProperty("force-gamemode")
    private boolean gamemodeForced = false;
    @JsonProperty("hardcore")
    private boolean hardcore = false;
    @JsonProperty("pvp")
    private boolean pvpEnabled = true;
    @JsonProperty("difficulty")
    private int difficulty = 1;
    @JsonProperty("generator-settings")
    private String generatorSettings;
    @JsonProperty("level-name")
    private String levelName;
    @JsonProperty("level-seed")
    private String levelSeed;
    @JsonProperty("level-type")
    private String levelType;
    @JsonProperty("enable-query")
    private boolean queryEnabled;
    @JsonProperty("rcon")
    private Rcon rcon;
    @JsonProperty("auto-save")
    private boolean autoSaveEnabled = true;
    @JsonProperty("force-resources")
    private boolean resourcesForced = false;
    @JsonProperty("bug-report")
    private boolean bugReportEnabled = true;
    @JsonProperty("online-mode")
    private boolean isAuthEnabled = true;

    public static NukkitServerProperties load(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return NukkitServer.PROPERTIES_MAPPER.readValue(reader, NukkitServerProperties.class);
        }
    }

    public boolean hasPropertiesChanged() {
        return valueChanged;
    }

    @Override
    public void setMotd(String motd) {
        valueChanged = true;
        this.motd = motd;
    }

    @Override
    public void setSubMotd(String subMotd) {
        valueChanged = true;
        this.subMotd = subMotd;
    }

    @Override
    public void setViewDistance(int viewDistance) {
        valueChanged = true;
        this.viewDistance = viewDistance;
    }

    @Override
    public void setWhitelistEnabled(boolean whitelistEnabled) {
        valueChanged = true;
        this.whitelistEnabled = whitelistEnabled;
    }

    @Override
    public void setAchievementsEnabled(boolean achievementsEnabled) {
        valueChanged = true;
        this.achievementsEnabled = achievementsEnabled;
    }

    @Override
    public void setAchievementsAnnounced(boolean achievementsAnnounced) {
        valueChanged = true;
        this.achievementsAnnounced = achievementsAnnounced;
    }

    @Override
    public void setSpawnProtection(int spawnProtection) {
        valueChanged = true;
        this.spawnProtection = spawnProtection;
    }

    @Override
    public void setMaxPlayers(int maxPlayers) {
        valueChanged = true;
        this.maxPlayers = maxPlayers;
    }

    @Override
    public void setFlightEnabled(boolean flightEnabled) {
        valueChanged = true;
        this.flightEnabled = flightEnabled;
    }

    @Override
    public void setAnimalSpawningEnabled(boolean animalSpawningEnabled) {
        valueChanged = true;
        this.animalSpawningEnabled = animalSpawningEnabled;
    }

    @Override
    public void setMobSpawningEnabled(boolean mobSpawningEnabled) {
        valueChanged = true;
        this.mobSpawningEnabled = mobSpawningEnabled;
    }

    public void setDefaultGamemode(String defaultGamemode) {
        valueChanged = true;
        this.defaultGamemode = defaultGamemode;
    }

    @Override
    public void setGamemodeForced(boolean gamemodeForced) {
        valueChanged = true;
        this.gamemodeForced = gamemodeForced;
    }

    @Override
    public void setHardcore(boolean hardcore) {
        valueChanged = true;
        if (hardcore && difficulty < 3) difficulty = 3;
        this.hardcore = hardcore;
    }

    @Override
    public void setPvpEnabled(boolean pvpEnabled) {
        valueChanged = true;
        this.pvpEnabled = pvpEnabled;
    }

    @Override
    public void setDifficulty(int difficulty) {
        valueChanged = true;
        this.difficulty = difficulty;
    }

    @Override
    public void setResourcesForced(boolean resourcesForced) {
        valueChanged = true;
        this.resourcesForced = resourcesForced;
    }

    public void addMissingValues() {
        if (motd == null) {
            motd = "A Minecraft Server";
            valueChanged = true;
        }

        if (subMotd == null) {
            subMotd = "Powered by Nukkit";
            valueChanged = true;
        }

        if (defaultGamemode == null) {
            defaultGamemode = "SURVIVAL";
            valueChanged = true;
        }

        if (serverAddress == null) {
            serverAddress = "0.0.0.0";
            valueChanged = true;
        }

        if (generatorSettings == null) {
            generatorSettings = "";
            valueChanged = true;
        }

        if (levelName == null) {
            levelName = "world";
            valueChanged = true;
        }

        if (levelSeed == null) {
            levelSeed = Long.toString(System.currentTimeMillis());
            valueChanged = true;
        }
        if (levelType == null) {
            levelType = "DEFAULT";
            valueChanged = true;
        }

        if (rcon == null) {
            rcon = new Rcon();
            rcon.enabled = true;
            rcon.address = "127.0.0.1";
            rcon.port = 27015;
            rcon.password = generateRandomPassword();
            valueChanged = true;
        }
    }

    public static void save(Path path, NukkitServerProperties configuration) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            NukkitServer.PROPERTIES_MAPPER.writerWithDefaultPrettyPrinter().writeValue(writer, configuration);
        }
    }

    public static NukkitServerProperties defaultConfiguration() {
        NukkitServerProperties configuration = new NukkitServerProperties();
        configuration.addMissingValues();
        return configuration;
    }

    private static String generateRandomPassword() {
        BigInteger integer = new BigInteger(130, new Random());
        return integer.toString(36);
    }

    @Getter
    @ToString
    public static class Rcon {
        private boolean enabled;
        private String address;
        private int port;
        private String password;
    }
}
