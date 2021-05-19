package cn.nukkit.api;

import java.lang.annotation.*;

/**
 * Indicates that the annotated element was removed in the popular Cloudburst new-raknet branch, using it
 * will work in PowerNukkit and normal Nukkit but will cause issues in that branch or when the branch gets
 * merged.
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
public @interface RemovedFromNewRakNet {
    String value() default "";
}
