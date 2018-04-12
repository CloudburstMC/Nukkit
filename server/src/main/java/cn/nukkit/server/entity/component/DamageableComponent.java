/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.entity.component;

import cn.nukkit.api.entity.component.Damageable;
import cn.nukkit.server.entity.Attribute;
import cn.nukkit.server.entity.BaseEntity;
import com.google.common.base.Preconditions;

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
