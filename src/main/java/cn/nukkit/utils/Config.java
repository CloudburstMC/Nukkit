package cn.nukkit.utils;

import cn.nukkit.Server;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit
 */
public class Config {
    //todo: JSON支持

    public static final int DETECT = -1; //Detect by file extension
    public static final int PROPERTIES = 0; // .properties
    public static final int CNF = Config.PROPERTIES; // .cnf
    //public static final int JSON = 1; // .js, .json
    public static final int YAML = 2; // .yml, .yaml
    //public static final int EXPORT = 3; // .export, .xport
    //public static final int SERIALIZED = 4; // .sl
    public static final int ENUM = 5; // .txt, .list, .enum
    public static final int ENUMERATION = Config.ENUM;

    private HashMap<String, Object> config = new HashMap<String, Object>();
    private File file;
    private boolean correct = false;
    private int type = Config.DETECT;

    public static HashMap<String, Integer> format = new HashMap<String, Integer>();

    static {
        format.put("properties", Config.PROPERTIES);
        format.put("con", Config.PROPERTIES);
        format.put("conf", Config.PROPERTIES);
        format.put("config", Config.PROPERTIES);
        //format.put("js", Config.JSON);
        //format.put("json", Config.JSON);
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
        this(file, type, new HashMap<String, Object>());
    }

    public Config(String file, int type, HashMap<String, Object> default_map) {
        this.load(file, type, default_map);
    }

    public void reload() {
        this.config = new HashMap<String, Object>();
        this.correct = false;
        this.load(this.file.toString());
        this.load(this.file.toString(), this.type);
    }

    public boolean load(String file) {
        return this.load(file, Config.DETECT);
    }

    public boolean load(String file, int type) {
        return this.load(file, type, new HashMap<String, Object>());
    }

    public boolean load(String file, int type, HashMap<String, Object> default_map) {
        this.correct = true;
        this.type = type;
        this.file = new File(file);
        if (!this.file.exists()) {
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
                    FileInputStream fileInputStream = new FileInputStream(this.file);
                    byte[] buffer = new byte[fileInputStream.available()];
                    fileInputStream.read(buffer);
                    fileInputStream.close();
                    content = new String(buffer);
                } catch (IOException e) {
                    Server.getInstance().getLogger().logException(e);
                }
                switch (this.type) {
                    case Config.PROPERTIES:
                        this.parseProperties(content);
                        break;
                    //todo: case Config.JSON:
                    case Config.YAML:
                        Yaml yaml = new Yaml();
                        this.config = (HashMap<String, Object>) yaml.load(content);
                        break;
                    // case Config.SERIALIZED 也许是不必要的？233
                    case Config.ENUM:
                        break;
                    default:
                        this.correct = false;
                        return false;
                }
                if (this.fillDefaults(default_map, this.config) > 0) {
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
        //todo: 异步储存文件
        if (this.correct) {
            String content;
            switch (this.type) {
                case Config.PROPERTIES:
                    //todo 0526最后coding位置
            }
            return true;
        } else {
            return false;
        }
    }

    private int fillDefaults(HashMap<String, Object> default_map, HashMap<String, Object> data) {
        int changed = 0;
        Iterator iterator = default_map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (!data.containsKey(entry.getKey())) {
                data.put(String.valueOf(entry.getKey()), entry.getValue());
                changed++;
            }
        }
        return changed;
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
        String content = "";
        Iterator iterator = this.config.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
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

    }
}
