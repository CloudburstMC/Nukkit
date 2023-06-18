package cn.nukkit.spark;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.utils.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.stream.Collectors;

public class SparkInstaller {
    public static void checkSpark(Server server) {
        Logger log = server.getLogger();
        PluginManager manager = server.getPluginManager();

        try {
            if (hasSpark(server)) {
                log.info("Using already user-installed spark plugin");
                return;
            }

            File file = new File(Server.getInstance().getDataPath() + "/spark/" + "spark.jar");
            file.getParentFile().mkdirs();

            boolean shouldDownload = true;

            if (file.exists()) {
                String fileSha1 = String.format("%040x", new BigInteger(1,
                        MessageDigest.getInstance("SHA-1").digest(Files.readAllBytes(file.toPath()))));
                String sparkSha1;
                URLConnection urlConnection =
                        new URL("https://sparkapi.lucko.me/download/nukkit/sha1").openConnection();
                urlConnection.setReadTimeout(5000);
                urlConnection.setConnectTimeout(5000);
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()))) {
                    sparkSha1 = reader.lines().collect(Collectors.joining(""));
                }

                if (fileSha1.equals(sparkSha1)) {
                    shouldDownload = false;
                }
            }

            if (shouldDownload) {
                log.info("Downloading updated spark plugin");

                URLConnection urlConnection =
                        new URL("https://sparkapi.lucko.me/download/nukkit").openConnection();
                urlConnection.setReadTimeout(5000);
                urlConnection.setConnectTimeout(5000);
                Files.copy(urlConnection.getInputStream(), file.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            }

            manager.loadPlugin(file);
        } catch (Exception e) {
            log.error("Failed to install spark");
            e.printStackTrace();
        }
    }

    public static boolean hasSpark(Server server) {
        PluginManager manager = server.getPluginManager();

        return manager.getPlugin("spark") != null;
    }
}