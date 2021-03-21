package cn.nukkit.command.defaults;

import cn.nukkit.Nukkit;
import cn.nukkit.Server;
import cn.nukkit.command.CapturingCommandSender;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.command.data.CommandParameter;
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
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Log4j2
public class DebugPasteCommand extends VanillaCommand {

    private static final String ENDPOINT = "https://debugpaste.powernukkit.org/paste.php";
    private static final String USER_AGENT = "PowerNukkit/"+ Nukkit.VERSION;

    public DebugPasteCommand(String name) {
        super(name, "%nukkit.command.debug.description", "%nukkit.command.debug.usage");
        this.setPermission("nukkit.command.debug.perform");
        this.commandParameters.clear();
        this.commandParameters.put("clear", new CommandParameter[]{
                CommandParameter.newEnum("clear", new String[]{"clear"})
        });
        
        this.commandParameters.put("upload", new CommandParameter[]{
                CommandParameter.newEnum("upload", new String[]{"upload"}),
                CommandParameter.newEnum("last", true, new String[]{"last"})
        });
        
        this.commandParameters.put("default", CommandParameter.EMPTY_ARRAY);
    }

    private static boolean filterValidPastes(Path file) {
        String name = file.getFileName().toString();
        return name.startsWith("debugpaste-") && name.endsWith(".zip");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        sender.sendMessage("The /debugpaste is executing, please wait...");
        Server server = Server.getInstance();
        if ((args.length != 1 || !"clear".equalsIgnoreCase(args[0])) &&
                TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - server.getLaunchTime()) < 15) {
            sender.sendMessage("Tip: This command works better if you use it right after you experience an issue");
        }
        server.getScheduler().scheduleAsyncTask(new AsyncTask() {
            @Override
            public void onRun() {
                if (args.length == 1 && "clear".equalsIgnoreCase(args[0])) {
                    clear(sender);
                    return;
                }
                
                if (args.length == 2 && "upload".equalsIgnoreCase(args[0]) && "last".equalsIgnoreCase(args[1])) {
                    uploadLast(sender);
                    return;
                }
                
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
                    b.append("ulimit:\n").append(eval( "sh", "-c", "ulimit -a")).append("\n\n");
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
                } else {
                    sender.sendMessage("Review the file and scrub the files as you wish, and run \"/debugpaste upload last\" to make it available online");
                }
            }
        });
        return true;
    }
    
    private static void clear(CommandSender sender) {
        Path dataPath = Paths.get(sender.getServer().getDataPath()).toAbsolutePath();
        Path pastesFolder = dataPath.resolve("debugpastes");
        try {
            AtomicInteger count = new AtomicInteger();
            if (Files.isDirectory(pastesFolder)) {
                try (Stream<Path> listing = Files.list(pastesFolder)) {
                    listing.filter(DebugPasteCommand::filterValidPastes)
                            .forEach(file -> {
                                try {
                                    Files.delete(file);
                                    count.incrementAndGet();
                                } catch (IOException e) {
                                    log.error("Could not delete {}", file, e);
                                }
                            });
                }
            }
            if (count.get() == 0) {
                sender.sendMessage("The debug pastes folder is already clean");
                return;
            }
            log.info("{} debug pastes were deleted by {}", count.get(), sender.getName());
            sender.sendMessage("All " + count.get() + " debug pastes were deleted");
        } catch (Exception e) {
            sender.sendMessage("Oh no! An error has occurred! " + e);
            log.error("Failed to delete {}", dataPath, e);
        }
    }
    
    private static void uploadLast(CommandSender sender) {
        Path dataPath = Paths.get(sender.getServer().getDataPath()).toAbsolutePath();
        Path pastesFolder = dataPath.resolve("debugpastes");
        Optional<Path> last = Optional.empty();
        try {
            try (Stream<Path> listing = Files.list(pastesFolder)) {
                last = listing.filter(Files::isRegularFile)
                        .filter(DebugPasteCommand::filterValidPastes)
                        .max(Comparator.comparing(file -> {
                            try {
                                BasicFileAttributes attributes = Files.readAttributes(file, BasicFileAttributes.class);
                                return attributes.creationTime().toMillis();
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        }));
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }

            if (!last.isPresent()) {
                sender.sendMessage("No debug pastes was found. Try to run \"/debugpaste upload\" to create a new one and send right away.");
                return;
            }

            Path lastPath = last.get();
            Path urlPath = lastPath.resolveSibling(lastPath.getFileName() + ".url");
            if (Files.isRegularFile(urlPath)) {
                Optional<String> url = Files.lines(urlPath).filter(line -> line.startsWith("URL=http") && line.length() > 8).findFirst();
                if (url.isPresent()) {
                    sender.sendMessage("The last debug paste " + lastPath.getFileName() + " was already uploaded to:");
                    String directUrl = url.get().substring(4);
                    sender.sendMessage(directUrl);
                    
                    if (!(sender instanceof ConsoleCommandSender)) {
                        sender.sendMessage("The url is also being logged in the console for convenience, so you can do CTRL+C, CTRL+V if you have console access.");
                        log.info("The last debug paste {} was already uploaded to: {}", lastPath.getFileName(), directUrl);
                    }
                    return;
                }
                String fileName = urlPath.getFileName().toString();
                Files.move(urlPath, urlPath.resolveSibling(fileName.substring(0, fileName.length() - 4) + System.currentTimeMillis() + ".url"));
            }
            
            upload(sender, lastPath);
        } catch (IOException e) {
            log.error("Failed to find the last debug paste", e);
            sender.sendMessage("Sorry, an error has occurred. Check the logs for details. "+ e);
        }
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
