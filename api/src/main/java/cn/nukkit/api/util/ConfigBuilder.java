package cn.nukkit.api.util;

import java.io.File;
import java.util.LinkedHashMap;

public interface ConfigBuilder {

    ConfigBuilder type(Config.Type type);

    ConfigBuilder file(String file);

    ConfigBuilder file(File file);

    ConfigBuilder defaultMap(LinkedHashMap<String, Object> defaultMap);

    ConfigBuilder defaultMap(ConfigSection defaultMap);

    Config build();
}
