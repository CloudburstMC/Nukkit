package cn.nukkit.api.enchantment;

public interface EnchantmentBuilder {

    EnchantmentBuilder level(int level);

    EnchantmentBuilder type(EnchantmentType type);

    EnchantmentType build();
}
