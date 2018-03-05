package cn.nukkit.api.entity.component;

import cn.nukkit.api.util.data.DyeColor;

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
    void setColor(DyeColor color);
}
