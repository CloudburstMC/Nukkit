package cn.nukkit.locale;

import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class LocaleManager {

    public static final LocaleManager INSTANCE = new LocaleManager();

    private final Map<Locale, Properties> locales = new ConcurrentHashMap<>();

    private LocaleManager() {
    }

}
