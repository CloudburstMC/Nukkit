package cn.nukkit.permission;

import java.util.ArrayList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Permission {

    public final static String DEFAULT_OP = "op";
    public final static String DEFAULT_NOT_OP = "notop";
    public final static String DEFAULT_TRUE = "true";
    public final static String DEFAULT_FALSE = "false";

    public static String DEFAULT_PERMISSION = DEFAULT_OP;

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

    private String name;

    private String description;

    private ArrayList<String> children = new ArrayList<>();

    private String defaultValue;

    public Permission(String name) {
        this(name, null, null, null);
    }

    public Permission(String name, String description) {
        this(name, description, null, null);
    }

    public Permission(String name, String description, String defualtValue) {
        this(name, description, defualtValue, null);
    }

    public Permission(String name, String description, String defualtValue, ArrayList<String> children) {
        this.name = name;
        this.description = description != null ? description : "";
        this.defaultValue = defualtValue != null ? defualtValue : DEFAULT_PERMISSION;
        this.children = children;

        this.recalculatePermissibles();
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getChildren() {
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

    //todo getPermissibles

    public void recalculatePermissibles() {
        //todo
    }

    //todo alot
}
