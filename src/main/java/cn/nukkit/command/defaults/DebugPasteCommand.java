package cn.nukkit.command.defaults;

import cn.nukkit.Nukkit;
import cn.nukkit.Server;
import cn.nukkit.command.CapturingCommandSender;
import cn.nukkit.command.CommandSender;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginDescription;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.HumanStringComparator;
import cn.nukkit.utils.Utils;
import com.nimbusds.jose.util.IOUtils;
import lombok.extern.log4j.Log4j2;
import org.iq80.leveldb.util.FileUtils;

import javax.annotation.Nonnull;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Log4j2
public class DebugPasteCommand extends VanillaCommand {

    private static final String ENDPOINT = "https://debugpaste.powernukkit.org/paste.php";
    private static final String USER_AGENT = "PowerNukkit/"+ Nukkit.VERSION;

    public DebugPasteCommand(String name) {
        super(name, "%nukkit.command.debug.description", "%nukkit.command.debug.usage");
        this.setPermission("nukkit.command.debug.perform");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        sender.sendMessage("The /debugpaste is executing, please wait...");
        Server server = Server.getInstance();
        server.getScheduler().scheduleAsyncTask(new AsyncTask() {
            @Override
            public void onRun() {
                String now = new SimpleDateFormat("yyyy-MM-dd'T'HH.mm.ss.SSSZ").format(new Date());
                Path zipPath;
                try {
                    Path dataPath = Paths.get(server.getDataPath()).toAbsolutePath();
                    Path dir = Files.createDirectories(dataPath.resolve("debugpastes/debugpaste-"+now)).toAbsolutePath();

                    Utils.writeFile(dir.resolve("thread-dump.txt").toString(), Utils.getAllThreadDumps());

                    CapturingCommandSender capturing = new CapturingCommandSender();
                    capturing.setOp(true);
                    new StatusCommand("status").execute(capturing, "status", new String[]{});
                    Utils.writeFile(dir.resolve("status.txt").toString(), capturing.getCleanCapture());
                    
                    Optional<Path> secondMostLatest = StreamSupport.stream(Files.newDirectoryStream(dataPath.resolve("logs")).spliterator(), false)
                            .filter(path -> path.toString().toLowerCase().endsWith(".log.gz"))
                            .max(((a,b)-> {
                                FileTime aTime = null, bTime = null;
                                try {
                                    aTime = Files.readAttributes(a, BasicFileAttributes.class).creationTime();
                                } catch (IOException ignored) {
                                }
                                try {
                                    bTime = Files.readAttributes(b, BasicFileAttributes.class).creationTime();
                                } catch (IOException ignored) {
                                }
                                if (aTime == null && bTime != null) {
                                    return 1;
                                }
                                if (aTime != null && bTime == null) {
                                    return -1;
                                }
                                if (aTime == null) {
                                    return 0;
                                }
                                int comp = aTime.compareTo(bTime);
                                if (comp != 0) {
                                    return comp;
                                }
                                return HumanStringComparator.getInstance().compare(
                                        a.getFileName().toString().toLowerCase(), 
                                        b.getFileName().toString().toLowerCase()
                                );
                            }));

                    Utils.copyFile(dataPath.resolve("logs/server.log").toFile(), dir.resolve("server-latest.log").toFile());
                    if (secondMostLatest.isPresent()) {
                        Utils.copyFile(secondMostLatest.get().toFile(), dir.resolve("server-second-most-latest.log.gz").toFile());
                    }

                    Utils.copyFile(dataPath.resolve("nukkit.yml").toFile(), dir.resolve("nukkit.yml").toFile());
                    Utils.copyFile(dataPath.resolve("server.properties").toFile(), dir.resolve("server.properties").toFile());

                    StringBuilder b = new StringBuilder();
                    b.append("\n# Server Information\n");
                    b.append("server.name: ").append(server.getName()).append('\n');
                    b.append("version.api: ").append(server.getApiVersion()).append('\n');
                    b.append("version.nukkit: ").append(server.getNukkitVersion()).append('\n');
                    b.append("version.git: ").append(server.getGitCommit()).append('\n');
                    b.append("version.codename: ").append(server.getCodename()).append('\n');
                    b.append("version.minecraft: ").append(server.getVersion()).append('\n');
                    b.append("version.protocol: ").append(ProtocolInfo.CURRENT_PROTOCOL).append('\n');
                    b.append("plugins:");
                    for (Plugin plugin : server.getPluginManager().getPlugins().values()) {
                        boolean enabled = plugin.isEnabled();
                        String name = plugin.getName();
                        PluginDescription desc = plugin.getDescription();
                        String version = desc.getVersion();
                        b.append("\n  ")
                                .append(name)
                                .append(":\n    ")
                                .append("version: '")
                                .append(version)
                                .append('\'')
                                .append("\n    enabled: ")
                                .append(enabled);
                    }
                    b.append("\n\n# Java Details\n");
                    Runtime runtime = Runtime.getRuntime();
                    b.append("memory.free: ").append(runtime.freeMemory()).append('\n');
                    b.append("memory.max: ").append(runtime.maxMemory()).append('\n');
                    b.append("cpu.runtime: ").append(ManagementFactory.getRuntimeMXBean().getUptime()).append('\n');
                    b.append("cpu.processors: ").append(runtime.availableProcessors()).append('\n');
                    b.append("java.specification.version: '").append(System.getProperty("java.specification.version")).append("'\n");
                    b.append("java.vendor: '").append(System.getProperty("java.vendor")).append("'\n");
                    b.append("java.version: '").append(System.getProperty("java.version")).append("'\n");
                    b.append("os.arch: '").append(System.getProperty("os.arch")).append("'\n");
                    b.append("os.name: '").append(System.getProperty("os.name")).append("'\n");
                    b.append("os.version: '").append(System.getProperty("os.version")).append("'\n");
                    b.append("ulimit:\n'").append(eval("ulimit", "-a")).append("'\n\n");
                    b.append("\n# Create a ticket: https://github.com/PowerNukkit/PowerNukkit/issues/new");

                    Utils.writeFile(dir.resolve("server-info.txt").toString(), b.toString());

                    zipPath = dir.resolveSibling(dir.getFileName() + ".zip");
                    Utils.zipFolder(dir, zipPath);
                    Path relative = dataPath.relativize(zipPath);
                    log.info("A debug paste was created at {}", relative);
                    if (sender.isPlayer()) {
                        sender.sendMessage("A debug paste has been saved in " + relative);
                    }
                    FileUtils.deleteRecursively(dir.toFile());
                } catch (IOException e) {
                    log.error("Failed to create a debugpaste in debugpastes/debugpaste-{}", now, e);
                    if (sender.isPlayer()) {
                        sender.sendMessage("An error has occurred: " + e);
                        sender.sendMessage("A partial paste might be available in debugpastes/debugpaste-" + now);
                    }
                    return;
                }

                if (args.length == 1 && "upload".equalsIgnoreCase(args[0])) {
                    upload(sender, zipPath);
                }
            }
        });
        return true;
    }
    
