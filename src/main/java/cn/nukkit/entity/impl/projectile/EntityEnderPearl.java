package cn.nukkit.entity.impl.projectile;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.projectile.EnderPearl;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerTeleportEvent.TeleportCause;
import cn.nukkit.level.Sound;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.player.Player;

public class EntityEnderPearl extends EntityProjectile implements EnderPearl {

    public EntityEnderPearl(EntityType<EnderPearl> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    public float getWidth() {
        return 0.25f;
    }

    @Override
    public float getLength() {
        return 0.25f;
    }

    @Override
    public float getHeight() {
        return 0.25f;
    }

    @Override
    public float getGravity() {
        return 0.03f;
    }

    @Override
    public float getDrag() {
        return 0.01f;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        this.timing.startTiming();

        boolean hasUpdate = super.onUpdate(currentTick);

        if (this.isCollided && this.getOwner() instanceof Player) {
            teleport();
        }

        if (this.age > 1200 || this.isCollided) {
            this.kill();
            hasUpdate = true;
        }

        this.timing.stopTiming();

        return hasUpdate;
    }

    @Override
    public void onCollideWithEntity(Entity entity) {
        if (this.getOwner() instanceof Player) {
            teleport();
        }
        super.onCollideWithEntity(entity);
    }

    private void teleport() {
        Entity owner = this.getOwner();
        owner.teleport(new Vector3f(NukkitMath.floorDouble(this.x) + 0.5, this.y, NukkitMath.floorDouble(this.z) + 0.5), TeleportCause.ENDER_PEARL);
        if ((((Player) owner).getGamemode() & 0x01) == 0) {
            owner.attack(new EntityDamageByEntityEvent(this, owner, EntityDamageEvent.DamageCause.PROJECTILE, 5f, 0f));
        }
        this.level.addSound(this, Sound.MOB_ENDERMEN_PORTAL);
    }
}
