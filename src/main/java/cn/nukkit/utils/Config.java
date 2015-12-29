package cn.nukkit.utils;

import cn.nukkit.Server;
import cn.nukkit.scheduler.FileWriteTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * author: MagicDroidX
 * Nukkit
 */
public class Config {

    public static final int DETECT = -1; //Detect by file extension
    public static final int PROPERTIES = 0; // .properties
    public static final int CNF = Config.PROPERTIES; // .cnf
    public static final int JSON = 1; // .js, .json
    public static final int YAML = 2; // .yml, .yaml
    //public static final int EXPORT = 3; // .export, .xport
    //public static final int SERIALIZED = 4; // .sl
    public static final int ENUM = 5; // .txt, .list, .enum
    public static final int ENUMERATION = Config.ENUM;

    private LinkedHashMap<String, Object> config = new LinkedHashMap<>();
    private Map<String, Object> nestedCache = new HashMap<>();
    private File file;
    private boolean correct = false;
    private int type = Config.DETECT;

    public static Map<String, Integer> format = new TreeMap<>();

    static {
        format.put("properties", Config.PROPERTIES);
        format.put("con", Config.PROPERTIES);
        format.put("conf", Config.PROPERTIES);
        format.put("config", Config.PROPERTIES);
        format.put("js", Config.JSON);
        format.put("json", Config.JSON);
        format.put("yml", Config.YAML);
        format.put("yaml", Config.YAML);
        //format.put("sl", Config.SERIALIZED);
        //format.put("serialize", Config.SERIALIZED);
        format.put("txt", Config.ENUM);
        format.put("list", Config.ENUM);
        format.put("enum", Config.ENUM);
    }

    public Config(String file) {
        this(file, Config.DETECT);
    }

    public Config(File file) {
        this(file.toString(), Config.DETECT);
    }

    public Config(String file, int type) {
        this(file, type, new LinkedHashMap<>());
    }

    public Config(File file, int type) {
        this(file.toString(), type, new LinkedHashMap<>());
    }

    public Config(String file, int type, LinkedHashMap<String, Object> defaultMap) {
        this.load(file, type, defaultMap);
    }

    public Config(File file, int type, LinkedHashMap<String, Object> defaultMap) {
        this(file.toString(), type, defaultMap);
    }

    public void reload() {
        this.config.clear();
        this.nestedCache.clear();
        this.correct = false;
        //this.load(this.file.toString());
        this.load(this.file.toString(), this.type);
    }

    public boolean load(String file) {
        return this.load(file, Config.DETECT);
    }

    public boolean load(String file, int type) {
        return this.load(file, type, new LinkedHashMap<>());
    }

