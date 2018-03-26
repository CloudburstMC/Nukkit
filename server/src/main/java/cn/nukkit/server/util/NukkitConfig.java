/*package cn.nukkit.server.util;

import cn.nukkit.api.util.Config;
import cn.nukkit.api.util.ConfigSection;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.scheduler.FileWriteTask;
import lombok.extern.log4j.Log4j2;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * author: MagicDroidX
 * Nukkit
 *//*
@Log4j2
public class NukkitConfig implements Config {

    public static final int DETECT = -1; //Detect by file extension
    public static final int PROPERTIES = 0; // .properties
    public static final int CNF = Config.PROPERTIES; // .cnf
    public static final int JSON = 1; // .js, .json
    public static final int YAML = 2; // .yml, .yaml
    //public static final int EXPORT = 3; // .export, .xport
    //public static final int SERIALIZED = 4; // .sl
    public static final int ENUM = 5; // .txt, .list, .enum
    public static final int ENUMERATION = Config.ENUM;

    public static final Map<String, Config.Type> format = new TreeMap<>();

    static {
        format.put("properties", Config.Type.PROPERTIES);
        format.put("con", Config.Type.PROPERTIES);
        format.put("conf", Config.Type.PROPERTIES);
        format.put("config", Config.Type.PROPERTIES);
        format.put("js", Config.Type.JSON);
        format.put("json", Config.Type.JSON);
        format.put("yml", Config.Type.YAML);
        format.put("yaml", Config.Type.YAML);
        format.put("txt", Config.Type.ENUM);
        format.put("list", Config.Type.ENUM);
        format.put("enum", Config.Type.ENUM);
    }

    private final Map<String, Object> nestedCache = new HashMap<>();
    private File file;
    private boolean correct = false;
    //private LinkedHashMap<String, Object> config = new LinkedHashMap<>();
    private NukkitConfigSection config = new NukkitConfigSection();
    private Config.Type type = Config.Type.DETECT;

    /**
     * Constructor for Config instance with undefined file object
     *
     * @param type - Config type
     *//*
    public NukkitConfig(Config.Type type) {
        this.type = type;
        this.correct = true;
        this.config = new NukkitConfigSection();
    }

    /**
     * Constructor for Config (YAML) instance with undefined file object
     *//*
    public NukkitConfig() {
        this(Config.Type.YAML);
    }

    public NukkitConfig(String file) {
        this(file, Config.Type.DETECT);
    }

    public NukkitConfig(File file) {
        this(file.toString(), Config.Type.DETECT);
    }

    public NukkitConfig(String file, Config.Type type) {
        this(file, type, (ConfigSection) new NukkitConfigSection());
    }

    public NukkitConfig(File file, Config.Type type) {
        this(file.toString(), type, (ConfigSection) new NukkitConfigSection());
    }

    @Deprecated
    public NukkitConfig(String file, Config.Type type, LinkedHashMap<String, Object> defaultMap) {
        this.load(file, type, new NukkitConfigSection(defaultMap));
    }

    public NukkitConfig(String file, Config.Type type, ConfigSection defaultMap) {
        this.load(file, type, defaultMap);
    }

    public NukkitConfig(File file, Config.Type type, ConfigSection defaultMap) {
        this.load(file.toString(), type, defaultMap);
    }

    @Deprecated
    public NukkitConfig(File file, Config.Type type, LinkedHashMap<String, Object> defaultMap) {
        this(file.toString(), type, (ConfigSection) new NukkitConfigSection(defaultMap));
    }

    @Override
    public void reload() {
        this.config.clear();
        this.nestedCache.clear();
        this.correct = false;
        //this.load(this.file.toString());
        if (this.file == null) throw new IllegalStateException("Failed to reload Config. File object is undefined.");
        this.load(this.file.toString(), this.type);

    }

    public boolean load(String file) {
        return this.load(file, Config.Type.DETECT);
    }

    public boolean load(String file, Config.Type type) {
        return this.load(file, type, new NukkitConfigSection());
    }

    public boolean load(String file, Config.Type type, ConfigSection defaultMap) {
        this.correct = true;
        this.type = type;
        this.file = new File(file);
        if (!this.file.exists()) {
            try {
                this.file.getParentFile().mkdirs();
                this.file.createNewFile();
            } catch (IOException e) {
                log.error("Could not create Config " + this.file.toString(), e);
            }
            this.config = (NukkitConfigSection)defaultMap;
            this.save();
        } else {
            if (this.type == Config.Type.DETECT) {
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
                    log.throwing(e);
                }
                this.parseContent(content);
                if (!this.correct) return false;
                if (this.setDefault(defaultMap) > 0) {
                    this.save();
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean load(InputStream inputStream) {
        if (inputStream == null) return false;
        if (this.correct) {
            String content;
            try {
                content = Utils.readFile(inputStream);
            } catch (IOException e) {
                log.throwing(e);
                return false;
            }
            this.parseContent(content);
        }
        return correct;
    }

    public boolean check() {
        return this.correct;
    }

    public boolean isCorrect() {
        return correct;
    }

    /**
     * Save configuration into provided file. Internal file object will be set to new file.
     *
     * @param file
     * @param async
     * @return
     *//*
    public boolean save(File file, boolean async) {
        this.file = file;
        return save(async);
    }

    public boolean save(File file) {
        this.file = file;
        return save();
    }

    public boolean save() {
        return this.save(false);
    }

    public boolean save(boolean async) {
        if (this.file == null) throw new IllegalStateException("Failed to save Config. File object is undefined.");
        if (this.correct) {
            String content = "";
            switch (this.type) {
                case CNF:
                case PROPERTIES:
                    content = this.writeProperties();
                    break;
                case JSON:
                    content = new GsonBuilder().setPrettyPrinting().create().toJson(this.config);
                    break;
                case YAML:
                    DumperOptions dumperOptions = new DumperOptions();
                    dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                    Yaml yaml = new Yaml(dumperOptions);
                    content = yaml.dump(this.config);
                    break;
                case ENUMERATION:
                case ENUM:
                    for (Object o : this.config.entrySet()) {
                        Map.Entry entry = (Map.Entry) o;
                        content += String.valueOf(entry.getKey()) + "\r\n";
                    }
                    break;
            }
            if (async) {
                NukkitServer.getInstance().getScheduler().scheduleAsyncTask(new FileWriteTask(this.file, content));

            } else {
                try {
                    Utils.writeFile(this.file, content);
                } catch (IOException e) {
                    log.throwing(e);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public void set(final String key, Object value) {
        this.config.set(key, value);
    }

    public Object get(String key) {
        return this.get(key, null);
    }

    public <T> T get(String key, T defaultValue) {
        return this.correct ? this.config.get(key, defaultValue) : defaultValue;
    }

    public NukkitConfigSection getSection(String key) {
        return this.correct ? this.config.getSection(key) : new NukkitConfigSection();
    }

    public boolean isSection(String key) {
        return config.isSection(key);
    }

    public NukkitConfigSection getSections(String key) {
        return this.correct ? this.config.getSections(key) : new NukkitConfigSection();
    }

    public NukkitConfigSection getSections() {
        return this.correct ? this.config.getSections() : new NukkitConfigSection();
    }

    public int getInt(String key) {
        return this.getInt(key, 0);
    }

    public int getInt(String key, int defaultValue) {
        return this.correct ? this.config.getInt(key, defaultValue) : defaultValue;
    }

    public boolean isInt(String key) {
        return config.isInt(key);
    }

    public long getLong(String key) {
        return this.getLong(key, 0);
    }

    public long getLong(String key, long defaultValue) {
        return this.correct ? this.config.getLong(key, defaultValue) : defaultValue;
    }

    public boolean isLong(String key) {
        return config.isLong(key);
    }

    public double getDouble(String key) {
        return this.getDouble(key, 0);
    }

    public double getDouble(String key, double defaultValue) {
        return this.correct ? this.config.getDouble(key, defaultValue) : defaultValue;
    }

    public boolean isDouble(String key) {
        return config.isDouble(key);
    }

    public String getString(String key) {
        return this.getString(key, "");
    }

    public String getString(String key, String defaultValue) {
        return this.correct ? this.config.getString(key, defaultValue) : defaultValue;
    }

    public boolean isString(String key) {
        return config.isString(key);
    }

    public boolean getBoolean(String key) {
        return this.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return this.correct ? this.config.getBoolean(key, defaultValue) : defaultValue;
    }

    public boolean isBoolean(String key) {
        return config.isBoolean(key);
    }

    public List getList(String key) {
        return this.getList(key, null);
    }

    public List getList(String key, List defaultList) {
        return this.correct ? this.config.getList(key, defaultList) : defaultList;
    }

    public boolean isList(String key) {
        return config.isList(key);
    }

    public List<String> getStringList(String key) {
        return config.getStringList(key);
    }

    public List<Integer> getIntegerList(String key) {
        return config.getIntegerList(key);
    }

    public List<Boolean> getBooleanList(String key) {
        return config.getBooleanList(key);
    }

    public List<Double> getDoubleList(String key) {
        return config.getDoubleList(key);
    }

    public List<Float> getFloatList(String key) {
        return config.getFloatList(key);
    }

    public List<Long> getLongList(String key) {
        return config.getLongList(key);
    }

    public List<Byte> getByteList(String key) {
        return config.getByteList(key);
    }

    public List<Character> getCharacterList(String key) {
        return config.getCharacterList(key);
    }

    public List<Short> getShortList(String key) {
        return config.getShortList(key);
    }

    public List<Map> getMapList(String key) {
        return config.getMapList(key);
    }

    public void setAll(LinkedHashMap<String, Object> map) {
        this.config = new NukkitConfigSection(map);
    }

    public boolean exists(String key) {
        return config.exists(key);
    }

    public boolean exists(String key, boolean ignoreCase) {
        return config.exists(key, ignoreCase);
    }

    public void remove(String key) {
        config.remove(key);
    }

    public Map<String, Object> getAll() {
        return this.config.getAllMap();
    }

    public void setAll(ConfigSection section) {
        this.config = (NukkitConfigSection) section;
    }

    public NukkitConfigSection getRootSection() {
        return config;
    }

    public int setDefault(LinkedHashMap<String, Object> map) {
        return setDefault((ConfigSection) new NukkitConfigSection(map));
    }

    public int setDefault(ConfigSection map) {
        int size = this.config.size();
        this.config = this.fillDefaults((NukkitConfigSection) map, this.config);
        return this.config.size() - size;
    }


    private NukkitConfigSection fillDefaults(NukkitConfigSection defaultMap, NukkitConfigSection data) {
        for (String key : defaultMap.keySet()) {
            if (!data.containsKey(key)) {
                data.put(key, defaultMap.get(key));
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
                    log.debug("[Config] Repeated property " + k + " on file " + this.file.toString());
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

    /**
     * @deprecated use {@link #get(String)} instead
     *//*
    @Deprecated
    public Object getNested(String key) {
        return get(key);
    }

    /**
     * @deprecated use {@link #get(String, T)} instead
     *//*
    @Deprecated
    public <T> T getNested(String key, T defaultValue) {
        return get(key, defaultValue);
    }

    /**
     * @deprecated use {@link #get(String)} instead
     *//*
    @Deprecated
    @SuppressWarnings("unchecked")
    public <T> T getNestedAs(String key, Class<T> type) {
        return (T) get(key);
    }

    /**
     * @deprecated use {@link #remove(String)} instead
     *//*
    @Deprecated
    public void removeNested(String key) {
        remove(key);
    }

    private void parseContent(String content) {
        switch (this.type) {
            case CNF:
            case PROPERTIES:
                this.parseProperties(content);
                break;
            case JSON:
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                this.config = new NukkitConfigSection(gson.fromJson(content, new TypeToken<LinkedHashMap<String, Object>>() {
                }.getType()));
                break;
            case YAML:
                DumperOptions dumperOptions = new DumperOptions();
                dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                Yaml yaml = new Yaml(dumperOptions);
                this.config = new NukkitConfigSection(yaml.loadAs(content, LinkedHashMap.class));
                if (this.config == null) {
                    this.config = new NukkitConfigSection();
                }
                break;
            case ENUMERATION:
            case ENUM:
                this.parseList(content);
                break;
            default:
                this.correct = false;
        }
    }

    public Set<String> getKeys() {
        if (this.correct) return config.getKeys();
        return new HashSet<>();
    }

    public Set<String> getKeys(boolean child) {
        if (this.correct) return config.getKeys(child);
        return new HashSet<>();
    }
}*/
