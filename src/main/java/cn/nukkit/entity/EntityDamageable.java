package cn.nukkit.entity;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface EntityDamageable {
    /**
     * @return if the entity is blocking.
     */
    default boolean isBlocking() {
        return false;
    }
}
