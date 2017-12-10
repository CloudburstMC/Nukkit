package cn.nukkit.api.resourcepack;

import com.google.common.collect.ImmutableList;

public interface ResourcePackManifest {

    String getFormatVersion();

    Header getHeader();

    String getModules();

    interface Header {

        String getDescription();

        String getName();

        String getUuid();

        ImmutableList<String> getVersion();
    }
}
