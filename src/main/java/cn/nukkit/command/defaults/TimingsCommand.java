package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.RemoteConsoleCommandSender;
import cn.nukkit.event.TimingsHandler;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by fromgate and Pub4Game on 30.06.2016.
 */
public class TimingsCommand extends VanillaCommand {

    public static long timingStart = 0;

    public TimingsCommand(String name) {
        super(name, "%nukkit.command.timings.description", "%nukkit.command.timings.usage");
        this.setPermission("nukkit.command.timings");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", usageMessage));
            return true;
        }
        String mode = args[0].toLowerCase();
        if (mode.equals("on")) {
            sender.getServer().getPluginManager().setUseTimings(true);
            TimingsHandler.reload();
            sender.sendMessage(new TranslationContainer("nukkit.command.timings.enable"));
            return true;
        } else if (mode.equals("off")) {
            sender.getServer().getPluginManager().setUseTimings(false);
            sender.sendMessage(new TranslationContainer("nukkit.command.timings.disable"));
            return true;
        }
        if (!sender.getServer().getPluginManager().useTimings()) {
            sender.sendMessage(new TranslationContainer("nukkit.command.timings.timingsDisabled"));
            return true;
        }
        boolean paste = mode.equals("paste");
        if (mode.equals("reset")) {
            TimingsHandler.reload();
            sender.sendMessage(new TranslationContainer("nukkit.command.timings.reset"));
        } else if (mode.equals("merged") || mode.equals("report") || paste) {
            long sampleTime = System.nanoTime() - timingStart;
            List<String> timeStr = TimingsHandler.getTimings();
            timeStr.add("Sample time " + sampleTime + " (" + String.format("%.3f", (double) sampleTime / 1.0E9D) + "s)");

            new PasteThread(sender, timeStr, paste).run();
        }
        return true;
    }


    class PasteThread extends Thread {
        private final CommandSender sender;
        private final List<String> report;
        private String reportUrl;
        private final boolean paste;

        public PasteThread(CommandSender sender, List<String> report, boolean paste) {
            super("Timings paste thread");
            this.sender = sender;
            this.report = report;
            this.paste = paste;
            this.reportUrl = null;
        }

        public synchronized void start() {
            if (this.sender instanceof RemoteConsoleCommandSender) {
                this.run();
            } else {
                super.start();
            }

        }

        public void run() {
            StringBuilder sb = new StringBuilder();
            report.forEach(s -> sb.append(s).append("\n"));

            if (paste) {
                try {
                    HttpURLConnection ex = (HttpURLConnection) (new URL("http://paste.ubuntu.com/")).openConnection();
                    ex.setDoOutput(true);
                    ex.setRequestMethod("POST");
                    ex.setInstanceFollowRedirects(false);
                    OutputStream out = ex.getOutputStream();
                    out.write("poster=Nukkit&syntax=text&content=".getBytes("UTF-8"));
                    out.write(URLEncoder.encode(sb.toString(), "UTF-8").getBytes("UTF-8"));
                    out.close();
                    ex.getInputStream().close();
                    String location = ex.getHeaderField("Location");
                    this.sender.sendMessage(new TranslationContainer("nukkit.command.timings.timingsUpload", location));
                    String pasteID = location.substring("http://paste.ubuntu.com/".length(), location.length() - 1);
                    reportUrl = "http://timings.aikar.co/?url=" + pasteID;
                    this.sender.sendMessage(new TranslationContainer("nukkit.command.timings.timingsRead", reportUrl));
                    sb.append("\nLink: ").append(reportUrl);
                } catch (IOException e) {
                    this.sender.sendMessage(TextFormat.RED + new TranslationContainer("nukkit.command.timings.pasteError"));
                    Server.getInstance().getLogger().info("Could not paste timings", e);
                }
            }

            File timingFolder = new File(Server.getInstance().getDataPath() + File.separator + "timings");
            timingFolder.mkdirs();
            File timingsFile = null;
            int index = 0;
            while (timingsFile == null || timingsFile.exists()) {
                index++;
                timingsFile = new File(timingFolder + File.separator + "timings_" + String.format("%03d", index) + ".txt");
            }

            try {
                Utils.writeFile(timingsFile, sb.toString());
                sender.sendMessage(new TranslationContainer("nukkit.command.timings.timingsWrite", timingsFile.getName()));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}