    @SuppressWarnings("unchecked")
    public boolean load(String file, int type, LinkedHashMap<String, Object> defaultMap) {
        this.correct = true;
        this.type = type;
        this.file = new File(file);
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                MainLogger.getLogger().error("Could not create Config " + this.file.toString() + ": " + e.getMessage());
            }
            this.config = defaultMap;
            this.save();
        } else {
            if (this.type == Config.DETECT) {
                String extension = "";
                if (this.file.getName().lastIndexOf(".") != -1 && this.file.getName().lastIndexOf(".") != 0) {
                    extension = this.file.getName().substring(this.file.getName().lastIndexOf(".") + 1);
                }
                if (format.containsKey(extension)) {
                    this.type = format.get(extension);
                } else {
                    this.correct = false;
                }
            }
            if (this.correct) {
                String content = "";
                try {
                    content = Utils.readFile(this.file);
                } catch (IOException e) {
                    Server.getInstance().getLogger().logException(e);
                }
                switch (this.type) {
                    case Config.PROPERTIES:
                        this.parseProperties(content);
                        break;
                    case Config.JSON:
                        GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();
                        this.config = gson.fromJson(content, new TypeToken<LinkedHashMap<String, Object>>() {
                        }.getType());
                        break;
                    case Config.YAML:
                        DumperOptions dumperOptions = new DumperOptions();
                        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                        Yaml yaml = new Yaml(dumperOptions);
                        this.config = yaml.loadAs(content, LinkedHashMap.class);
                        if (this.config == null) {
                            this.config = new LinkedHashMap<>();
                        }
                        break;
                    // case Config.SERIALIZED
                    case Config.ENUM:
                        this.parseList(content);
                        break;
                    default:
                        this.correct = false;
                        return false;
                }
                if (this.setDefault(defaultMap) > 0) {
                    this.save();
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean check() {
        return this.correct;
    }

    public boolean isCorrect() {
        return correct;
    }

    public boolean save() {
        return this.save(false);
    }

    public boolean save(Boolean async) {
        if (this.correct) {
            String content = "";
            switch (this.type) {
                case Config.PROPERTIES:
                    content = this.writeProperties();
                    break;
                case Config.JSON:
                    content = new GsonBuilder().setPrettyPrinting().create().toJson(this.config);
                    break;
                case Config.YAML:
                    DumperOptions dumperOptions = new DumperOptions();
                    dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                    Yaml yaml = new Yaml(dumperOptions);
                    content = yaml.dump(this.config);
                    break;
                case Config.ENUM:
                    for (Object o : this.config.entrySet()) {
                        Map.Entry entry = (Map.Entry) o;
                        content += String.valueOf(entry.getKey()) + "\r\n";
                    }
                    break;
            }
            if (async) {
                Server.getInstance().getScheduler().scheduleAsyncTask(new FileWriteTask(this.file, content));

            } else {
                try {
                    Utils.writeFile(this.file, content);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public Object get(String k) {
        return this.get(k, true);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String k, T defaultValue) {
        return (this.correct && this.config.containsKey(k)) ? (T) this.config.get(k) : defaultValue;
    }

    @SuppressWarnings("unchecked")
    public <T> T getAs(String k, Class<T> type) {
        return (T) this.get(k);
    }

    @SuppressWarnings("unchecked")
    public void setNested(final String key, final Object value) {
        final String[] vars = key.split("\\.");

        Map<String, Object> map = this.config;

        for (int i = 0; i < vars.length - 1; i++) {
            String k = vars[i];
            if (!map.containsKey(k)) {
                map.put(k, new LinkedHashMap<>());
            }
            map = (Map<String, Object>) map.get(k);
        }

        map.put(vars[vars.length - 1], value);
    }

    public Object getNested(String key) {
        return this.getNested(key, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T getNested(String key, T defaultValue) {
        if (this.nestedCache.containsKey(key)) {
            return (T) this.nestedCache.get(key);
        }
        String[] vars = key.split("\\.");

        Map map = this.config;
        for (int i = 0; i < vars.length - 1; i++) {
            String k = vars[i];
            if (!map.containsKey(k)) {
                return defaultValue;
            }
            map = (Map<String, Object>) map.get(k);
        }

        return (T) map.getOrDefault(vars[vars.length - 1], defaultValue);
    }

    @SuppressWarnings("unchecked")
    public <T> T getNestedAs(String key, Class<T> type) {
        return (T) this.getNested(key);
    }

    public void set(String k) {
        this.set(k, false);
    }

    public void set(String k, Object v) {
        this.config.put(k, v);
    }

    public void setAll(LinkedHashMap<String, Object> map) {
        this.config = map;
    }

    public boolean exists(String k) {
        return this.exists(k, false);
    }

    public boolean exists(String k, boolean lowercase) {
        if (lowercase) {
            k = k.toLowerCase();
            for (Object o : this.config.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                if (entry.getKey().toString().toLowerCase().equals(k)) {
                    return true;
                }
            }
            return false;
        } else {
            return this.config.containsKey(k);
        }
    }

    public void remove(String k) {
        this.config.remove(k);
    }

    public Map<String, Object> getAll() {
        return new LinkedHashMap<>(this.config);
    }

    public int setDefault(LinkedHashMap<String, Object> map) {
        int size = this.config.size();
        this.config = this.fillDefaults(map, this.config);
        return this.config.size() - size;
    }


    private LinkedHashMap<String, Object> fillDefaults(LinkedHashMap<String, Object> default_map, LinkedHashMap<String, Object> data) {
        for (Map.Entry<String, Object> entry : default_map.entrySet()) {
            if (!data.containsKey(entry.getKey())) {
                data.put(entry.getKey(), entry.getValue());
            }
        }
        return data;
    }

    private void parseList(String content) {
        content = content.replace("\r\n", "\n");
        for (String v : content.split("\n")) {
            if (v.trim().isEmpty()) {
                continue;
            }
            config.put(v, true);
        }
    }

    private String writeProperties() {
        String content = "#Properties Config file\r\n#" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) + "\r\n";
        for (Object o : this.config.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            Object v = entry.getValue();
            Object k = entry.getKey();
            if (v instanceof Boolean) {
                v = (Boolean) v ? "on" : "off";
            }
            content += String.valueOf(k) + "=" + String.valueOf(v) + "\r\n";
        }
        return content;
    }

    private void parseProperties(String content) {
        for (String line : content.split("\n")) {
            if (Pattern.compile("[a-zA-Z0-9\\-_\\.]*+=+[^\\r\\n]*").matcher(line).matches()) {
                String[] b = line.split("=", -1);
                String k = b[0];
                String v = b[1].trim();
                String v_lower = v.toLowerCase();
                if (this.config.containsKey(k)) {
                    MainLogger.getLogger().debug("[Config] Repeated property " + k + " on file " + this.file.toString());
                }
                switch (v_lower) {
                    case "on":
                    case "true":
                    case "yes":
                        this.config.put(k, true);
                        break;
                    case "off":
                    case "false":
                    case "no":
                        this.config.put(k, false);
                        break;
                    default:
                        this.config.put(k, v);
                        break;
                }
            }
        }
    }
}
