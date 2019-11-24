package cn.nukkit.plugin.simple;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {

    String permission();

    String description() default "";

    String theDefault();

}
