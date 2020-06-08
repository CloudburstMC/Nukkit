package cn.nukkit.api;

import java.lang.annotation.*;

/**
 * Indicates that the annotated element is only available in PowerNukkit environment
 * and will cause issues when used in a normal NukkitX server without PowerNukkit's patches and features. 
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE,
        ElementType.FIELD, ElementType.PACKAGE})
@PowerNukkitOnly @Since("1.2.1.0-PN")
@Inherited
@Documented
public @interface PowerNukkitOnly {
    String value() default "";
}
