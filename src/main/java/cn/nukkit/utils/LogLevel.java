package cn.nukkit.utils;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public enum LogLevel implements Comparable<LogLevel> {
    NONE {
        @Override
        public void log(MainLogger logger, String message) {
        }
    },
    EMERGENCY {
        @Override
        public void log(MainLogger logger, String message) {
            logger.emergency(message);
        }
    },
    ALERT {
        @Override
        public void log(MainLogger logger, String message) {
            logger.alert(message);
        }
    },
    CRITICAL {
        @Override
        public void log(MainLogger logger, String message) {
            logger.critical(message);
        }
    },
    ERROR {
        @Override
        public void log(MainLogger logger, String message) {
            logger.error(message);
        }
    },
    WARNING {
        @Override
        public void log(MainLogger logger, String message) {
            logger.warning(message);
        }
    },
    NOTICE {
        @Override
        public void log(MainLogger logger, String message) {
            logger.notice(message);
        }
    },
    INFO {
        @Override
        public void log(MainLogger logger, String message) {
            logger.info(message);
        }
    },
    DEBUG {
        @Override
        public void log(MainLogger logger, String message) {
            logger.debug(message);
        }
    };

    public static final LogLevel DEFAULT_LEVEL = INFO;

    public abstract void log(MainLogger logger, String message);

    int getLevel() {
        return ordinal();
    }
}
