package com.nukkitx.server.entity.component;

import com.google.common.base.Preconditions;
import com.nukkitx.api.entity.component.Damageable;
import com.nukkitx.server.entity.BaseEntity;

import javax.annotation.Nonnegative;

public class DamageableComponent implements Damageable {
    private final BaseEntity entity;
    private volatile int maximumHealth;
    private volatile float health;
    private volatile boolean stale = false;
    //private EntityDamageEvent last;

    public DamageableComponent(BaseEntity entity, int maximumHealth) {
        this.entity = entity;
        this.maximumHealth = maximumHealth;
        health = maximumHealth;
        //last = null;
    }

    @Override
    @Nonnegative
    public float getFixedHealth() {
        return (health < 0 ? 0 : health);
    }

    @Override
    public float getHealth() {
        return health;
    }

    @Override
    public void setHealth(@Nonnegative float health) {
        Preconditions.checkArgument(health <= maximumHealth, "health cannot be larger than maximum health");
        if (this.health != health) {
            this.health = health;
            entity.onAttributeUpdate(new Attribute("minecraft:health", health, 0f, maximumHealth, maximumHealth));
        }
    }

    @Override
    @Nonnegative
    public int getMaximumHealth() {
        return maximumHealth;
    }

    @Override
    public void setMaximumHealth(int maximumHealth) {
        this.maximumHealth = maximumHealth;
        this.stale = true;
    }

    @Override
    public void damage(@Nonnegative float damage) {
        setHealth(getHealth() - damage);
        this.stale = true;
    }

    @Override
    public void replenish(float replenish) {
        setHealth(getHealth() + replenish);
        this.stale = true;
    }

    public boolean isStale() {
        return stale;
    }

    /*@Override
    public void damage(@Nonnegative float damage, @Nullable EntityDamageEvent source) {
        damage(damage);
        this.last = source;
    }

    @Nonnull
    @Override
    public Optional<EntityDamageEvent> getLastDamageCause() {
        return Optional.ofNullable(last);
    }

    @Override
    public void setLastDamageCause(@Nullable EntityDamageEvent last) {
        this.last = last;
    }*/
}
