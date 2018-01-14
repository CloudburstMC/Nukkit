package cn.nukkit.api.entity.component;

import cn.nukkit.api.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public interface Armorable extends EntityComponent {
    /**
     * Gets the helmet the entity is wearing.
     * @return the helmet worn
     */
    @Nonnull
    Optional<ItemStack> getHelmet();

    /**
     * Sets the helmet the entity is wearing.
     * @param stack the helmet to wear
     */
    void setHelmet(@Nullable ItemStack stack);

    /**
     * Gets the chestplate the entity is wearing.
     * @return the chestplate worn
     */
    @Nonnull
    Optional<ItemStack> getChestplate();

    /**
     * Sets the chestplate the entity is wearing.
     * @param stack the chestplate to wear
     */
    void setChestplate(@Nullable ItemStack stack);

    /**
     * Gets the leggings the entity is wearing.
     * @return the leggings worn
     */
    @Nonnull
    Optional<ItemStack> getLeggings();

    /**
     * Sets the leggings the entity is wearing.
     * @param stack the leggings to wear
     */
    void setLeggings(@Nullable ItemStack stack);

    /**
     * Gets the boots the entity is wearing.
     * @return the boots worn
     */
    @Nonnull
    Optional<ItemStack> getBoots();

    /**
     * Sets the boots the entity is wearing.
     * @param stack the boots to wear
     */
    void setBoots(@Nullable ItemStack stack);
}
