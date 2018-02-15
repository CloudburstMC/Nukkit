package cn.nukkit.server.lang;

import cn.nukkit.api.message.Message;
import cn.nukkit.api.message.TranslationMessage;
import cn.nukkit.api.util.TextFormat;
import cn.nukkit.server.Bootstrap;
import cn.nukkit.server.NukkitServer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Formatter;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NukkitLocaleManager {
    private static final Pattern colorPattern = Pattern.compile("((?>" + TextFormat.FORMAT_CHAR + "[0-9A-FK-OR])*?)(.+)");
    private static final ClassLoader LOADER = Bootstrap.class.getClassLoader();
    private static final String LANGUAGE_PATH = "lang/";
    private static final String EXTENSION = ".lang";
    private final Map<Locale, Map<String, String>> locales = new ConcurrentHashMap<>();

    public NukkitLocaleManager() throws Exception {
        loadDefaultLocales();
    }

    private void loadDefaultLocales() throws Exception {
        InputStream langList = LOADER.getResourceAsStream(LANGUAGE_PATH + "language_names.json");
        JsonNode langNode = NukkitServer.JSON_MAPPER.readTree(langList);
        Preconditions.checkArgument(langNode.getNodeType() == JsonNodeType.ARRAY, "Language array not found");

        for (JsonNode node: langNode) {
            Preconditions.checkArgument(node.getNodeType() == JsonNodeType.STRING, "Expected String for locale code");
            String localeCode = node.asText();

            Locale locale = getLocaleByString(localeCode);

            loadLocale(LOADER.getResourceAsStream(LANGUAGE_PATH + locale.toString() + EXTENSION), locale, true);
        }
    }

    public void loadLocale(Path pathToLanguage, Locale locale) throws Exception {
        Preconditions.checkNotNull(pathToLanguage, "pathToLangauge");
        Preconditions.checkNotNull(locale, "locale");
        Preconditions.checkArgument(Files.isRegularFile(pathToLanguage) && !Files.isDirectory(pathToLanguage), "path was not to a file");
        InputStream stream = Files.newInputStream(pathToLanguage);
        loadLocale(stream, locale, false);
    }

    private void loadLocale(InputStream stream, Locale locale, boolean defaults) throws IOException {
        Map<String, String> newReplacements = NukkitServer.PROPERTIES_MAPPER.readValue(stream, new TypeReference<Map<String, String>>(){});

        Map<String, String> replacements = locales.get(locale);
        if (replacements == null && defaults) {
            replacements = new ConcurrentHashMap<>();
        } else {
            throw new IllegalArgumentException("Locale is not supported.");
        }
        replacements.putAll(newReplacements);
    }

    public Locale getLocaleByString(String localeString) {
        Preconditions.checkNotNull(localeString, "localeString");
        String[] localStringParts = localeString.split("_");
        Preconditions.checkArgument(localStringParts.length > 1, "Invalid format");
        return new Locale(localStringParts[0], localStringParts[1]);
    }

    public String replaceI18n(String i18n, Object... objects) throws RuntimeException {
        return replaceI18n(Locale.getDefault(), i18n, objects);
    }

    public String replaceI18n(Locale locale, String string, Object... objects) throws RuntimeException {
        Map<String, String> replacements = locales.get(locale);

        Matcher matcher = colorPattern.matcher(string);
        if (!matcher.find()) {
            throw new IllegalArgumentException("No valid i18n code was found");
        }
        String i18n = matcher.group();
        if (!replacements.containsKey(i18n)) {
            throw new IllegalArgumentException("i18n code is not registered");
        }
        String l10n = replacements.get(i18n);

        Formatter formatter = new Formatter(locale);
        return colorPattern.matcher(string).replaceFirst(formatter.format(l10n, objects).toString());
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
