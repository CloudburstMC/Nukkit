package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityEnderEye;
import cn.nukkit.entity.projectile.EntityEnderPearl;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

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
        Vector3 motion;

        if (this instanceof ItemEnderEye) {
            if (player.getLevel().getDimension() != Level.DIMENSION_OVERWORLD) {
                return false;
            }

            Vector3 vector = player // TODO: Stronghold position here. Meanwhile you can set custom motion in ProjectileLaunchEvent.
                    .subtract(player).normalize();
            vector.y = 0.55f;
            motion = vector.divide(this.getThrowForce());
        } else {
            motion = directionVector.multiply(this.getThrowForce());
        }

        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", player.x))
                        .add(new DoubleTag("", player.y + player.getEyeHeight()))
                        .add(new DoubleTag("", player.z)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", motion.x))
                        .add(new DoubleTag("", motion.y))
                        .add(new DoubleTag("", motion.z)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", (float) player.yaw))
                        .add(new FloatTag("", (float) player.pitch)));

        this.correctNBT(nbt);

        Entity projectile = Entity.createEntity(this.getProjectileEntityType(), player.getLevel().getChunk(player.getChunkX(), player.getChunkZ()), nbt, player);
        if (projectile instanceof EntityProjectile) {
            if (projectile instanceof EntityEnderPearl || projectile instanceof EntityEnderEye) {
                if (player.getServer().getTick() - player.getLastEnderPearlThrowingTick() < 20) {
                    projectile.close();
                    return false;
                }
            }

            ProjectileLaunchEvent ev = new ProjectileLaunchEvent((EntityProjectile) projectile);

            player.getServer().getPluginManager().callEvent(ev);

            if (ev.isCancelled()) {
                projectile.close();
            } else {
                if (!player.isCreative()) {
                    this.count--;
                }
                if (projectile instanceof EntityEnderPearl || projectile instanceof EntityEnderEye) {
                    player.onThrowEnderPearl();
                }
                projectile.spawnToAll();
                player.getLevel().addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BOW);
            }
        }

        return true;
    }

    protected void correctNBT(CompoundTag nbt) {
    }
}
