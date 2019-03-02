package com.nukkitx.api.entity.component;

import com.nukkitx.api.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.Optional;

@Nonnull
@ParametersAreNullableByDefault
public interface Equippable extends EntityComponent {
    /**
     * Gets the helmet the entity is wearing.
     *
     * @return the helmet worn
     */
    Optional<ItemStack> getHelmet();

    /**
     * Sets the helmet the entity is wearing.
     *
     * @param helmet the helmet to wear
     */
    void setHelmet(ItemStack helmet);

    /**
     * Gets the chestplate the entity is wearing.
     *
     * @return the chestplate worn
     */
    Optional<ItemStack> getChestplate();

    /**
     * Sets the chestplate the entity is wearing.
     *
     * @param chestplate the chestplate to wear
     */
    void setChestplate(ItemStack chestplate);

    /**
     * Gets the leggings the entity is wearing.
     *
     * @return the leggings worn
     */
    Optional<ItemStack> getLeggings();

    /**
     * Sets the leggings the entity is wearing.
     *
     * @param leggings the leggings to wear
     */
    void setLeggings(ItemStack leggings);

    /**
     * Gets the boots the entity is wearing.
     *
     * @return the boots worn
     */
    Optional<ItemStack> getBoots();

    /**
     * Sets the boots the entity is wearing.
     *
     * @param boots the boots to wear
     */
    void setBoots(ItemStack boots);
}
