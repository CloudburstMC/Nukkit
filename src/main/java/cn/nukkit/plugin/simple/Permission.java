package cn.nukkit.plugin.simple;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * used with Command.
 * @author magiclu # DreamServer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface Permission {

    String permission();

    String description() default "";

    String theDefault() default cn.nukkit.permission.Permission.DEFAULT_PERMISSION;

    Children[] childeren() default {};
}
