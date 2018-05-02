package com.nukkitx.api.resourcepack;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nukkitx.api.util.SemVer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Getter
@EqualsAndHashCode
public class MinecraftPackManifest {
    @JsonProperty("format_version")
    private Integer formatVersion;
    private Header header;
    private Collection<Module> modules;
    protected Collection<Dependency> dependencies;

    public Collection<Module> getModules() {
        return Collections.unmodifiableCollection(modules);
    }

    @Getter
    @ToString
    public static class Header {
        private String description;
        private String name;
        private UUID uuid;
        private SemVer version;
        @JsonProperty("min_engine_version")
        private SemVer minimumSupportedMinecraftVersion;
    }

    @Getter
    @ToString
    public static class Module {
        private String description;
        private String name;
        private UUID uuid;
        private SemVer version;
    }

    @Getter
    @ToString
    public static class Dependency {
        private UUID uuid;
        private SemVer version;
    }
}
