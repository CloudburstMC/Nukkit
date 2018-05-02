/*package com.nukkitx.server.util;

import com.nukkitx.api.util.Config;
import com.nukkitx.api.util.ConfigBuilder;
import com.nukkitx.api.util.ConfigSection;
import com.google.common.base.Preconditions;

import java.io.File;
import java.util.LinkedHashMap;

public class NukkitConfigBuilder implements ConfigBuilder {
    private Config.Type type;
    private String file;
    private ConfigSection section;

    @Override
    public NukkitConfigBuilder type(Config.Type type) {
        this.type = type;
        return this;
    }

    @Override
    public NukkitConfigBuilder file(String file) {
        this.file = file;
        return this;
    }

    @Override
    public NukkitConfigBuilder file(File file) {
        this.file = file.toString();
        return this;
    }

    @Override
    public NukkitConfigBuilder defaultMap(LinkedHashMap<String, Object> defaultMap) {
        Preconditions.checkArgument( defaultMap != null, "defaultMap cannot be null");
        this.section = new NukkitConfigSection(defaultMap);
        return this;
    }

    @Override
    public NukkitConfigBuilder defaultMap(ConfigSection section) {
        this.section = section;
        return this;
    }

    @Override
    public Config build() {
        Preconditions.checkArgument(type != null || file != null, "At least file or type has to be set");

        if (type == null && section == null) {
            return new NukkitConfig(file);
        }

        if (file != null) {
            if (section != null) {
                return new NukkitConfig(file, type, section);
            }
            return new NukkitConfig(file, type);
        }
        return new NukkitConfig(type);
    }
}*/
