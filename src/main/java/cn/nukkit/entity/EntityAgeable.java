package cn.nukkit.entity;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface EntityAgeable {
    int DATA_AGEABLE_FLAGS = 14;

    int DATA_FLAG_BABY = 0;

    boolean isBaby();
}
