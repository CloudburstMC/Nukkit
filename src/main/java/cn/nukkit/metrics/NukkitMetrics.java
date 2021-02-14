package cn.nukkit.metrics;

import cn.nukkit.Server;
import cn.nukkit.api.Since;
import cn.nukkit.utils.Config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Since("1.3.2.0-PN")
public class NukkitMetrics {
    private static boolean metricsStarted = false;

    private final Server server;

    private boolean enabled;
    private String serverUUID;
    private boolean logFailedRequests;

    @Since("1.3.2.0-PN") public NukkitMetrics(Server server) {
        this.server = server;

        if (metricsStarted) {
            return;
        }

        try {
            this.loadConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (enabled) {
            Metrics metrics = new Metrics("Nukkit", serverUUID, logFailedRequests, server.getLogger());

            metrics.addCustomChart(new Metrics.SingleLineChart("players", () -> server.getOnlinePlayers().size()));
            metrics.addCustomChart(new Metrics.SimplePie("codename", server::getCodename));
            metrics.addCustomChart(new Metrics.SimplePie("minecraft_version", server::getVersion));
            metrics.addCustomChart(new Metrics.SimplePie("nukkit_version", server::getNukkitVersion));
            metrics.addCustomChart(new Metrics.SimplePie("xbox_auth", () -> server.getPropertyBoolean("xbox-auth") ? "Required" : "Not required"));

            metrics.addCustomChart(new Metrics.AdvancedPie("player_platform", () -> {
                Map<String, Integer> valueMap = new HashMap<>();

                server.getOnlinePlayers().forEach((uuid, player) -> {
                    String deviceOS = mapDeviceOSToString(player.getLoginChainData().getDeviceOS());
                    if (!valueMap.containsKey(deviceOS)) {
                        valueMap.put(deviceOS, 1);
                    } else {
                        valueMap.put(deviceOS, valueMap.get(deviceOS) + 1);
                    }
                });
                return valueMap;
            }));

            metrics.addCustomChart(new Metrics.AdvancedPie("player_game_version", () -> {
                Map<String, Integer> valueMap = new HashMap<>();

                server.getOnlinePlayers().forEach((uuid, player) -> {
                    String gameVersion = player.getLoginChainData().getGameVersion();
                    if (!valueMap.containsKey(gameVersion)) {
                        valueMap.put(gameVersion, 1);
                    } else {
                        valueMap.put(gameVersion, valueMap.get(gameVersion) + 1);
                    }
                });
                return valueMap;
            }));

            // The following code can be attributed to the PaperMC project
            // https://github.com/PaperMC/Paper/blob/master/Spigot-Server-Patches/0005-Paper-Metrics.patch#L614
            metrics.addCustomChart(new Metrics.DrilldownPie("java_version", () -> {
                Map<String, Map<String, Integer>> map = new HashMap<>();
                String javaVersion = System.getProperty("java.version");
                Map<String, Integer> entry = new HashMap<>();
                entry.put(javaVersion, 1);

                // http://openjdk.java.net/jeps/223
                // Java decided to change their versioning scheme and in doing so modified the java.version system
                // property to return $major[.$minor][.$secuity][-ea], as opposed to 1.$major.0_$identifier
                // we can handle pre-9 by checking if the "major" is equal to "1", otherwise, 9+
                String majorVersion = javaVersion.split("\\.")[0];
                String release;

                int indexOf = javaVersion.lastIndexOf('.');

                if (majorVersion.equals("1")) {
                    release = "Java " + javaVersion.substring(0, indexOf);
                } else {
                    // of course, it really wouldn't be all that simple if they didn't add a quirk, now would it
                    // valid strings for the major may potentially include values such as -ea to deannotate a pre release
                    Matcher versionMatcher = Pattern.compile("\\d+").matcher(majorVersion);
                    if (versionMatcher.find()) {
                        majorVersion = versionMatcher.group(0);
                    }
                    release = "Java " + majorVersion;
                }
                map.put(release, entry);
                return map;
            }));

            metricsStarted = true;
        }
    }

    /**
     * Loads the bStats configuration.
     */
    private void loadConfig() throws IOException {
        File bStatsFolder = new File(server.getPluginPath(), "bStats");

        if (!bStatsFolder.exists() && !bStatsFolder.mkdirs()) {
            server.getLogger().warning("Failed to create bStats metrics directory");
            return;
        }

        File configFile = new File(bStatsFolder, "config.yml");
        if (!configFile.exists()) {
            writeFile(configFile,
                    "# bStats collects some data for plugin authors like how many servers are using their plugins.",
                    "# To honor their work, you should not disable it.",
                    "# This has nearly no effect on the server performance!",
                    "# Check out https://bStats.org/ to learn more :)",
                    "enabled: true",
                    "serverUuid: \"" + UUID.randomUUID().toString() + "\"",
                    "logFailedRequests: false");
        }

        Config config = new Config(configFile, Config.YAML);

        // Load configuration
        this.enabled = config.getBoolean("enabled", true);
        this.serverUUID = config.getString("serverUuid");
        this.logFailedRequests = config.getBoolean("logFailedRequests", false);
    }

    private void writeFile(File file, String... lines) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    private String mapDeviceOSToString(int os) {
        switch (os) {
            case 1: return "Android";
            case 2: return "iOS";
            case 3: return "macOS";
            case 4: return "FireOS";
            case 5: return "Gear VR";
            case 6: return "Hololens";
            case 7: return "Windows 10";
            case 8: return "Windows";
            case 9: return "Dedicated";
            case 10: return "PS4";
            case 11: return "Switch";
            case 12: return "Switch";
            case 13: return "Xbox One";
            case 14: return "Windows Phone";
        }
        return "Unknown";
    }
}
