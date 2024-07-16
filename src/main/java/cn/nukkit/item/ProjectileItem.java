package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityEnderEye;
import cn.nukkit.entity.projectile.EntityEnderPearl;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
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
        if (this instanceof ItemEnderEye && player.getLevel().getDimension() != Level.DIMENSION_OVERWORLD) {
            return false;
        }

        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", player.x))
                        .add(new DoubleTag("", player.y + player.getEyeHeight()))
                        .add(new DoubleTag("", player.z)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", directionVector.x))
                        .add(new DoubleTag("", directionVector.y))
                        .add(new DoubleTag("", directionVector.z)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", (float) player.yaw))
                        .add(new FloatTag("", (float) player.pitch)));

        this.correctNBT(nbt);

        Entity projectile = Entity.createEntity(this.getProjectileEntityType(), player.getLevel().getChunk(player.getChunkX(), player.getChunkZ()), nbt, player);
        if (projectile != null) {
            if (projectile instanceof EntityEnderPearl || projectile instanceof EntityEnderEye) {
                if (player.getServer().getTick() - player.getLastEnderPearlThrowingTick() < 20) {
                    projectile.close();
                    return false;
                }
            }

            if (projectile instanceof EntityEnderEye) {
                if (player.getServer().getTick() - player.getLastEnderPearlThrowingTick() < 20) {
                    projectile.close();
                    return false;
                }

                Position strongholdPosition = this.getStrongholdPosition(player);
                if (strongholdPosition == null) {
                    projectile.close();
                    return false;
                }

                Vector3f vector = strongholdPosition
                        .subtract(player.getPosition())
                        .asVector3f()
                        .normalize()
                        .multiply(1f);

                vector.y = 0.55f;

                projectile.setMotion(vector.asVector3().divide(this.getThrowForce()));
            } else {
                projectile.setMotion(projectile.getMotion().multiply(this.getThrowForce()));
            }

            if (projectile instanceof EntityProjectile) {
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
        }

        return true;
    }

    protected void correctNBT(CompoundTag nbt) {
    }

    private Position getStrongholdPosition(Player p) {
        return p.level.getDimension() == Level.DIMENSION_OVERWORLD ? p : null; // TODO
    }
}
