package cn.nukkit.item;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.projectile.EnderPearl;
import cn.nukkit.entity.projectile.Projectile;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.player.Player;
import cn.nukkit.registry.EntityRegistry;
import cn.nukkit.utils.Identifier;

/**
 * @author CreeperFace
 */
public abstract class ProjectileItem extends Item {

    public ProjectileItem(Identifier id) {
        super(id);
    }

    abstract public EntityType<? extends Projectile> getProjectileEntityType();

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

        Projectile projectile = EntityRegistry.get().newEntity(this.getProjectileEntityType(), player.getLevel().getChunk(player.getFloorX() >> 4, player.getFloorZ() >> 4), nbt);
        projectile.shootingEntity = player;
        if (projectile instanceof EnderPearl) {
            if (player.getServer().getTick() - player.getLastEnderPearlThrowingTick() < 20) {
                projectile.kill();
                return false;
            }
        }

        projectile.setMotion(projectile.getMotion().multiply(this.getThrowForce()));

        ProjectileLaunchEvent ev = new ProjectileLaunchEvent((Projectile) projectile);

        player.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            projectile.kill();
        } else {
            if (!player.isCreative()) {
                this.decrementCount();
            }
            if (projectile instanceof EnderPearl) {
                player.onThrowEnderPearl();
            }
            projectile.spawnToAll();
            player.getLevel().addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BOW);
        }
        return true;
    }

    protected void correctNBT(CompoundTag nbt) {

    }
}
