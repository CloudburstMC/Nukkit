package cn.nukkit.plugin.simple;

import cn.nukkit.permission.Permission;

/**
 * The children permission
 * @author magiclu # DreamServer
 */
public @interface Children {

    String name();

    String description() default "";

    String theDefault() default Permission.DEFAULT_PERMISSION;
}
