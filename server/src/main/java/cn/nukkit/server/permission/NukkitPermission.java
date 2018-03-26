package cn.nukkit.server.permission;

import cn.nukkit.api.permission.Permissible;
import cn.nukkit.api.permission.Permission;
import cn.nukkit.server.NukkitServer;

import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class NukkitPermission implements Permission {

    public static String getByName(String value) {
        switch (value.toLowerCase()) {
            case "op":
            case "isop":
            case "operator":
            case "isoperator":
            case "admin":
            case "isadmin":
                return DEFAULT_OP;

            case "!op":
            case "notop":
            case "!operator":
            case "notoperator":
            case "!admin":
            case "notadmin":
                return DEFAULT_NOT_OP;

            case "true":
                return DEFAULT_TRUE;

            default:
                return DEFAULT_FALSE;
        }
    }

    private final String name;

    private String description;

    private Map<String, Boolean> children = new HashMap<>();

    private String defaultValue;

    public NukkitPermission(String name) {
        this(name, null, null, new HashMap<>());
    }

    public NukkitPermission(String name, String description) {
        this(name, description, null, new HashMap<>());
    }

    public NukkitPermission(String name, String description, String defualtValue) {
        this(name, description, defualtValue, new HashMap<>());
    }

    public NukkitPermission(String name, String description, String defualtValue, Map<String, Boolean> children) {
        this.name = name;
        this.description = description != null ? description : "";
        this.defaultValue = defualtValue != null ? defualtValue : DEFAULT_PERMISSION;
        this.children = children;

        this.recalculatePermissibles();
    }

    public String getName() {
        return name;
    }

    public Map<String, Boolean> getChildren() {
        return children;
    }

    public String getDefault() {
        return defaultValue;
    }

    public void setDefault(String value) {
        if (!value.equals(this.defaultValue)) {
            this.defaultValue = value;
            this.recalculatePermissibles();
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Permissible> getPermissibles() {
        return NukkitServer.getInstance().getPermissionManager().getPermissionSubscriptions(this.name);
    }

    public void recalculatePermissibles() {
        Set<Permissible> perms = this.getPermissibles();

        NukkitServer.getInstance().getPermissionManager().recalculatePermissionDefaults(this);

        for (Permissible p : perms) {
            p.recalculatePermissions();
        }
    }

    public static List<NukkitPermission> loadPermissions(Map<String, Object> data) {
        return loadPermissions(data, DEFAULT_OP);
    }

    public static List<NukkitPermission> loadPermissions(Map<String, Object> data, String defaultValue) {
        List<NukkitPermission> result = new ArrayList<>();
        if (data != null) {
            for (Map.Entry e : data.entrySet()) {
                String key = (String) e.getKey();
                Map<String, Object> entry = (Map<String, Object>) e.getValue();
                result.add(loadPermission(key, entry, defaultValue, result));
            }
        }
        return result;
    }

    public static NukkitPermission loadPermission(String name, Map<String, Object> data) {
        return loadPermission(name, data, DEFAULT_OP, new ArrayList<>());
    }

    public static NukkitPermission loadPermission(String name, Map<String, Object> data, String defaultValue) {
        return loadPermission(name, data, defaultValue, new ArrayList<>());
    }

    public static NukkitPermission loadPermission(String name, Map<String, Object> data, String defaultValue, List<NukkitPermission> output) {
        String desc = null;
        Map<String, Boolean> children = new HashMap<>();
        if (data.containsKey("default")) {
            String value = NukkitPermission.getByName(String.valueOf(data.get("default")));
            if (value != null) {
                defaultValue = value;
            } else {
                throw new IllegalStateException("'default' key contained unknown value");
            }
        }

        if (data.containsKey("children")) {
            if (data.get("children") instanceof Map) {
                for (Map.Entry entry : ((Map<String, Object>) data.get("children")).entrySet()) {
                    String k = (String) entry.getKey();
                    Object v = entry.getValue();
                    if (v instanceof Map) {
                        NukkitPermission nukkitPermission = loadPermission(k, (Map<String, Object>) v, defaultValue, output);
                        if (nukkitPermission != null) {
                            output.add(nukkitPermission);
                        }
                    }
                    children.put(k, true);
                }
            } else {
                throw new IllegalStateException("'children' key is of wrong type");
            }
        }

        if (data.containsKey("description")) {
            desc = (String) data.get("description");
        }

        return new NukkitPermission(name, desc, defaultValue, children);
    }

    @Override
    public void addParent(Permission permission, boolean value) {
        this.getChildren().put(this.getName(), value);
        permission.recalculatePermissibles();
    }

    public Permission addParent(String name, boolean value) {
        Optional<Permission> perm = NukkitServer.getInstance().getPermissionManager().getPermission(name);
        Permission parentPerm;
        if (!perm.isPresent()) {
            NukkitServer.getInstance().getPermissionManager().addPermission((parentPerm = new NukkitPermission(name)));
        } else {
            parentPerm = perm.get();
        }

        this.addParent(parentPerm, value);

        return parentPerm;
    }

}
