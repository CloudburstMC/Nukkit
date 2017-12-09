package cn.nukkit.api.entity;

/**
 * @author CreeperFace
 */
public interface Projectile extends Entity {

    double getBaseDamage();

    Entity getShootingEntity();
}
