package cn.nukkit.command;

import cn.nukkit.Server;
import cn.nukkit.utils.MainLogger;
import cn.nukkit.utils.TextFormat;
import jline.console.ConsoleReader;

import java.io.IOException;

/**
 * author: MagicDroidX
 * Nukkit
 */
public class CommandReader extends Thread {

    ConsoleReader reader;

    public CommandReader() {
        try {
            this.reader = new ConsoleReader();
        } catch (IOException e) {
            Server.getInstance().getLogger().error("无法启动 Console Reader");
        }
        this.start();
    }

    public void run() {
        MainLogger logger = Server.getInstance().getLogger();
        Long lastLine = System.currentTimeMillis();
        while (true) {
            String line = "";
            try {
                line = this.reader.readLine("> ");
            } catch (IOException e) {
                logger.logException(e);
            }
            if (!line.equals("")) {
                logger.info(TextFormat.LIGHT_PURPLE + "使用了指令：" + line);
            } else if (System.currentTimeMillis() - lastLine <= 1) {
                try {
                    this.sleep(40);
                } catch (InterruptedException e) {
                    logger.logException(e);
                }
            }
            lastLine = System.currentTimeMillis();
        }
    }
}
