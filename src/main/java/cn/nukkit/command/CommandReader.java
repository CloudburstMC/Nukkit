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
public class CommandReader extends Thread {

    private ConsoleReader reader;

    public static CommandReader instance;

    private CursorBuffer stashed;

    public static CommandReader getInstance() {
        return instance;
    }

    //Console reader;

    public CommandReader() {
        //reader = System.console();
        /*if (reader == null) {
            throw new IllegalStateException("Unable to start console reader");
        }*/
        if (instance != null) {
            throw new RuntimeException("Command Reader is already exist");
        }
        try {
            this.reader = new ConsoleReader();
            reader.setPrompt("> ");
            instance = this;
        } catch (IOException e) {
            Server.getInstance().getLogger().error("无法启动 Console Reader");
        }
        this.start();
    }

    public void run() {
        MainLogger logger = Server.getInstance().getLogger();
        Long lastLine = System.currentTimeMillis();
        while (true) {
            //String line = reader.readLine("> ");

            String line = "";
            try {
                //line = this.reader.readLine();
                reader.resetPromptLine("", "", 0);
                line = this.reader.readLine("> ");
            } catch (IOException e) {
                logger.logException(e);
            }
            if (!line.trim().equals("")) {
                if (line.trim().toLowerCase().equals("stop")) {
                    logger.info(TextFormat.YELLOW + "NUKKIT EXITING...");
                    try {
                        reader.resetPromptLine("", "", 0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
                }
                logger.notice(TextFormat.LIGHT_PURPLE + "使用了指令：" + line);
            } else if (System.currentTimeMillis() - lastLine <= 1) {
                try {
                    sleep(40);
                } catch (InterruptedException e) {
                    logger.logException(e);
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
        /*if (this.stashed.toString().isEmpty()) {
            return;
        }*/
        try {
            reader.resetPromptLine("> ", this.stashed.toString(), this.stashed.cursor);
        } catch (IOException e) {
            // ignore
        }
    }

}
