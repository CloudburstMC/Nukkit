package cn.nukkit.lang;

import cn.nukkit.Server;
import cn.nukkit.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author  MagicDroidX
 * Nukkit Project
 */
public class BaseLang {

    public static final String FALLBACK_LANGUAGE = "eng";

    protected final String langName;

    protected Map<String, String> lang = new HashMap<>();
    protected Map<String, String> fallbackLang = new HashMap<>();

    public BaseLang(String lang) {
        this(lang, null);
    }

    public BaseLang(String lang, String path) {
        this(lang, path, FALLBACK_LANGUAGE);
    }

    public BaseLang(String lang, String path, String fallback) {
        this.langName = lang.toLowerCase();
        boolean useFallback = !lang.equals(fallback);

        if (path == null) {
            path = "lang/";
            this.lang = this.loadLang(this.getClass().getClassLoader().getResourceAsStream(path + this.langName + "/lang.ini"));
            if (useFallback) this.fallbackLang = this.loadLang(this.getClass().getClassLoader().getResourceAsStream(path + fallback + "/lang.ini"));
        } else {
            this.lang = this.loadLang(path + this.langName + "/lang.ini");
            if (useFallback) this.fallbackLang = this.loadLang(path + fallback + "/lang.ini");
        }
        if (this.fallbackLang == null) this.fallbackLang = this.lang;
    }

    public Map<String, String> getLangMap() {
        return lang;
    }

    public Map<String, String> getFallbackLangMap() {
        return fallbackLang;
    }

    public String getName() {
        return this.get("language.name");
    }

    public String getLang() {
        return langName;
    }

    protected Map<String, String> loadLang(String path) {
        try {
            String content = Utils.readFile(path);
            Map<String, String> d = new HashMap<>();
            for (String line : content.split("\n")) {
                line = line.trim();
                if (line.isEmpty() || line.charAt(0) == '#') {
                    continue;
                }
                String[] t = line.split("=");
                if (t.length < 2) {
                    continue;
                }
                String key = t[0];
                StringBuilder value = new StringBuilder();
                for (int i = 1; i < t.length - 1; i++) {
                    value.append(t[i]).append("=");
                }
                value.append(t[t.length - 1]);
                if (value.length() == 0) {
                    continue;
                }
                d.put(key, value.toString());
            }
            return d;
        } catch (IOException e) {
            Server.getInstance().getLogger().logException(e);
            return null;
        }
    }

    protected Map<String, String> loadLang(InputStream stream) {
        try {
            String content = Utils.readFile(stream);
            Map<String, String> d = new HashMap<>();
            for (String line : content.split("\n")) {
                line = line.trim();
                if (line.isEmpty() || line.charAt(0) == '#') {
                    continue;
                }
                String[] t = line.split("=");
                if (t.length < 2) {
                    continue;
                }
                String key = t[0];
                StringBuilder value = new StringBuilder();
                for (int i = 1; i < t.length - 1; i++) {
                    value.append(t[i]).append("=");
                }
                value.append(t[t.length - 1]);
                if (value.length() == 0) {
                    continue;
                }
                d.put(key, value.toString());
            }
            return d;
        } catch (IOException e) {
            Server.getInstance().getLogger().logException(e);
            return null;
        }
    }

    public String translateString(String str) {
        return this.translateString(str, new String[]{}, null);
    }

    public String translateString(String str, String... params) {
        if (params != null) {
            return this.translateString(str, params, null);
        }
        return this.translateString(str, new String[0], null);
    }

    public String translateString(String str, Object... params) {
        if (params != null) {
            String[] paramsToString = new String[params.length];
            for (int i = 0; i < params.length; i++) {
                paramsToString[i] = Objects.toString(params[i]);
            }
            return this.translateString(str, paramsToString, null);
        }
        return this.translateString(str, new String[0], null);
    }

    public String translateString(String str, String param, String onlyPrefix) {
        return this.translateString(str, new String[]{param}, onlyPrefix);
    }

    public String translateString(String str, String[] params, String onlyPrefix) {
        String baseText = this.get(str);
        baseText = this.parseTranslation((baseText != null && (onlyPrefix == null || str.indexOf(onlyPrefix) == 0)) ? baseText : str, onlyPrefix);
        for (int i = 0; i < params.length; i++) {
            baseText = baseText.replace("{%" + i + "}", this.parseTranslation(String.valueOf(params[i])));
        }

        return baseText;
    }

    public String translate(TextContainer c) {
        String baseText = this.parseTranslation(c.getText());
        if (c instanceof TranslationContainer) {
            baseText = this.internalGet(c.getText());
            baseText = this.parseTranslation(baseText != null ? baseText : c.getText());
            for (int i = 0; i < ((TranslationContainer) c).getParameters().length; i++) {
                baseText = baseText.replace("{%" + i + "}", this.parseTranslation(((TranslationContainer) c).getParameters()[i]));
            }
        }
        return baseText;
    }

    public String internalGet(String id) {
        String translation = this.lang.get(id);
        if (translation == null) {
            translation = this.fallbackLang.get(id);
        }
        return translation;
    }

    public String get(String id) {
        String translation = this.lang.get(id);
        if (translation == null) {
            translation = this.fallbackLang.get(id);
        }
        return translation == null ? id : translation;
    }

    protected String parseTranslation(String text) {
        return this.parseTranslation(text, null);
    }

    protected String parseTranslation(String text, String onlyPrefix) {
        StringBuilder newString = new StringBuilder();
        text = String.valueOf(text);

        StringBuilder replaceString = null;

        int len = text.length();

        for (int i = 0; i < len; ++i) {
            char c = text.charAt(i);
            if (replaceString != null) {
                if (((int) c >= 0x30 && (int) c <= 0x39) // 0-9
                        || ((int) c >= 0x41 && (int) c <= 0x5a) // A-Z
                        || ((int) c >= 0x61 && (int) c <= 0x7a) || // a-z
                        c == '.' || c == '-') {
                    replaceString.append(c);
                } else {
                    String t = this.internalGet(replaceString.substring(1));
                    if (t != null && (onlyPrefix == null || replaceString.indexOf(onlyPrefix) == 1)) {
                        newString.append(t);
                    } else {
                        newString.append(replaceString);
                    }
                    replaceString = null;
                    if (c == '%') {
                        replaceString = new StringBuilder(String.valueOf(c));
                    } else {
                        newString.append(c);
                    }
                }
            } else if (c == '%') {
                replaceString = new StringBuilder(String.valueOf(c));
            } else {
                newString.append(c);
            }
        }

        if (replaceString != null) {
            String t = this.internalGet(replaceString.substring(1));
            if (t != null && (onlyPrefix == null || replaceString.indexOf(onlyPrefix) == 1)) {
                newString.append(t);
            } else {
                newString.append(replaceString);
            }
        }
        return newString.toString();
    }
}
