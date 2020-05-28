package cn.nukkit.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated element is only available in PowerNukkit environment
 * and will cause issues when used in a normal NukkitX server without PowerNukkit's patches and features. 
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE,
        ElementType.FIELD, ElementType.PACKAGE})
@PowerNukkitOnly @Since("1.2.1.0-PN")
public @interface PowerNukkitOnly {
}
