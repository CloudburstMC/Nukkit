package cn.nukkit.server.resourcepacks;

import cn.nukkit.api.resourcepack.ResourcePackManifest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@EqualsAndHashCode
public class NukkitResourcePackPackManifest implements ResourcePackManifest {
    @JsonProperty("format_version")
    private String formatVersion;
    private Header header;
    private String modules;

    public boolean verify() {
        return formatVersion != null && header != null && modules != null && header.description != null &&
                header.name != null && header.uuid != null && header.version != null && header.version.size() == 3;
    }

    @ToString
    public static class Header implements ResourcePackManifest.Header {
        @Getter
        private String description;
        @Getter
        private String name;
        @Getter
        private String uuid;
        private List<String> version;

        public ImmutableList<String> getVersion() {
            return ImmutableList.copyOf(version);
        }
    }
}
