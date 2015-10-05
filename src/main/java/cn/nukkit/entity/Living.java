package cn.nukkit.entity;

import cn.nukkit.Server;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.CompoundTag;
import cn.nukkit.nbt.ShortTag;
import cn.nukkit.network.Network;
import cn.nukkit.network.protocol.EntityEventPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Living extends Entity implements Damageable {
    public Living(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    protected float gravity = 0.08f;
    protected float drag = 0.02f;

    protected int attackTime = 0;

    protected boolean invisible = false;

    @Override
    protected void initEntity() {
        super.initEntity();

        if (this.namedTag.contains("HealF")) {
            this.namedTag.putShort("Health", this.namedTag.getShort("HealF"));
            this.namedTag.remove("HealF");
        }

        if (!this.namedTag.contains("Health") || !(this.namedTag.get("Health") instanceof ShortTag)) {
            this.namedTag.putShort("Health", (short) this.getMaxHealth());
        }

        this.setHealth(this.namedTag.getShort("Health"));
    }

    @Override
    public void setHealth(float health) {
        this.setHealth((int) health);
    }

    @Override
    public void setHealth(int health) {
        boolean wasAlive = this.isAlive();
        super.setHealth(health);
        if (this.isAlive() && !wasAlive) {
            EntityEventPacket pk = new EntityEventPacket();
            pk.eid = this.getId();
            pk.eid = EntityEventPacket.RESPAWN;
            Server.broadcastPacket(this.hasSpawned.values(), pk.setChannel(Network.CHANNEL_WORLD_EVENTS));
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putShort("Health", (short) this.getHealth());
    }

    public abstract String getName();

    public boolean hasLineOfSight(Entity entity) {
        //todo
        return true;
    }

    @Override
    public void heal(float amount, EntityRegainHealthEvent source) {
        super.heal(amount, source);
        if (source.isCancelled()) {
            return;
        }

        this.attackTime = 0;
    }

    @Override
    public void attack(float damage, EntityDamageEvent source) {
        if (this.attackTime > 0 && this.noDamageTicks > 0) {
            EntityDamageEvent lastCause = this.getLastDamageCause();
            if (lastCause != null && lastCause.getDamage() >= damage) {
                source.setCancelled();
            }
        }

        super.attack(damage, source);

        if (source.isCancelled()) {
            return;
        }

        if (source instanceof EntityDamageByEntityEvent) {
            Entity e = ((EntityDamageByEntityEvent) source).getDamager();
            if (source instanceof EntityDamageByChildEntityEvent) {
                e = ((EntityDamageByChildEntityEvent) source).getChild();
            }

            if (e.isOnFire()) {
                this.setOnFire(2 * this.server.getDifficulty());
            }

            double deltaX = this.x - e.x;
            double deltaZ = this.z = e.z;
            this.knockBack(e, damage, deltaX, deltaZ, ((EntityDamageByEntityEvent) source).getKnockBack());
        }

        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = this.getId();
        pk.event = this.getHealth() <= 0 ? EntityEventPacket.DEATH_ANIMATION : EntityEventPacket.HURT_ANIMATION;
        Server.broadcastPacket(this.hasSpawned.values(), pk.setChannel(Network.CHANNEL_WORLD_EVENTS));

        this.attackTime = 10;
    }

    //todo more
}
