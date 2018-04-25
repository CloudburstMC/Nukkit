package cn.nukkit.server.locale;

import cn.nukkit.api.locale.LocaleManager;
import cn.nukkit.api.message.Message;
import cn.nukkit.api.message.TranslationMessage;
import cn.nukkit.api.util.TextFormat;
import cn.nukkit.server.Bootstrap;
import cn.nukkit.server.NukkitServer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Formatter;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Log4j2
public class NukkitLocaleManager implements LocaleManager {
    private static final Pattern colorPattern = Pattern.compile("((?>" + TextFormat.FORMAT_CHAR + "[0-9A-FK-OR])*?)(.+)");
    private static final ClassLoader LOADER = Bootstrap.class.getClassLoader();
    private static final String LANGUAGE_PATH = "lang/";
    private static final String EXTENSION = ".lang";
    private final Map<Locale, Properties> locales = new ConcurrentHashMap<>();

    public NukkitLocaleManager() throws Exception {
        loadDefaultLocales();
    }

    private void loadDefaultLocales() throws Exception {
        InputStream langList = LOADER.getResourceAsStream(LANGUAGE_PATH + "languages.json");
        JsonNode langNode = NukkitServer.JSON_MAPPER.readTree(langList);
        Preconditions.checkNotNull(langNode, "Unable to load language list");
        Preconditions.checkArgument(langNode.getNodeType() == JsonNodeType.ARRAY, "Language array not found");

        for (JsonNode node: langNode) {
            Preconditions.checkArgument(node.getNodeType() == JsonNodeType.STRING, "Expected String for locale code");
            String localeCode = node.asText();

            Locale locale = getLocaleByString(localeCode);

            InputStream stream = LOADER.getResourceAsStream(LANGUAGE_PATH + locale.toString() + EXTENSION);
            if (stream == null) {
                continue;
            }

            loadLocale(stream, locale);
        }
    }

    public void loadLocale(Path pathToLanguage, Locale locale) throws IOException {
        Preconditions.checkNotNull(pathToLanguage, "pathToLangauge");
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
        }
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
        Properties properties = locales.get(locale);

        String i18n = TextFormat.removeFormatting(string);

        if (!properties.containsKey(i18n)) {
            return string;
        }
        String l10n = properties.getProperty(i18n);

        Formatter formatter = new Formatter(locale);
        return string.replace(i18n, formatter.format(l10n, objects).toString());
    }

    public ImmutableList<Locale> availableLocales() {
        return ImmutableList.copyOf(locales.keySet());
    }

    public boolean isFallbackAvailable() {
        return locales.containsKey(Locale.US);
    }

    public boolean isLocaleAvailable(Locale locale) {
        return locales.containsKey(locale);
    }

    public String getMessage(Message message) {
        if (message instanceof TranslationMessage) {
            return replaceI18n(message.getMessage(), ((TranslationMessage) message).getParameters());
        } else {
            return message.getMessage();
        }
    }
}
