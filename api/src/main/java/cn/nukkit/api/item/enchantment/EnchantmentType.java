package cn.nukkit.api.item.enchantment;

/**
 * @author CreeperFace
 */
public interface EnchantmentType {

    int getId();

    String getName();

    int getMaxLevel();

    EnchantmentTarget getTarget();
}
