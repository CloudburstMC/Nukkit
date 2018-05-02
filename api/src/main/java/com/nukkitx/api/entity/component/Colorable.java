package com.nukkitx.api.entity.component;

import com.nukkitx.api.metadata.data.DyeColor;

import javax.annotation.Nonnull;

public interface Colorable extends EntityComponent {
    /**
     * Get the color of this entity
     *
     * @return the color of the entity
     */
    DyeColor getColor();

    /**
     * Set the entity color of this wolf
     *
     * @param color the color to apply
     */
    void setColor(@Nonnull DyeColor color);
}
