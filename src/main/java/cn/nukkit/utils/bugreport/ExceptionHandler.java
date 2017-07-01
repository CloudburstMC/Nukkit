package cn.nukkit.utils.bugreport;

import cn.nukkit.Server;
import cn.nukkit.lang.BaseLang;

/**
 * Project nukkit
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    public static void registerExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        handle(thread, throwable);
    }

    public void handle(Thread thread, Throwable throwable) {
        throwable.printStackTrace();

        try {
            new BugReportGenerator(throwable).start();
        } catch (Exception exception) {
            // Fail Safe
        }
    }

}