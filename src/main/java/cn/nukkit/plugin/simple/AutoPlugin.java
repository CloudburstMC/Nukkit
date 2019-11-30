package cn.nukkit.plugin.simple;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * You can use the AutoPlugin annotation to inject objects of the main class,
 * which was recently added based on a simple plugin system.
 * <code>
 *     @ AutoPlugin
 *     private Plugin plugin;
 *     @ Override
 *     public boolean execute(CommandSender commandSender, String label, String[] strings) {
 *         plugin.getLogger().info("233");//no npe
 *         return true;
 *     }
 * </code>
 * @author magiclu # DreamServer
 * @see java.lang.annotation.Annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface AutoPlugin {


}
