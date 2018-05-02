package com.nukkitx.api.permission;

import java.util.Map;

public interface Permission {

    String DEFAULT_OP = "op";
    String DEFAULT_NOT_OP = "notop";
    String DEFAULT_TRUE = "true";
    String DEFAULT_FALSE = "false";
    String DEFAULT_PERMISSION = DEFAULT_OP;

    String getName();

    Map<String, Boolean> getChildren();

    void recalculatePermissibles();

    void addParent(Permission nukkitPermission, boolean value);

    Permission addParent(String name, boolean value);

    String getDefault();
}
