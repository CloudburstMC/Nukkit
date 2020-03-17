package cn.nukkit.entity.projectile;

import cn.nukkit.entity.Projectile;

public interface SplashPotion extends Projectile {

    short getPotionId();

    void setPotionId(int potionId);
}
