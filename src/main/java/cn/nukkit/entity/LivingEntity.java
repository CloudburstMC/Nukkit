package cn.nukkit.entity;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockMagma;
import cn.nukkit.entity.data.EntityData;
import cn.nukkit.entity.passive.WaterAnimal;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTurtleShell;
import cn.nukkit.level.Position;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.gamerule.GameRules;
import cn.nukkit.math.BlockRayTrace;
import cn.nukkit.math.Vector3f;
import cn.nukkit.math.Vector3i;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.network.protocol.AnimatePacket;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.player.Player;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.Identifier;
import co.aikar.timings.Timing;
import co.aikar.timings.Timings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cn.nukkit.block.BlockIds.AIR;
import static cn.nukkit.entity.data.EntityFlag.BREATHING;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class LivingEntity extends Entity implements EntityDamageable {

    public LivingEntity(EntityType<?> type, Chunk chunk, CompoundTag tag) {
        super(type, chunk, tag);
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

    protected int turtleTicks = 200;

    @Override
    protected void initEntity() {
        super.initEntity();

        if (this.namedTag.contains("HealF")) {
            this.namedTag.putFloat("Health", this.namedTag.getShort("HealF"));
            this.namedTag.remove("HealF");
        }

        if (!this.namedTag.contains("Health") || !(this.namedTag.get("Health") instanceof FloatTag)) {
            this.namedTag.putFloat("Health", this.getMaxHealth());
        }

        this.setHealth(this.namedTag.getFloat("Health"));
    }

    @Override
    public void setHealth(float health) {
        boolean wasAlive = this.isAlive();
        super.setHealth(health);
        if (this.isAlive() && !wasAlive) {
            EntityEventPacket pk = new EntityEventPacket();
            pk.entityRuntimeId = this.getUniqueId();
            pk.event = EntityEventPacket.RESPAWN;
            Server.broadcastPacket(this.hasSpawned, pk);
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putFloat("Health", this.getHealth());
    }

    public boolean hasLineOfSight(Entity entity) {
        //todo
        return true;
    }

    public void collidingWith(Entity ent) { // can override (IronGolem|Bats)
        ent.applyEntityCollision(this);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (this.attackTime > 0 || this.noDamageTicks > 0) {
            EntityDamageEvent lastCause = this.getLastDamageCause();
            if (lastCause != null && lastCause.getDamage() >= source.getDamage()) {
                return false;
            }
        }

        if (super.attack(source)) {
            if (source instanceof EntityDamageByEntityEvent) {
                Entity damager = ((EntityDamageByEntityEvent) source).getDamager();
                if (source instanceof EntityDamageByChildEntityEvent) {
                    damager = ((EntityDamageByChildEntityEvent) source).getChild();
                }

                //Critical hit
                if (damager instanceof Player && !damager.onGround) {
                    AnimatePacket animate = new AnimatePacket();
                    animate.action = AnimatePacket.Action.CRITICAL_HIT;
                    animate.entityRuntimeId = getUniqueId();

                    this.getLevel().addChunkPacket(damager.getChunkX(), damager.getChunkZ(), animate);
                    this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_ATTACK_STRONG);

                    source.setDamage(source.getDamage() * 1.5f);
                }

                if (damager.isOnFire() && !(damager instanceof Player)) {
                    this.setOnFire(2 * this.server.getDifficulty());
                }

                double deltaX = this.x - damager.x;
                double deltaZ = this.z - damager.z;
                this.knockBack(damager, source.getDamage(), deltaX, deltaZ, ((EntityDamageByEntityEvent) source).getKnockBack());
            }

            EntityEventPacket pk = new EntityEventPacket();
            pk.entityRuntimeId = this.getUniqueId();
            pk.event = this.getHealth() <= 0 ? EntityEventPacket.DEATH_ANIMATION : EntityEventPacket.HURT_ANIMATION;
            Server.broadcastPacket(this.hasSpawned, pk);

            this.attackTime = source.getAttackCooldown();

            return true;
        } else {
            return false;
        }
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

        Vector3f motion = new Vector3f(this.motionX, this.motionY, this.motionZ);

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

        if (this.level.getGameRules().get(GameRules.DO_ENTITY_DROPS)) {
            for (cn.nukkit.item.Item item : ev.getDrops()) {
                this.getLevel().dropItem(this, item);
            }
        }
    }

    @Override
    public boolean entityBaseTick() {
        return this.entityBaseTick(1);
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        try (Timing ignored = Timings.livingEntityBaseTickTimer.startTiming()) {
            boolean isBreathing = !this.isInsideOfWater();
            if (this instanceof Player && (((Player) this).isCreative() || ((Player) this).isSpectator())) {
                isBreathing = true;
            }

            if (this instanceof Player) {
                if (!isBreathing && ((Player) this).getInventory().getHelmet() instanceof ItemTurtleShell) {
                    if (turtleTicks > 0) {
                        isBreathing = true;
                        turtleTicks--;
                    }
                } else {
                    turtleTicks = 200;
                }
            }

            this.setFlag(BREATHING, isBreathing);

            boolean hasUpdate = super.entityBaseTick(tickDiff);

            if (this.isAlive()) {

                if (this.isInsideOfSolid()) {
                    hasUpdate = true;
                    this.attack(new EntityDamageEvent(this, DamageCause.SUFFOCATION, 1));
                }

                if (this.isOnLadder() || this.hasEffect(Effect.LEVITATION)) {
                    this.resetFallDistance();
                }

                if (!this.hasEffect(Effect.WATER_BREATHING) && this.isInsideOfWater()) {
                    if (this instanceof WaterAnimal || (this instanceof Player && (((Player) this).isCreative() || ((Player) this).isSpectator()))) {
                        this.setAirTicks(400);
                    } else {
                        if (turtleTicks == 0 || turtleTicks == 200) {
                            hasUpdate = true;
                            int airTicks = this.getAirTicks() - tickDiff;

                            if (airTicks <= -20) {
                                airTicks = 0;
                                this.attack(new EntityDamageEvent(this, DamageCause.DROWNING, 2));
                            }

                            setAirTicks(airTicks);
                        }
                    }
                } else {
                    if (this instanceof WaterAnimal) {
                        hasUpdate = true;
                        int airTicks = getAirTicks() - tickDiff;

                        if (airTicks <= -20) {
                            airTicks = 0;
                            this.attack(new EntityDamageEvent(this, DamageCause.SUFFOCATION, 2));
                        }

                        setAirTicks(airTicks);
                    } else {
                        int airTicks = getAirTicks();

                        if (airTicks < 400) {
                            setAirTicks(Math.min(400, airTicks + tickDiff * 5));
                        }
                    }
                }
            }

            if (this.attackTime > 0) {
                this.attackTime -= tickDiff;
            }

            if (this.riding == null) {
                for (Entity entity : level.getNearbyEntities(this.boundingBox.grow(0.20000000298023224, 0.0D, 0.20000000298023224), this)) {
                    if (entity instanceof EntityRideable) {
                        this.collidingWith(entity);
                    }
                }
            }

            // Used to check collisions with magma blocks
            Block block = this.level.getLoadedBlock((int) x, (int) y - 1, (int) z);
            if (block instanceof BlockMagma) block.onEntityCollide(this);
            return hasUpdate;
        }
    }

    public Item[] getDrops() {
        return new Item[0];
    }

    public Block[] getLineOfSight(int maxDistance) {
        return this.getLineOfSight(maxDistance, 0);
    }

    public Block[] getLineOfSight(int maxDistance, int maxLength) {
        return this.getLineOfSight(maxDistance, maxLength, new Identifier[0]);
    }

    public Block[] getLineOfSight(int maxDistance, int maxLength, Identifier[] transparent) {
        if (maxDistance > 120) {
            maxDistance = 120;
        }

        if (transparent != null && transparent.length == 0) {
            transparent = null;
        }

        List<Block> blocks = new ArrayList<>();

        Position position = getPosition();
        position.y += getEyeHeight();
        for (Vector3i pos : BlockRayTrace.of(position, getDirectionVector(), maxDistance)) {
            Block block = this.level.getLoadedBlock(pos.x, pos.y, pos.z);
            if (block == null) {
                break;
            }
            blocks.add(block);

            if (maxLength != 0 && blocks.size() > maxLength) {
                blocks.remove(0);
            }

            Identifier id = block.getId();

            if (transparent == null) {
                if (id != AIR) {
                    break;
                }
            } else {
                if (Arrays.binarySearch(transparent, id) < 0) {
                    break;
                }
            }
        }

        return blocks.toArray(new Block[0]);
    }

    public Block getTargetBlock(int maxDistance) {
        return getTargetBlock(maxDistance, new Identifier[0]);
    }

    public Block getTargetBlock(int maxDistance, Identifier[] transparent) {
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

    public int getAirTicks() {
        return this.getShortData(EntityData.AIR);
    }

    public void setAirTicks(int ticks) {
        this.setShortData(EntityData.AIR, ticks);
    }
}
