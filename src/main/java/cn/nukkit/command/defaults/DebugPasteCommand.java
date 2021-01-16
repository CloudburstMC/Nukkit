package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.CapturingCommandSender;
import cn.nukkit.command.CommandSender;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginDescription;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.HastebinUtility;
import cn.nukkit.utils.Utils;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

@Log4j2
public class DebugPasteCommand extends VanillaCommand {

    public DebugPasteCommand(String name) {
        super(name, "%nukkit.command.debug.description", "%nukkit.command.debug.usage");
        this.setPermission("nukkit.command.debug.perform");
        this.commandParameters.clear();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        Server server = Server.getInstance();
        server.getScheduler().scheduleAsyncTask(new AsyncTask() {
            @Override
            public void onRun() {
                try {
                    CapturingCommandSender capturing = new CapturingCommandSender();
                    capturing.setOp(true);
                    new StatusCommand("status").execute(capturing, "status", new String[]{});
                    String dataPath = server.getDataPath();
                    String status = HastebinUtility.upload(capturing.getCleanCapture());
                    String nukkitYML = HastebinUtility.upload(new File(dataPath, "nukkit.yml"));
                    String serverProperties = HastebinUtility.upload(new File(dataPath, "server.properties"));
                    String latestLog = HastebinUtility.upload(new File(dataPath, "/logs/server.log"));
                    String threadDump = HastebinUtility.upload(Utils.getAllThreadDumps());

                    StringBuilder b = new StringBuilder();
                    b.append("# Files\n");
                    b.append("links.nukkit_yml: ").append(nukkitYML).append('\n');
                    b.append("links.server_properties: ").append(serverProperties).append('\n');
                    b.append("links.server_status: ").append(status).append('\n');
                    b.append("links.server_log: ").append(latestLog).append('\n');
                    b.append("links.thread_dump: ").append(threadDump).append('\n');
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
                    b.append("os.version: '").append(System.getProperty("os.version")).append("'\n\n");
                    b.append("\n# Create a ticket: https://github.com/PowerNukkit/PowerNukkit/issues/new");
                    String link = HastebinUtility.upload(b.toString());
                    sender.sendMessage(link);
                } catch (IOException e) {
                    log.error("Failed to upload debug paste, hastebin might be down", e);
                }
            }
        });
        return true;
    }
}
