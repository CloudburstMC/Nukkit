package cn.nukkit.test;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author joserobjr
 */
public class LogLevelAdjuster {
    private Map<Class<?>, Level> adjustedClasses = new LinkedHashMap<>();
    
    public synchronized void setLevel(Class<?> c, Level level) {
        adjustedClasses.computeIfAbsent(c, this::getLevel);
        applyLevel(c, level);
    }
    
    public void onlyNow(Class<?> c, Level level, Runnable runnable) {
        Level original = getLevel(c);
        setLevel(c, level);
        try {
            runnable.run();
        } finally {
            setLevel(c, original);
        }
    }

    public <V> V onlyNow(Class<?> c, Level level, Callable<V> runnable) throws Exception {
        Level original = getLevel(c);
        setLevel(c, level);
        try {
            return runnable.call();
        } finally {
            setLevel(c, original);
        }
    }
    
    public Level getLevel(Class<?> c) {
        return LogManager.getLogger(c).getLevel();
    }
    
    private void applyLevel(Class<?> c, Level level) {
        Configurator.setLevel(LogManager.getLogger(c).getName(), level);
    }
    
    public synchronized void restoreLevel(Class<?> c) {
        Level level = adjustedClasses.remove(c);
        if (level != null) {
            applyLevel(c, level);
        }
    }
    
    public synchronized void restoreLevels() {
        adjustedClasses.forEach(this::applyLevel);
        adjustedClasses.clear();
    }
}
