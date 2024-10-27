package cn.nukkit.plugin.simple;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Command auto-registration for launching SimplePlugin
 * @author magiclu # DreamServer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface Command {

    String name();

    String usage() default "";

    String description() default "";

    String permission();

    String[] aliases() default {};

    String permissionMessage() default "";
}
