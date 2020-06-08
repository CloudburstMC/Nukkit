package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityEnderPearl;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelSoundEventPacketV2;

/**
 * @author CreeperFace
 */
public abstract class ProjectileItem extends Item {

    public ProjectileItem(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    abstract public String getProjectileEntityType();

    abstract public float getThrowForce();

    public boolean onClickAir(Player player, Vector3 directionVector) {
        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", player.x))
                        .add(new DoubleTag("", player.y + player.getEyeHeight() - 0.30000000149011612))
                        .add(new DoubleTag("", player.z)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", directionVector.x))
                        .add(new DoubleTag("", directionVector.y))
                        .add(new DoubleTag("", directionVector.z)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", (float) player.yaw))
                        .add(new FloatTag("", (float) player.pitch)));

        this.correctNBT(nbt);

        Entity projectile = Entity.createEntity(this.getProjectileEntityType(), player.getLevel().getChunk(player.getFloorX() >> 4, player.getFloorZ() >> 4), nbt, player);
        if (projectile != null) {
            projectile = correctProjectile(player, projectile);
            if (projectile == null) {
                return false;
            }

            projectile.setMotion(projectile.getMotion().multiply(this.getThrowForce()));

            if (projectile instanceof EntityProjectile) {
                ProjectileLaunchEvent ev = new ProjectileLaunchEvent((EntityProjectile) projectile);

                player.getServer().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    projectile.kill();
                } else {
                    if (!player.isCreative()) {
                        this.count--;
                    }
                    if (projectile instanceof EntityEnderPearl) {
                        player.onThrowEnderPearl();
                    }
                    projectile.spawnToAll();
                    addThrowSound(player);
                }
            }
        } else {
            return false;
        }
        return true;
    }

    protected void addThrowSound(Player player) {
        player.getLevel().addLevelSoundEvent(player, LevelSoundEventPacketV2.SOUND_THROW, -1, "minecraft:player", false, false);
    }

    protected Entity correctProjectile(Player player, Entity projectile) {
        return projectile;
    }

    protected void correctNBT(CompoundTag nbt) {

    }
}
