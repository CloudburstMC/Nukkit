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
import java.io.UnsupportedEncodingException;
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

    private Map<String, Object> config = new LinkedHashMap<>();
    private TreeMap<String, Object> nestedCache = new TreeMap<>();
    private File file;
    private boolean correct = false;
    private int type = Config.DETECT;

    public static TreeMap<String, Integer> format = new TreeMap<>();

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

    public Config(String file, int type) {
        this(file, type, new TreeMap<>());
    }

    public Config(String file, int type, Map<String, Object> default_map) {
        this.load(file, type, default_map);
    }

    public void reload() {
        this.config.clear();
        this.nestedCache.clear();
        this.correct = false;
        this.load(this.file.toString());
        this.load(this.file.toString(), this.type);
    }

    public boolean load(String file) {
        return this.load(file, Config.DETECT);
    }

    public boolean load(String file, int type) {
        return this.load(file, type, new LinkedHashMap<>());
    }

    public boolean load(String file, int type, Map<String, Object> default_map) {
        this.correct = true;
        this.type = type;
        this.file = new File(file);
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                MainLogger.getLogger().error("无法创建配置文件 " + this.file.toString());
            }
            this.config = default_map;
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
                        this.config = gson.fromJson(content, new TypeToken<TreeMap<String, Object>>() {
                        }.getType());
                        break;
                    case Config.YAML:
                        DumperOptions dumperOptions = new DumperOptions();
                        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                        Yaml yaml = new Yaml(dumperOptions);
                        this.config = yaml.loadAs(content, LinkedHashMap.class);
                        break;
                    // case Config.SERIALIZED 也许是不必要的？233
                    case Config.ENUM:
                        this.parseList(content);
                        break;
                    default:
                        this.correct = false;
                        return false;
                }
                if (this.setDefault(default_map) > 0) {
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
                try {
                    Server.getInstance().getScheduler().scheduleAsyncTask(new FileWriteTask(this.file, content));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
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

    public Object __get(String k) {
        return this.get(k);
    }

    public void __set(String k, Object v) {
        this.set(k, v);
    }

    public boolean __exists(String k) {
        return this.exists(k);
    }

    public void __remove(String k) {
        this.remove(k);
    }

    public Object get(String k) {
        return this.get(k, true);
    }

    public Object get(String k, Object default_value) {
        return (this.correct && this.config.containsKey(k)) ? this.config.get(k) : default_value;
    }

    public void setNested(final String key, final Object value) {
        final String[] vars = key.split("\\.");
        if (vars.length < 2) {
            this.set(key, value);
            return;
        }
        Map<String, Object> hashMap = new LinkedHashMap<String, Object>() {
            {
                put(vars[vars.length - 1], value);
            }
        }; //内嵌中心元素
        for (int i = vars.length - 2; i > 0; i--) {
            HashMap<String, Object> new_hashMap = new LinkedHashMap<>();
            new_hashMap.put(vars[i], hashMap);
            hashMap = new_hashMap;
        }
        this.config.put(vars[0], hashMap);
        this.config.put(key, value);
    }

    public Object getNested(String key) {
        return this.getNested(key, null);
    }

    public Object getNested(String key, Object default_value) {
        if (this.nestedCache.containsKey(key)) {
            return this.nestedCache.get(key);
        }
        String[] vars = key.split("\\.");
        if (vars.length < 2) {
            return this.get(key, default_value);
        }

        if (!this.config.containsKey(vars[0])) {
            return default_value;
        } else {
            Map<String, Object> map = (Map<String, Object>) this.config.get(vars[0]);
            for (int i = 1; i < vars.length - 1; i++) {
                if (map.containsKey(vars[i])) {
                    map = (Map<String, Object>) map.get(vars[i]);
                } else {
                    return default_value;
                }
            }
            if (map.containsKey(vars[vars.length - 1])) {
                Object value = map.get(vars[vars.length - 1]);
                this.nestedCache.put(key, value);
                return value;
            } else {
                return default_value;
            }
        }
    }

    public void set(String k) {
        this.set(k, false);
    }

    public void set(String k, Object v) {
        this.config.put(k, v);
    }

    public void setAll(Map<String, Object> map) {
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
        return this.config;
    }

    public int setDefault(Map<String, Object> map) {
        int size = this.config.size();
        this.config = this.fillDefaults(map, this.config);
        return this.config.size() - size;
    }


    private Map<String, Object> fillDefaults(Map<String, Object> default_map, Map<String, Object> data) {
        default_map.entrySet().stream().filter(entry -> !data.containsKey(entry.getKey())).forEach(entry -> {
            data.put(entry.getKey(), entry.getValue());
        });
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
                    MainLogger.getLogger().debug("[Config] 重复的键值 " + k + " ，位于文件 " + this.file.toString());
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
