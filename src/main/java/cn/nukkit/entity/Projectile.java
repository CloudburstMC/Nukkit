package cn.nukkit.entity;

import javax.annotation.Nullable;

public interface Projectile extends Entity {

    @Nullable
    default Entity getShooter() {
        return getOwner();
    }

    default void setShooter(@Nullable Entity entity) {
        setOwner(entity);
    }

    boolean isCritical();

    void setCritical(boolean critical);

    default void setCritical() {
        setCritical(true);
    }

    float getDamage();

    void setDamage(float damage);
}
