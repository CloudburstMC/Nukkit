package cn.nukkit.dispenser;

import cn.nukkit.block.BlockDispenser;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.Projectile;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.math.BlockFace;
import cn.nukkit.registry.EntityRegistry;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.nbt.tag.CompoundTag;

/**
 * @author CreeperFace
 */
public class ProjectileDispenseBehavior implements DispenseBehavior {

    private EntityType<? extends Projectile> entityType;

    public ProjectileDispenseBehavior() {

    }

    public ProjectileDispenseBehavior(EntityType<? extends Projectile> entity) {
        this.entityType = entity;
    }

    @Override
    public void dispense(BlockDispenser source, Item item) {
        Location dispensePos = Location.from(source.getDispensePosition(), source.getLevel());

        BlockFace face = source.getFacing();

        Projectile projectile = EntityRegistry.get().newEntity(entityType, dispensePos);
        if (projectile == null) {
            return;
        }

        projectile.setMotion(Vector3f.from(face.getXOffset(), face.getYOffset() + 0.1f, face.getZOffset()).mul(6));
        projectile.spawnToAll();
    }

    protected EntityType<?> getEntityType() {
        return this.entityType;
    }

    /**
     * you can add extra data of projectile here
     *
     * @param nbt tag
     */
    protected void correctNBT(CompoundTag nbt) {

    }
}
