package cn.nukkit.locale;

import cn.nukkit.Nukkit;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Log4j2
public class LocaleManager {
    public static final Locale FALLBACK_LOCALE = Locale.US;

    private static final Pattern I18N_PATTERN = Pattern.compile("%([A-Za-z0-9._\\-]+)");
    private static final String LANG_FILE_EXTENSION = ".lang";

    private final Set<Locale> availableLocales;
    private final Properties texts = new Properties();
    private final Path[] textPaths;
    private Locale locale;

    public LocaleManager(String languagesPath, String... textPathStrings) {
        Path path;
        Path[] textPaths = new Path[textPathStrings.length];
        try {
            path = Paths.get(ClassLoader.getSystemResource(languagesPath).toURI());

            for (int i = 0; i < textPathStrings.length; i++) {
                textPaths[i] = Paths.get(ClassLoader.getSystemResource(textPathStrings[i]).toURI());
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid resource path", e);
        }
        this.availableLocales = loadAvailableLocales(path);
        this.textPaths = textPaths;
    }

    public LocaleManager(Set<Locale> availableLocales, Path... textPaths) {
        this.availableLocales = checkNotNull(availableLocales, "availableLocales");
        this.textPaths = checkNotNull(textPaths, "textPaths");
    }

    public static ImmutableSet<Locale> loadAvailableLocales(Path languagesPath) {
        ImmutableSet.Builder<Locale> builder = ImmutableSet.builder();
        try (InputStream stream = Files.newInputStream(languagesPath)) {
            JsonNode array = Nukkit.JSON_MAPPER.readTree(stream);
            for (JsonNode element : array) {
                builder.add(getLocaleFromString(element.textValue()));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to load language list", e);
        }
        return builder.build();
    }

    public static Locale getLocaleFromString(String localeString) {
        checkNotNull(localeString, "localeString");
        String[] codes = localeString.split("_");
        checkArgument(codes.length == 2, "Invalid language country code");
        return new Locale(codes[0], codes[1]);
    }

    public Set<Locale> getAvailableLocales() {
        return availableLocales;
    }

    public void setLocaleOrFallback(String localeString) {
        if (!setLocale(localeString)) {
            this.setLocale(FALLBACK_LOCALE);
        }
    }

    public boolean setLocale(String localeString) {
        try {
            Locale locale = getLocaleFromString(localeString);
            this.setLocale(locale);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void loadTransforms(Path path) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(path)))) {
            String line;
            while ((line = br.readLine()) != null) {
                int equals = line.indexOf('=');
                if (equals == -1) {
                    continue;
                }
                String key = line.substring(0, equals);
                int comment = line.indexOf('#');
                String value = line.substring(equals + 1, comment == -1 ? line.length() : comment).trim();
                this.texts.setProperty(key, value);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to load transforms", e);
        }
    }

    public String translate(TextContainer textContainer) {
        return this.translate(textContainer.getText());
    }

    public String translate(String string, Object... objects) {
        Preconditions.checkNotNull(string, "string is null");
        if (string.isEmpty()) {
            return string;
        }

        String i18n;
        boolean percentBreak = false;
        Matcher matcher = I18N_PATTERN.matcher(string);

        if (matcher.find()) {
            i18n = matcher.group(1);
            percentBreak = true;
        } else {
            i18n = string;
        }

        Object[] args = objects == null ? new Object[0] : objects;

        for (int i = 0; i < args.length; i++) {
            String arg = Objects.toString(objects[i]);
            Matcher argMatcher = I18N_PATTERN.matcher(arg);
            if (argMatcher.matches()) {
                String match = argMatcher.group(1);
                if (this.texts.containsKey(match)) {
                    arg = this.texts.getProperty(match);
                }
                args[i] = arg;
            }
        }

        if (!this.texts.containsKey(i18n)) {
            return string;
        }

        String l10n = this.texts.getProperty(i18n);
        i18n = (percentBreak ? '%' : "") + i18n;
        return string.replace(i18n, String.format(l10n, args));
    }

    public String translateOnly(String prefix, String string, Object... objects) {
        Preconditions.checkNotNull(string, "string is null");
        if (string.isEmpty()) {
            return string;
        }

        String i18n;
        boolean percentBreak = false;
        Matcher matcher = I18N_PATTERN.matcher(string);

        if (matcher.find()) {
            i18n = matcher.group(1);
            percentBreak = true;
        } else {
            i18n = string;
        }

        Object[] args = objects == null ? new Object[0] : objects;

        for (int i = 0; i < args.length; i++) {
            String arg = objects[i].toString();
            Matcher argMatcher = I18N_PATTERN.matcher(arg);
            if (argMatcher.matches()) {
                String match = argMatcher.group(1);
                if (i18n.startsWith(prefix) && this.texts.containsKey(match)) {
                    arg = this.texts.getProperty(match);
                    args[i] = arg;
                }
            }
        }

        if (!this.texts.containsKey(i18n) || !i18n.startsWith(prefix)) {
            return string;
        }

        String l10n = this.texts.getProperty(i18n);
        i18n = (percentBreak ? '%' : "") + i18n;
        return string.replace(i18n, String.format(l10n, args));
    }

    public String get(String key) {
        return this.texts.getProperty(key);
    }

    public Locale getLocale() {
        return locale;
    }

    public synchronized void setLocale(Locale locale) {
        checkNotNull(locale, "locale");
        checkArgument(this.availableLocales.contains(locale), "locale is not available");
        if (this.locale != null && this.locale.equals(locale)) {
            return; // Same locale
        }
        this.locale = locale;

        this.texts.clear(); // Clear any existing
        for (Path path : this.textPaths) {
            this.loadTransforms(path.resolve(locale + LANG_FILE_EXTENSION));
        }
        this.texts.setProperty("language", locale.toString());
    }
}
