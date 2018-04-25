package cn.nukkit.api.locale;

import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Locale;

public interface LocaleManager {

    void loadLocale(Path pathToLanguage, Locale locale) throws IOException;

    void loadLocale(InputStream stream, Locale locale) throws IOException;

    String replaceI18n(String i18n, Object... objects);

    String replaceI18n(Locale locale, String string, Object... objects);

    boolean isLocaleAvailable(Locale locale);

    ImmutableList<Locale> availableLocales();
}
