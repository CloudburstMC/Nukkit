package cn.nukkit.entity;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public interface EntityAgeable {

    boolean isBaby();

    default void setBaby(boolean baby) {

    }
}
