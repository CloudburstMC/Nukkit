package cn.nukkit.api.permission;

import java.util.Map;

public interface Permission {

    String getByName(String value);

    String getName();

    Map<String, Boolean> getChildren();

    void recalculatePermissibles();

    void addParent(Permission nukkitPermission, boolean value);

    Permission addParent(String name, boolean value);
}
