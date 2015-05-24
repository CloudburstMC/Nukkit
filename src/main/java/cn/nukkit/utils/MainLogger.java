package cn.nukkit.utils;

import org.fusesource.jansi.AnsiConsole;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * author: MagicDroidX
 * Nukkit
 */
public class MainLogger extends Thread {
    protected File logFile;
    protected String logStream = "";
    protected boolean shutdown;
    protected boolean logDebug = false;
    protected boolean enable_Ansi = false;

    protected static MainLogger logger;

    public MainLogger(String logFile) {
        this(logFile, false, false);
    }

    public MainLogger(String logFile, Boolean logDebug) {
        this(logFile, logDebug, false);
    }

    public MainLogger(String logFile, Boolean logDebug, Boolean enable_Ansi) {
        AnsiConsole.systemInstall();

        if (logger != null) {
            throw new RuntimeException("MainLogger has been already created");
        }
        logger = this;
        this.logFile = new File(logFile);
        if (!this.logFile.exists()) {
            try {
                this.logFile.createNewFile();
            } catch (IOException e) {
                this.logException(e);
            }
        }
        this.logDebug = logDebug;
        this.enable_Ansi = enable_Ansi;
        this.start();
    }

    public static MainLogger getLogger() {
        return logger;
    }

    public void emergency(String message) {
        this.send(TextFormat.RED + "[EMERGENCY] " + message);
    }

    public void alert(String message) {
        this.send(TextFormat.RED + "[ALERT] " + message);
    }

    public void critical(String message) {
        this.send(TextFormat.RED + "[CRITICAL] " + message);
    }

    public void error(String message) {
        this.send(TextFormat.DARK_RED + "[ERROR] " + message);
    }

    public void warning(String message) {
        this.send(TextFormat.YELLOW + "[WARNING] " + message);
    }

    public void notice(String message) {
        this.send(TextFormat.AQUA + "[NOTICE] " + message);
    }

    public void info(String message) {
        this.send(TextFormat.WHITE + "[INFO] " + message);
    }

    public void debug(String message) {
        if (!this.logDebug) {
            return;
        }
        this.send(TextFormat.GRAY + "[DEBUG] " + message);
    }

    public void setLogDebug(Boolean logDebug) {
        this.logDebug = logDebug;
    }

    public void logException(Exception e) {
        this.logException(e, e.getStackTrace());
    }

    public void logException(Exception e, StackTraceElement[] trace) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        this.alert(stringWriter.toString().toUpperCase());
    }

    public void shutdown() {
        this.shutdown = true;
    }

    protected void send(String message) {
        this.send(message, -1);
    }

    protected void send(String message, int level) {
        Date now = new Date();
        String cleanMessage = TextFormat.clean(message);
        message = TextFormat.toANSI(TextFormat.AQUA + new SimpleDateFormat("hh:mm:ss").format(now) + TextFormat.RESET + " " + message + TextFormat.RESET);
        if (this.enable_Ansi) {
            System.out.println(message);
        } else {
            System.out.println(cleanMessage);
        }

        String str = new SimpleDateFormat("Y-m-d hh:mm:ss").format(now) + " " + cleanMessage + "" + "\r\n";
        synchronized (this) {
            this.logStream += str;
            this.notify();
        }
    }

    public void run() {
        this.shutdown = false;
        while (!this.shutdown) {
            synchronized (this) {
                if (this.logStream.length() > 0) {
                    String chunk = this.logStream;
                    this.logStream = "";
                    try {
                        FileWriter fileWriter = new FileWriter(this.logFile, true);
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                        bufferedWriter.write(chunk);
                        bufferedWriter.close();
                    } catch (IOException e) {
                        this.logException(e);
                    }
                }
                try {
                    this.wait(250000);
                } catch (InterruptedException e) {
                    this.logException(e);
                }
            }
        }

        if (this.logStream.length() > 0) {
            String chunk = this.logStream;
            this.logStream = "";
            try {
                FileWriter fileWriter = new FileWriter(this.logFile, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(chunk);
                bufferedWriter.close();
            } catch (IOException e) {
                this.logException(e);
            }
        }
    }

}
