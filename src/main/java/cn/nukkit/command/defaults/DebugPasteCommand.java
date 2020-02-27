package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.player.IPlayer;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginDescription;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.HastebinUtility;
import cn.nukkit.utils.Utils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import static cn.nukkit.command.args.OfflinePlayerArgument.offlinePlayer;
import static cn.nukkit.command.args.PlayerArgument.getPlayer;

@Log4j2
public class DebugPasteCommand extends BaseCommand {

    public DebugPasteCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("debugpaste", "%nukkit.command.debug.description");
        setPermission("nukkit.command.debug.perform");

        dispatcher.register(literal("debugpaste").executes(this::run));
    }

    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();

        if (!this.testPermission(source)) {
            return -1;
        }

        Server server = source.getServer();
        server.getScheduler().scheduleAsyncTask(new AsyncTask() {
            @Override
            public void onRun() {
                try {
                    // TODO
                    //new StatusCommand("status").execute(server.getConsoleSender(), "status", new String[]{});
                    String dataPath = server.getDataPath();
                    String nukkitYML = HastebinUtility.upload(new File(dataPath, "nukkit.yml"));
                    String serverProperties = HastebinUtility.upload(new File(dataPath, "server.properties"));
                    String latestLog = HastebinUtility.upload(new File(dataPath, "/logs/server.log"));
                    String threadDump = HastebinUtility.upload(Utils.getAllThreadDumps());

                    StringBuilder b = new StringBuilder();
                    b.append("# Files\n");
                    b.append("links.nukkit_yml: ").append(nukkitYML).append('\n');
                    b.append("links.server_properties: ").append(serverProperties).append('\n');
                    b.append("links.server_log: ").append(latestLog).append('\n');
                    b.append("links.thread_dump: ").append(threadDump).append('\n');
                    b.append("\n# Server Information\n");

                    b.append("version.api: ").append(server.getApiVersion()).append('\n');
                    b.append("version.nukkit: ").append(server.getNukkitVersion()).append('\n');
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
                    b.append("\n# Create a ticket: https://github.com/NukkitX/Nukkit/issues/new");
                    String link = HastebinUtility.upload(b.toString());
                    source.sendMessage(link);
                } catch (IOException e) {
                    log.error("Error creating debug paste", e);
                }
            }
        });
        return 1;
    }
}
