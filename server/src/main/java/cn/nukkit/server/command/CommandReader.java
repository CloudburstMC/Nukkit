package cn.nukkit.server.command;

import cn.nukkit.server.InterruptibleThread;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.event.server.ServerCommandEvent;
import cn.nukkit.server.utils.completers.CommandsCompleter;
import cn.nukkit.server.utils.completers.PlayersCompleter;
import co.aikar.timings.Timings;
import jline.console.ConsoleReader;
import jline.console.CursorBuffer;

import java.io.IOException;

/**
 * author: MagicDroidX
 * Nukkit
 */
public class CommandReader extends Thread implements InterruptibleThread {

    private ConsoleReader reader;

    public static CommandReader instance;

    private CursorBuffer stashed;

    private boolean running = true;

    public static CommandReader getInstance() {
        return instance;
    }

    public CommandReader() {
        if (instance != null) {
            throw new RuntimeException("CommandReader is already initialized!");
        }
        try {
            this.reader = new ConsoleReader();
            reader.setPrompt("> ");
            instance = this;

            reader.addCompleter(new PlayersCompleter()); // Add player TAB completer
            reader.addCompleter(new CommandsCompleter()); // Add command TAB completer
        } catch (IOException e) {
            NukkitServer.getInstance().getLogger().error("Unable to start CommandReader", e);
        }
        this.setName("Console");
    }

    public String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void run() {
        Long lastLine = System.currentTimeMillis();
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                if (NukkitServer.getInstance().getConsoleSender() == null || NukkitServer.getInstance().getPluginManager() == null) {
                    continue;
                }

                if (!line.trim().isEmpty()) {
                    //todo 将即时执行指令改为每tick执行
                    try {
                        Timings.serverCommandTimer.startTiming();
                        ServerCommandEvent event = new ServerCommandEvent(NukkitServer.getInstance().getConsoleSender(), line);
                        NukkitServer.getInstance().getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            NukkitServer.getInstance().getScheduler().scheduleTask(() -> NukkitServer.getInstance().dispatchCommand(event.getSender(), event.getCommand()));
                        }
                        Timings.serverCommandTimer.stopTiming();
                    } catch (Exception e) {
                        NukkitServer.getInstance().getLogger().logException(e);
                    }

                } else if (System.currentTimeMillis() - lastLine <= 1) {
                    try {
                        sleep(40);
                    } catch (InterruptedException e) {
                        NukkitServer.getInstance().getLogger().logException(e);
                    }
                }
                lastLine = System.currentTimeMillis();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        this.running = false;
    }

    public synchronized void stashLine() {
        this.stashed = reader.getCursorBuffer().copy();
        try {
            reader.getOutput().write("\u001b[1G\u001b[K");
            reader.flush();
        } catch (IOException e) {
            // ignore
        }
    }

    public synchronized void unstashLine() {
        try {
            reader.resetPromptLine("> ", this.stashed.toString(), this.stashed.cursor);
        } catch (IOException e) {
            // ignore
        }
    }

    public void removePromptLine() {
        try {
            reader.resetPromptLine("", "", 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
