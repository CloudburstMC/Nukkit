package cn.nukkit.dispenser;

import cn.nukkit.block.BlockDispenser;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.Projectile;
import cn.nukkit.entity.impl.projectile.EntityProjectile;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.math.BlockFace;
import cn.nukkit.registry.EntityRegistry;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;

/**
 * @author CreeperFace
 */
public class ProjectileDispenseBehavior extends DefaultDispenseBehavior {

    private EntityType<? extends Projectile> entityType;

    public ProjectileDispenseBehavior(EntityType<? extends Projectile> entity) {
        this.entityType = entity;
    }

    @Override
    public Item dispense(Vector3i position, BlockDispenser source, BlockFace face, Item item) {
        Vector3f dispensePos = source.getDispensePosition();

        CompoundTagBuilder nbt = CompoundTagBuilder.builder();
        this.correctNBT(nbt);

        Entity projectile = EntityRegistry.get().newEntity(entityType, Location.from(dispensePos, source.getLevel()));

        if (projectile == null) {
            return super.dispense(position, source, face, item);
        }

        Vector3f motion = Vector3f.from(face.getXOffset(), face.getYOffset() + 0.1f, face.getZOffset())
                .normalize();

        projectile.setMotion(motion);
        ((EntityProjectile) projectile).inaccurate(getAccuracy());
        projectile.setMotion(projectile.getMotion().mul(getMotion()));

        ((EntityProjectile) projectile).updateRotation();

        projectile.spawnToAll();
        return null;
    }

    protected double getMotion() {
        return 1.1;
    }

    protected float getAccuracy() {
        return 6;
    }

    protected EntityType<?> getEntityType() {
        return this.entityType;
    }

    /**
     * you can add extra data of projectile here
     *
     * @param nbt tag
     */
    protected void correctNBT(CompoundTagBuilder nbt) {

    }
}
