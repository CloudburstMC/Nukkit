package cn.nukkit.lang;

import cn.nukkit.Server;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author MagicDroidX (Nukkit Project)
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
            File file = new File(path);
            if (!file.exists() || file.isDirectory()) {
                throw new FileNotFoundException();
            }
            try (FileInputStream stream = new FileInputStream(file)) {
                return parseLang(new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)));
            }
        } catch (IOException e) {
            Server.getInstance().getLogger().logException(e);
            return null;
        }
    }

    protected Map<String, String> loadLang(InputStream stream) {
        try {
            return parseLang(new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)));
        } catch (IOException e) {
            Server.getInstance().getLogger().logException(e);
            return null;
        }
    }
    
    private Map<String, String> parseLang(BufferedReader reader) throws IOException {
        Map<String, String> d = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.charAt(0) == '#') {
                continue;
            }
            String[] t = line.split("=", 2);
            if (t.length < 2) {
                continue;
            }
            String key = t[0];
            String value = t[1];
            if (value.length() > 1 && value.charAt(0) == '"' && value.charAt(value.length()-1) == '"') {
                value = value.substring(1, value.length() - 1).replace("\\\"", "\"").replace("\\\\", "\\");
            }
            if (value.isEmpty()) {
                continue;
            }
            d.put(key, value);
        }
        return d;
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
        if (this.lang.containsKey(id)) {
            return this.lang.get(id);
        } else if (this.fallbackLang.containsKey(id)) {
            return this.fallbackLang.get(id);
        }
        return null;
    }

    public String get(String id) {
        if (this.lang.containsKey(id)) {
            return this.lang.get(id);
        } else if (this.fallbackLang.containsKey(id)) {
            return this.fallbackLang.get(id);
        }
        return id;
    }

    protected String parseTranslation(String text) {
        return this.parseTranslation(text, null);
    }

    protected String parseTranslation(String text, String onlyPrefix) {
        StringBuilder newString = new StringBuilder();
        text = String.valueOf(text);

        String replaceString = null;

        int len = text.length();

        for (int i = 0; i < len; ++i) {
            char c = text.charAt(i);
            if (replaceString != null) {
                int ord = c;
                if ((ord >= 0x30 && ord <= 0x39) // 0-9
                        || (ord >= 0x41 && ord <= 0x5a) // A-Z
                        || (ord >= 0x61 && ord <= 0x7a) || // a-z
                        c == '.' || c == '-') {
                    replaceString += String.valueOf(c);
                } else {
                    String t = this.internalGet(replaceString.substring(1));
                    if (t != null && (onlyPrefix == null || replaceString.indexOf(onlyPrefix) == 1)) {
                        newString.append(t);
                    } else {
                        newString.append(replaceString);
                    }
                    replaceString = null;
                    if (c == '%') {
                        replaceString = String.valueOf(c);
                    } else {
                        newString.append(c);
                    }
                }
            } else if (c == '%') {
                replaceString = String.valueOf(c);
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
