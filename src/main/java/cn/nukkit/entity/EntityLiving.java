package cn.nukkit.entity;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.data.ShortEntityData;
import cn.nukkit.entity.passive.EntityWaterAnimal;
import cn.nukkit.event.entity.*;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ShortTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.potion.Effect;
import cn.nukkit.timings.Timings;
import cn.nukkit.utils.BlockIterator;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityLiving extends Entity implements EntityDamageable {
    public EntityLiving(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected float getGravity() {
        return 0.08f;
    }

    @Override
    protected float getDrag() {
        return 0.02f;
    }

    protected int attackTime = 0;

    protected boolean invisible = false;

    protected float movementSpeed = 0.1f;

    @Override
    protected void initEntity() {
        super.initEntity();

        if (this.namedTag.contains("HealF")) {
            this.namedTag.putShort("Health", this.namedTag.getShort("HealF"));
            this.namedTag.remove("HealF");
        }

        if (!this.namedTag.contains("Health") || !(this.namedTag.get("Health") instanceof ShortTag)) {
            this.namedTag.putShort("Health", this.getMaxHealth());
        }

        this.setHealth(this.namedTag.getShort("Health"));
    }

    @Override
    public void setHealth(float health) {
        boolean wasAlive = this.isAlive();
        super.setHealth(health);
        if (this.isAlive() && !wasAlive) {
            EntityEventPacket pk = new EntityEventPacket();
            pk.eid = this.getId();
            pk.eid = EntityEventPacket.RESPAWN;
            Server.broadcastPacket(this.hasSpawned.values(), pk);
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putShort("Health", this.getHealth());
    }

    public boolean hasLineOfSight(Entity entity) {
        //todo
        return true;
    }

    @Override
    public void heal(EntityRegainHealthEvent source) {
        super.heal(source);
        if (source.isCancelled()) {
            return;
        }

        this.attackTime = 0;
    }

    @Override
    public void attack(EntityDamageEvent source) {
        if (this.attackTime > 0 || this.noDamageTicks > 0) {
            EntityDamageEvent lastCause = this.getLastDamageCause();
            if (lastCause != null && lastCause.getDamage() >= source.getDamage()) {
                source.setCancelled();
            }
        }

        super.attack(source);

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
            double deltaZ = this.z - e.z;
            this.knockBack(e, source.getDamage(), deltaX, deltaZ, ((EntityDamageByEntityEvent) source).getKnockBack());
        }

        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = this.getId();
        pk.event = this.getHealth() <= 0 ? EntityEventPacket.DEATH_ANIMATION : EntityEventPacket.HURT_ANIMATION;
        Server.broadcastPacket(this.hasSpawned.values(), pk);

        this.attackTime = 10;
    }

    public void knockBack(Entity attacker, double damage, double x, double z) {
        this.knockBack(attacker, damage, x, z, 0.4);
    }

    public void knockBack(Entity attacker, double damage, double x, double z, double base) {
        double f = Math.sqrt(x * x + z * z);
        if (f <= 0) {
            return;
        }

        f = 1 / f;

        Vector3 motion = new Vector3(this.motionX, this.motionY, this.motionZ);

        motion.x /= 2d;
        motion.y /= 2d;
        motion.z /= 2d;
        motion.x += x * f * base;
        motion.y += base;
        motion.z += z * f * base;

        if (motion.y > base) {
            motion.y = base;
        }

        this.setMotion(motion);
    }

    @Override
    public void kill() {
        if (!this.isAlive()) {
            return;
        }
        super.kill();
        EntityDeathEvent ev = new EntityDeathEvent(this, this.getDrops());
        this.server.getPluginManager().callEvent(ev);
        for (cn.nukkit.item.Item item : ev.getDrops()) {
            this.getLevel().dropItem(this, item);
        }
    }

    @Override
    public boolean entityBaseTick() {
        return this.entityBaseTick(1);
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        Timings.livingEntityBaseTickTimer.startTiming();
        boolean hasUpdate = super.entityBaseTick(tickDiff);

        if (this.isAlive()) {
            if (this.isInsideOfSolid()) {
                hasUpdate = true;
                EntityDamageEvent ev = new EntityDamageEvent(this, EntityDamageEvent.CAUSE_SUFFOCATION, 1);
                this.attack(ev);
            }

            if (!this.hasEffect(Effect.WATER_BREATHING) && this.isInsideOfWater()) {
                if (this instanceof EntityWaterAnimal) {
                    this.setDataProperty(new ShortEntityData(DATA_AIR, 300));
                } else {
                    hasUpdate = true;
                    int airTicks = this.getDataPropertyShort(DATA_AIR) - tickDiff;

                    if (airTicks <= -20) {
                        airTicks = 0;
                        EntityDamageEvent ev = new EntityDamageEvent(this, EntityDamageEvent.CAUSE_DROWNING, 2);
                        this.attack(ev);
                    }

                    this.setDataProperty(new ShortEntityData(DATA_AIR, airTicks));
                }
            } else {
                if (this instanceof EntityWaterAnimal) {
                    hasUpdate = true;
                    int airTicks = this.getDataPropertyInt(DATA_AIR) - tickDiff;

                    if (airTicks <= -20) {
                        airTicks = 0;
                        EntityDamageEvent ev = new EntityDamageEvent(this, EntityDamageEvent.CAUSE_SUFFOCATION, 2);
                        this.attack(ev);
                    }

                    this.setDataProperty(new ShortEntityData(DATA_AIR, airTicks));
                } else {
                    this.setDataProperty(new ShortEntityData(DATA_AIR, 300));
                }
            }
        }

        if (this.attackTime > 0) {
            this.attackTime -= tickDiff;
        }
        Timings.livingEntityBaseTickTimer.stopTiming();

        return hasUpdate;
    }

    public Item[] getDrops() {
        return new Item[0];
    }

    public Block[] getLineOfSight(int maxDistance) {
        return this.getLineOfSight(maxDistance, 0);
    }

    public Block[] getLineOfSight(int maxDistance, int maxLength) {
        return this.getLineOfSight(maxDistance, maxLength, new Integer[]{});
    }

    @Deprecated
    public Block[] getLineOfSight(int maxDistance, int maxLength, Map<Integer, Object> transparent) {
        return this.getLineOfSight(maxDistance, maxLength, transparent.keySet().stream().toArray(Integer[]::new));
    }

    public Block[] getLineOfSight(int maxDistance, int maxLength, Integer[] transparent) {
        if (maxDistance > 120) {
            maxDistance = 120;
        }

        if (transparent != null && transparent.length == 0) {
            transparent = null;
        }

        List<Block> blocks = new ArrayList<>();

        BlockIterator itr = new BlockIterator(this.level, this.getPosition(), this.getDirectionVector(), this.getEyeHeight(), maxDistance);

        while (itr.hasNext()) {
            Block block = itr.next();
            blocks.add(block);

            if (maxLength != 0 && blocks.size() > maxLength) {
                blocks.remove(0);
            }

            int id = block.getId();

            if (transparent == null) {
                if (id != 0) {
                    break;
                }
            } else {
                if (Arrays.binarySearch(transparent, id) < 0) {
                    break;
                }
            }
        }

        return blocks.stream().toArray(Block[]::new);
    }

    public Block getTargetBlock(int maxDistance) {
        return getTargetBlock(maxDistance, new Integer[]{});
    }

    @Deprecated
    public Block getTargetBlock(int maxDistance, Map<Integer, Object> transparent) {
        return getTargetBlock(maxDistance, transparent.keySet().stream().toArray(Integer[]::new));
    }

    public Block getTargetBlock(int maxDistance, Integer[] transparent) {
        try {
            Block[] blocks = this.getLineOfSight(maxDistance, 1, transparent);
            Block block = blocks[0];
            if (block != null) {
                if (transparent != null && transparent.length != 0) {
                    if (Arrays.binarySearch(transparent, block.getId()) < 0) {
                        return block;
                    }
                } else {
                    return block;
                }
            }
        } catch (Exception ignored) {

        }

        return null;
    }

    public void setMovementSpeed(float speed) {
        this.movementSpeed = speed;
    }

    public float getMovementSpeed() {
        return this.movementSpeed;
    }

}
