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

package cn.nukkit.api.entity;

import cn.nukkit.api.Server;
import cn.nukkit.api.entity.component.EntityComponent;
import cn.nukkit.api.level.Level;
import cn.nukkit.api.util.BoundingBox;
import cn.nukkit.api.util.Rotation;
import com.flowpowered.math.GenericMath;
import com.flowpowered.math.vector.Vector3f;
import com.google.common.base.VerifyException;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface Entity {

    @Nonnull
    Rotation getRotation();

    /**
     * Gets this entity's current velocity
     *
     * @return Current travelling velocity of this entity
     */
    @Nonnull
    Vector3f getMotion();

    UUID getUniqueId();

    @Nonnull
    Vector3f getPosition();

    void setPosition(@Nonnull Vector3f position);

    void setPositionFromSystem(@Nonnull Vector3f position);

    @Nonnull
    Vector3f getGamePosition();

    @Nonnull
    BoundingBox getBoundingBox();

    /**
     * Sets this entity's velocity
     *
     * @param velocity New velocity to travel with
     */
    void setMotion(Vector3f velocity);

    /**
     * Gets the entity's height
     *
     * @return height of entity
     */
    float getHeight();

    /**
     * Gets the entity's width
     *
     * @return width of entity
     */
    float getWidth();

    float getOffset();

    /**
     * Returns true if the entity is supported by a block. This value is a
     * state updated by the server and is not recalculated unless the entity
     * moves.
     *
     * @return True if entity is on ground.
     */
    boolean isOnGround();

    /**
     * Gets the current world this entity resides in
     *
     * @return World
     */
    Level getLevel();

    /**
     * Teleports this entity to the given position. If this entity is riding a
     * vehicle, it will be dismounted prior to teleportation.
     *
     * @param position New location to teleport this entity to
     * @return <code>true</code> if the teleport was successful
     */
    boolean teleport(Vector3f position);

    /**
     * Teleports this entity to the given position in level. If this entity is riding a
     * vehicle, it will be dismounted prior to teleportation.
     *
     * @param location New location to teleport this entity to
     * @return <code>true</code> if the teleport was successful
     */
    boolean teleport(Vector3f location, Level level);

    /**
     * Teleports this entity to the target Entity. If this entity is riding a
     * vehicle, it will be dismounted prior to teleportation.
     *
     * @param destination Entity to teleport this entity to
     * @return <code>true</code> if the teleport was successful
     */
    boolean teleport(Entity destination);

    /**
     * Returns a unique id for this entity
     *
     * @return Entity id
     */
    long getEntityId();

    /**
     * Mark the entity for removal.
     */
    void remove();

    /**
     * Returns true if this entity has been marked for removal.
     *
     * @return True if it is dead.
     */
    boolean isAlive();

    /**
     * Gets the {@link cn.nukkit.api.Server} that contains this Entity
     *
     * @return Server instance running this Entity
     */
    @Nonnull
    Server getServer();

    /*/**
     * Returns the distance this entity has fallen
     *
     * @return The distance.
     */
    //float getFallDistance();

    /*/**
     * Sets the fall distance for this entity
     *
     * @param distance The new distance.
     */
    //void setFallDistance(float distance);


    long getTickCreated();

    void setTickCreated(long tickCreated);

    /**
     * Gets the amount of ticks this entity has lived for.
     * <p>
     * This is the equivalent to "age" in entities.
     *
     * @return Age of entity
     */
    long getTicksLived();

    /**
     * Sets the amount of ticks this entity has lived for.
     * <p>
     * This is the equivalent to "age" in entities. May not be less than one
     * tick.
     *
     * @param ticksLived Age of entity
     */
    void setTicksLived(long ticksLived);

    /**
     * Returns whether this entity is inside a vehicle.
     *
     * @return True if the entity is in a vehicle.
     */
    boolean isInsideVehicle();

    /**
     * Leave the current vehicle. If the entity is currently in a vehicle (and
     * is removed from it), true will be returned, otherwise false will be
     * returned.
     *
     * @return True if the entity was in a vehicle.
     */
    boolean leaveVehicle();

    /**
     * Get the vehicle that this player is inside. If there is no vehicle,
     * null will be returned.
     *
     * @return The current vehicle.
     */
    Optional<Entity> getVehicle();

    /**
     * Gets whether or not the mob's custom name is displayed client side.
     * <p>
     * This value has no effect on players, they will always display their
     * name.
     *
     * @return if the custom name is displayed
     */
    boolean isCustomNameVisible();

    /**
     * Sets whether or not to display the mob's custom name client side. The
     * name will be displayed above the mob similarly to a player.
     * <p>
     * This value has no effect on players, they will always display their
     * name.
     *
     * @param flag custom name or not
     */
    void setCustomNameVisible(boolean flag);

    /**
     * Gets whether the entity is glowing or not.
     *
     * @return whether the entity is glowing
     */
    boolean isGlowing();

    /**
     * Sets whether the entity has a team colored (default: white) glow.
     *
     * @param flag if the entity is glowing
     */
    void setGlowing(boolean flag);

    /**
     * Gets whether the entity is invulnerable or not.
     *
     * @return whether the entity is
     */
    boolean isInvulnerable();

    /**
     * Sets whether the entity is invulnerable or not.
     * <p>
     * When an entity is invulnerable it can only be damaged by players in
     * creative mode.
     *
     * @param flag if the entity is invulnerable
     */
    void setInvulnerable(boolean flag);

    /**
     * Gets whether the entity is silent or not.
     *
     * @return whether the entity is silent.
     */
    boolean isSneaking();

    /**
     * Sets whether the entity is silent or not.
     * <p>
     * When an entity is silent it will not produce any sound.
     *
     * @param sneaking if the entity is silent
     */
    void setSneaking(boolean sneaking);

    //boolean attack(EntityDamageEvent source);

    boolean isAffectedByGravity();

    void setAffectedByGravity(boolean affectedByGravity);

    default Vector3f getDirectionVector() {
        Rotation rotation = getRotation();
        double xzLen = Math.cos(Math.toRadians(rotation.getPitch()));
        double x = -xzLen * Math.sin(Math.toRadians(rotation.getYaw()));
        double y = -Math.sin(Math.toRadians(rotation.getPitch()));
        double z = xzLen * Math.cos(Math.toRadians(rotation.getYaw()));
        return GenericMath.normalizeSafe(new Vector3f(x, y, z));
    }

    Set<Class<? extends EntityComponent>> providedComponents();

    <C extends EntityComponent> boolean provides(Class<C> clazz);

    <C extends EntityComponent> Optional<C> get(Class<C> clazz);

    default <C extends EntityComponent> C ensureAndGet(Class<C> clazz) {
        Optional<C> component = get(clazz);
        if (!component.isPresent()) {
            throw new VerifyException("Component class " + clazz.getName() + " isn't provided by this entity.");
        }
        return component.get();
    }
}
