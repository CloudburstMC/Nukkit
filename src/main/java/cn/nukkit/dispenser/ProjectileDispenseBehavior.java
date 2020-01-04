package cn.nukkit.dispenser;

import cn.nukkit.block.BlockDispenser;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.registry.EntityRegistry;

/**
 * @author CreeperFace
 */
public class ProjectileDispenseBehavior implements DispenseBehavior {

    private EntityType<?> entityType;

    public ProjectileDispenseBehavior() {

    }

    public ProjectileDispenseBehavior(EntityType<?> entity) {
        this.entityType = entity;
    }

    @Override
    public void dispense(BlockDispenser source, Item item) {
        Position dispensePos = Position.fromObject(source.getDispensePosition(), source.getLevel());
        CompoundTag nbt = Entity.getDefaultNBT(dispensePos);
        this.correctNBT(nbt);

        BlockFace face = source.getFacing();

        Entity projectile = EntityRegistry.get().newEntity(getEntityType(), dispensePos.getLevel().getChunk(dispensePos.getFloorX(), dispensePos.getFloorZ()), nbt);
        if (projectile == null) {
            return;
        }

        projectile.setMotion(new Vector3(face.getXOffset(), face.getYOffset() + 0.1f, face.getZOffset()).multiply(6));
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
