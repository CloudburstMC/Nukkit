package com.nukkitx.server.locale;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.nukkitx.api.locale.LocaleManager;
import com.nukkitx.api.message.Message;
import com.nukkitx.api.message.TranslationMessage;
import com.nukkitx.server.NukkitServer;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class NukkitLocaleManager implements LocaleManager {
    private static final Pattern I18N_PATTERN = Pattern.compile("%([a-z0-9.]+)");
    private static final ClassLoader LOADER = NukkitServer.class.getClassLoader();
    private static final String LANGUAGE_PATH = "lang/";
    private static final String EXTENSION = ".lang";
    private final Map<Locale, Properties> locales = new ConcurrentHashMap<>();
    private final Set<Locale> availableLocales = new HashSet<>();

    public NukkitLocaleManager() throws IOException {
        loadAvailableLocales();
    }

    private void loadAvailableLocales() throws IOException {
        InputStream langList = LOADER.getResourceAsStream(LANGUAGE_PATH + "nukkit/languages.json");
        JsonNode langNode = NukkitServer.JSON_MAPPER.readTree(langList);
        Preconditions.checkNotNull(langNode, "Unable to load language list");
        Preconditions.checkArgument(langNode.getNodeType() == JsonNodeType.ARRAY, "Language array not found");

        for (JsonNode node: langNode) {
            Preconditions.checkArgument(node.getNodeType() == JsonNodeType.STRING, "Expected String for locale code");
            String localeCode = node.asText();

            Locale locale = getLocaleByString(localeCode);

            availableLocales.add(locale);
        }
    }

    public boolean loadDefaultLocale(Locale locale) throws IOException {
        Preconditions.checkNotNull(locale, "locale");
        Preconditions.checkArgument(availableLocales().contains(locale), "Locale is unavailable");

        return loadLocale("nukkit/", locale) && loadLocale("vanilla/", locale);
    }

    private boolean loadLocale(String directory, Locale locale) throws IOException {
        String path = LANGUAGE_PATH + directory + locale.toString() + EXTENSION;
        InputStream stream = LOADER.getResourceAsStream(path);
        if (stream == null) {
            return false;
        }
        loadLocale(stream, locale);
        return true;
    }

    public void loadLocale(Path pathToLanguage, Locale locale) throws IOException {
        Preconditions.checkNotNull(pathToLanguage, "pathToLanguage");
        Preconditions.checkArgument(Files.isRegularFile(pathToLanguage) && !Files.isDirectory(pathToLanguage), "path was not to a file");
        InputStream stream = Files.newInputStream(pathToLanguage);
        loadLocale(stream, locale);
    }

    public void loadLocale(InputStream stream, Locale locale) throws IOException {
        Preconditions.checkNotNull(stream, "stream");
        Preconditions.checkNotNull(locale, "locale");
        Properties properties;
        if ((properties = locales.get(locale)) == null) {
            properties = new Properties();
            locales.put(locale, properties);
            availableLocales.add(locale);
        }
        log.info("Loading {} stream to properties", locale);
        properties.load(stream);
    }

    public Locale getLocaleByString(String localeString) {
        Preconditions.checkNotNull(localeString, "localeString");
        String[] localStringParts = localeString.split("_");
        Preconditions.checkArgument(localStringParts.length > 1, "Invalid format");
        return new Locale(localStringParts[0], localStringParts[1]);
    }

    public String replaceI18n(String i18n, Object... objects) {
        return replaceI18n(Locale.getDefault(), i18n, objects);
    }

    public String replaceI18n(Locale locale, String string, Object... objects) {
        Preconditions.checkNotNull(locale, "locale");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(string), "string is null or empty");
        Properties properties = locales.get(locale);

        if (properties == null) {
            properties = locales.get(Locale.US);
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

        if (!properties.containsKey(i18n)) {
            return string;
        }

        String l10n = properties.getProperty(i18n);
        i18n = (percentBreak ? '%' : "") + i18n;
        Formatter formatter = new Formatter(locale);
        return string.replace(i18n, formatter.format(l10n, objects).toString());
    }

    public ImmutableList<Locale> availableLocales() {
        return ImmutableList.copyOf(availableLocales);
    }

    public boolean isFallbackAvailable() {
        return availableLocales.contains(Locale.US);
    }

    public boolean isLocaleAvailable(Locale locale) {
        return availableLocales.contains(locale);
    }

    public String getMessage(Message message) {
        if (message instanceof TranslationMessage) {
            return replaceI18n(message.getMessage(), (Object[]) ((TranslationMessage) message).getParameters());
        } else {
            return message.getMessage();
        }
    }
}
