package cn.nukkit.api.entity.component;

import cn.nukkit.api.item.ItemInstance;

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
    Optional<ItemInstance> getHelmet();

    /**
     * Sets the helmet the entity is wearing.
     *
     * @param helmet the helmet to wear
     */
    void setHelmet(ItemInstance helmet);

    /**
     * Gets the chestplate the entity is wearing.
     *
     * @return the chestplate worn
     */
    Optional<ItemInstance> getChestplate();

    /**
     * Sets the chestplate the entity is wearing.
     *
     * @param chestplate the chestplate to wear
     */
    void setChestplate(ItemInstance chestplate);

    /**
     * Gets the leggings the entity is wearing.
     *
     * @return the leggings worn
     */
    Optional<ItemInstance> getLeggings();

    /**
     * Sets the leggings the entity is wearing.
     *
     * @param leggings the leggings to wear
     */
    void setLeggings(ItemInstance leggings);

    /**
     * Gets the boots the entity is wearing.
     *
     * @return the boots worn
     */
    Optional<ItemInstance> getBoots();

    /**
     * Sets the boots the entity is wearing.
     *
     * @param boots the boots to wear
     */
    void setBoots(ItemInstance boots);
}
