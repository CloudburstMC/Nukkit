package cn.nukkit.plugin.simple;

import cn.nukkit.command.Command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author magiclu # DreamServer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface EnableCommand {

    Class<? extends Command>[] noRegister() default {};
}
