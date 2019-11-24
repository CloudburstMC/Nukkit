package cn.nukkit.plugin.simple;

import cn.nukkit.event.Listener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Listener auto-registration for launching SimplePlugin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface EnableRegister {

    Class<? extends Listener>[] noRegister() default {};
}
