package cn.nukkit.plugin.simple;

import cn.nukkit.plugin.PluginLoadOrder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
@ Main(name="11",api = "1.0.9",commands ={
        @ Command(name="fp",
            permission = @ Permission(
                    theDefault = "op"
            )
        )
})
*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Main {


    String name();

    String version() default "0.0.1";

    String author() default "";

    String[] api();

    String[] depend() default {};

    String[] loadBefore() default {};

    String [] softDepend() default {};

    String description() default "";

    PluginLoadOrder load() default PluginLoadOrder.POSTWORLD;

    String website() default "";

    String prefix() default "";

    Command[] commands() default {};

    Permission[] permissions() default {};


}
