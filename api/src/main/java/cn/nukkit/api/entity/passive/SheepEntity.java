package cn.nukkit.api.entity.passive;

import cn.nukkit.api.entity.Animal;
import cn.nukkit.api.util.data.DyeColor;

/**
 * @author CreeperFace
 */
public interface SheepEntity extends Animal {

    boolean isSheared();

    void shear();

    DyeColor getColor();

    void setColor(DyeColor color);
}
