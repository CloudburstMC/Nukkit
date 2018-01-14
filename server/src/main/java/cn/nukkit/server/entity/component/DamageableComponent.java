package cn.nukkit.server.entity.component;

import cn.nukkit.api.entity.component.Damageable;
import cn.nukkit.api.event.entity.EntityDamageEvent;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class DamageableComponent implements Damageable {
    private int maximumHealth;
    private float health;
    private EntityDamageEvent last;

    public DamageableComponent(int maximumHealth) {
        this.maximumHealth = maximumHealth;
        health = maximumHealth;
        last = null;
    }

    public int getRoundedHealth() {
        return (health < 0 ? 0 : Math.round(health));
    }

    @Override
    public float getHealth() {
        return health;
    }

    @Override
    public void setHealth(@Nonnegative float health) {
        Preconditions.checkArgument(health <= maximumHealth, "health cannot be larger than maximum health");
        this.health = health;
    }

    @Override
    @Nonnegative
    public int getMaximumHealth() {
        return maximumHealth;
    }

    @Override
    public void setMaximumHealth(int maximumHealth) {
        this.maximumHealth = maximumHealth;
    }

    @Override
    public void damage(@Nonnegative float damage) {
        health -= damage;
    }

    @Override
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
    }
}
