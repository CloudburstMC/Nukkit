package cn.nukkit.potion;

import cn.nukkit.entity.Entity;
import cn.nukkit.utils.Potions;

/**
 * Created by Snake1999 on 2016/1/11.
 * Package cn.nukkit.potion in project nukkit
 */
public class PotionEffective extends Potion {

    protected int potionType;

    public PotionEffective(int potionType) {
        this.potionType = potionType;
        setDisplayType(potionType);
    }

    @Override
    public void applyTo(Entity entity) {
        Potions.applyPotion(potionType, isSplashPotion(), entity);
    }

}