    private static void upload(CommandSender sender, Path zipPath) {
        sender.sendMessage("Uploading...");
        try {
            final URL url = new URL(ENDPOINT);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("PUT");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestProperty("Content-Type", "application/zip");
            connection.setDoOutput(true);
            
            try (BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream())) {
                Files.copy(zipPath, bos);
            }

            int code = connection.getResponseCode();
            if (code != 201) {
                throw new IOException("The server responded with code "+code+", expected 201");
            }
            
            try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                InputStreamReader rd = connection.getContentEncoding() != null? new InputStreamReader(in, connection.getContentEncoding()) : new InputStreamReader(in);
                BufferedReader reader = new BufferedReader(rd)
            ) {
                String response = reader.readLine();
                if (log.isDebugEnabled()) {
                    StringBuilder sb = new StringBuilder().append(response);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    String fullReturn = sb.toString();
                    if (!fullReturn.equals(response)) {
                        log.debug(fullReturn);
                    }
                }
                
                URL publicUrl = new URL(response);
                log.info("The debug paste {} was uploaded to {}", zipPath.getFileName(), publicUrl);
                if (sender.isPlayer()) {
                    sender.sendMessage("Your paste was uploaded to: " + publicUrl);
                }
                Utils.writeFile(zipPath.resolveSibling(zipPath.getFileName() + ".url").toString(), 
                        "[InternetShortcut]" + System.lineSeparator() +
                                "URL="+publicUrl + System.lineSeparator());
            }
        } catch (Exception e) {
            log.error("Failed to upload the debugpaste {}", zipPath, e);
            sender.sendMessage("Failed to upload the debugpaste, the file is still available in your server directory.");
        }
    }
    
    @Nonnull
    private static String eval(String... command) {
        try {
            try (InputStream in = Runtime.getRuntime().exec(command).getInputStream()) {
                return IOUtils.readInputStreamToString(in, Charset.defaultCharset()).trim();
            }
        } catch (Exception e) {
            return e.toString().trim();
        }
    }
}
