package cn.nukkit.plugin.simple;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    String name();

    String usage() default "";

    String description() default "";

    String permission();

    String[] aliases() default {};
}
