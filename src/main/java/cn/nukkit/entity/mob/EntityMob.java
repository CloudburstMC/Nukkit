package cn.nukkit.entity.mob;

import cn.nukkit.entity.Entity;

public interface EntityMob {

    void attackEntity(Entity player);

    int getDamage();
    int getDamage(Integer difficulty);

    int getMinDamage();
    int getMinDamage(Integer difficulty);

    int getMaxDamage();
    int getMaxDamage(Integer difficulty);

    void setDamage(int damage);
    void setDamage(int[] damage);
    void setDamage(int damage, int difficulty);

    void setMinDamage(int damage);
    void setMinDamage(int[] damage);
    void setMinDamage(int damage, int difficulty);

    void setMaxDamage(int damage);
    void setMaxDamage(int[] damage);
    void setMaxDamage(int damage, int difficulty);

}
