package cn.nukkit.api.entity.passive;

import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.util.data.DyeColor;

/**
 * @author CreeperFace
 */
public interface Sheep extends Entity {

    boolean isSheared();

    void shear();

    DyeColor getColor();

    void setColor(DyeColor color);
}
