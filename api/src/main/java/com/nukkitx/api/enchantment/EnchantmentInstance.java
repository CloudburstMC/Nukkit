package com.nukkitx.api.enchantment;

import jdk.nashorn.internal.ir.annotations.Immutable;

@Immutable
public interface EnchantmentInstance {

    int getLevel();

    EnchantmentType getEnchantmentType();
}
