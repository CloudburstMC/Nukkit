package cn.nukkit.item;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.Projectile;
import cn.nukkit.entity.impl.projectile.EntityEnderPearl;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.level.Location;
import cn.nukkit.player.Player;
import cn.nukkit.registry.EntityRegistry;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.data.SoundEvent;

/**
 * @author CreeperFace
 */
public abstract class ProjectileItem extends Item {

    public ProjectileItem(Identifier id) {
        super(id);
    }

    abstract public EntityType<? extends Projectile> getProjectileEntityType();

    abstract public float getThrowForce();

    public boolean onClickAir(Player player, Vector3f directionVector) {
        Location location = Location.from(player.getPosition().add(0, player.getEyeHeight() - 0.3f, 0),
                player.getYaw(), player.getPitch(), player.getLevel());

        Projectile projectile = EntityRegistry.get().newEntity(this.getProjectileEntityType(), location);
        projectile.setPosition(location.getPosition());
        projectile.setRotation(player.getYaw(), player.getPitch());
        projectile.setMotion(directionVector);
        projectile.setOwner(player);
        this.onProjectileCreation(projectile);

        if (projectile instanceof EntityEnderPearl) {
            if (player.getServer().getTick() - player.getLastEnderPearlThrowingTick() < 20) {
                projectile.kill();
                return false;
            }
        }

        projectile.setMotion(projectile.getMotion().mul(this.getThrowForce()));

        ProjectileLaunchEvent ev = new ProjectileLaunchEvent(projectile);

        player.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            projectile.kill();
        } else {
            if (!player.isCreative()) {
                this.decrementCount();
            }
            if (projectile instanceof EntityEnderPearl) {
                player.onThrowEnderPearl();
            }
            projectile.spawnToAll();
            player.getLevel().addLevelSoundEvent(player.getPosition(), SoundEvent.BOW);
        }
        return true;
    }

    protected void onProjectileCreation(Projectile projectile) {

    }
}
