package cn.nukkit.api;

import java.lang.annotation.*;

/**
 * Indicates that the annotated element is only available in PowerNukkit 
 * or in Cloudburst Nukkit environment with the "new-raknet" branch merged  
 * and will cause issues when used in a normal Nukkit server without the "new-raknet" patches and features.
 * 
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE,
        ElementType.FIELD, ElementType.PACKAGE})
@Inherited
@Documented
public @interface NewRakNetOnly {
    String value() default "";
}
