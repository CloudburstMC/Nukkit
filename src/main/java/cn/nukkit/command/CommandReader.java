package cn.nukkit.command;

import cn.nukkit.Server;
import cn.nukkit.utils.MainLogger;
import cn.nukkit.utils.TextFormat;
import jline.console.ConsoleReader;
import jline.console.CursorBuffer;

import java.io.IOException;

/**
 * author: MagicDroidX
 * Nukkit
 */
public class CommandReader extends cn.nukkit.Thread {

    private ConsoleReader reader;

    public static CommandReader instance;

    private CursorBuffer stashed;

    public static CommandReader getInstance() {
        return instance;
    }

    public CommandReader() {
        if (instance != null) {
            throw new RuntimeException("Command Reader is already exist");
        }
        try {
            this.reader = new ConsoleReader();
            reader.setPrompt("> ");
            instance = this;
        } catch (IOException e) {
            Server.getInstance().getLogger().error("Unable to start Console Reader");
        }
        this.setName("Console");
        this.start();
    }

    public void run() {
        MainLogger logger = Server.getInstance().getLogger();
        Long lastLine = System.currentTimeMillis();
        while (true) {
            String line = "";
            try {
                reader.resetPromptLine("", "", 0);
                line = this.reader.readLine("> ");
            } catch (IOException e) {
                logger.logException(e);
            }
            if (!line.trim().equals("")) {
                //todo
                logger.notice(TextFormat.LIGHT_PURPLE + "Using command: " + line);
                if (line.trim().toLowerCase().equals("stop")) {
                    Server.getInstance().shutdown();
                    /*logger.info(TextFormat.YELLOW + "NUKKIT EXITING...");
                    try {
                        reader.resetPromptLine("", "", 0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.exit(0);*/
                }

            } else if (System.currentTimeMillis() - lastLine <= 1) {
                try {
                    sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            lastLine = System.currentTimeMillis();
        }
    }

    public void stashLine() {
        this.stashed = reader.getCursorBuffer().copy();
        try {
            reader.getOutput().write("\u001b[1G\u001b[K");
            reader.flush();
        } catch (IOException e) {
            // ignore
        }
    }

    public void unstashLine() {
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
