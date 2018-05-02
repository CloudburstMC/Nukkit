package com.nukkitx.api.plugin;

import com.google.common.collect.ImmutableMap;

public interface PermissionDescription {

    String getDescription();

    String getDefault();

    ImmutableMap<String, PermissionDescription> getChildPermissions();
}
