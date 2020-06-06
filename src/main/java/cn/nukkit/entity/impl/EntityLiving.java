package cn.nukkit.entity.impl;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.block.BlockMagma;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityDamageable;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.Rideable;
import cn.nukkit.entity.impl.passive.EntityWaterAnimal;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTurtleShell;
import cn.nukkit.level.Location;
import cn.nukkit.level.gamerule.GameRules;
import cn.nukkit.math.BlockRayTrace;
import cn.nukkit.player.Player;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.Identifier;
import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import com.nukkitx.math.vector.Vector2f;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.protocol.bedrock.data.EntityData;
import com.nukkitx.protocol.bedrock.data.EntityEventType;
import com.nukkitx.protocol.bedrock.data.SoundEvent;
import com.nukkitx.protocol.bedrock.packet.AnimatePacket;
import com.nukkitx.protocol.bedrock.packet.EntityEventPacket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cn.nukkit.block.BlockIds.AIR;
import static com.nukkitx.protocol.bedrock.data.EntityFlag.BREATHING;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityLiving extends BaseEntity implements EntityDamageable {

    public EntityLiving(EntityType<?> type, Location location) {
        super(type, location);
    }

    @Override
    public float getGravity() {
        return 0.08f;
    }

    @Override
    public float getDrag() {
        return 0.02f;
    }

    protected int attackTime = 0;

    protected boolean invisible = false;

    protected float movementSpeed = 0.1f;

    protected int turtleTicks = 200;

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForFloat("Health", this::setHealth);
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        tag.floatTag("Health", this.getHealth());
    }

    @Override
    public void setHealth(float health) {
        boolean wasAlive = this.isAlive();
        super.setHealth(health);
        if (this.isAlive() && !wasAlive) {
            EntityEventPacket pk = new EntityEventPacket();
            pk.setRuntimeEntityId(this.getRuntimeId());
            pk.setType(EntityEventType.RESPAWN);
            Server.broadcastPacket(this.hasSpawned, pk);
        }
    }

    public boolean hasLineOfSight(Entity entity) {
        //todo
        return true;
    }

    public void collidingWith(Entity ent) { // can override (IronGolem|Bats)
        ent.onEntityCollision(this);
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
                if (damager instanceof Player && !damager.isOnGround()) {
                    AnimatePacket animate = new AnimatePacket();
                    animate.setAction(AnimatePacket.Action.CRITICAL_HIT);
                    animate.setRuntimeEntityId(this.getRuntimeId());

                    this.getLevel().addChunkPacket(damager.getPosition(), animate);
                    this.getLevel().addLevelSoundEvent(this.getPosition(), SoundEvent.ATTACK_STRONG);

                    source.setDamage(source.getDamage() * 1.5f);
                }

                if (damager.isOnFire() && !(damager instanceof Player)) {
                    this.setOnFire(2 * this.server.getDifficulty().ordinal());
                }

                Vector2f diff = this.getPosition().sub(damager.getPosition()).toVector2(true);

                this.knockBack(damager, ((EntityDamageByEntityEvent) source).getKnockBack(), diff.getX(), diff.getY());
            }

            EntityEventPacket pk = new EntityEventPacket();
            pk.setRuntimeEntityId(this.getRuntimeId());
            pk.setType(this.getHealth() <= 0 ? EntityEventType.DEATH_ANIMATION : EntityEventType.HURT_ANIMATION);
            Server.broadcastPacket(this.hasSpawned, pk);

            this.attackTime = source.getAttackCooldown();

            return true;
        } else {
            return false;
        }
    }

    public void knockBack(Entity attacker, float strength, float diffX, float diffZ) {
        //TODO: knockback resistance
        float f = 1f / (float) Math.sqrt(diffX * diffX + diffZ * diffZ);

        diffX = (diffX * f) * (0.4f * strength);
        diffZ = (diffZ * f) * (0.4f * strength);

        Vector3f motion = Vector3f.from(diffX, 0.4f, diffZ);

        if (motion.getY() > 0.4f) {
            motion = Vector3f.from(motion.getX(), 0.4f, motion.getZ());
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

        if (this.getLevel().getGameRules().get(GameRules.DO_ENTITY_DROPS)) {
            for (cn.nukkit.item.Item item : ev.getDrops()) {
                this.getLevel().dropItem(this.getPosition(), item);
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

            this.data.setFlag(BREATHING, isBreathing);

            boolean hasUpdate = super.entityBaseTick(tickDiff);

            if (this.isAlive()) {

                if (this.isInsideOfSolid()) {
                    hasUpdate = true;
                    this.attack(new EntityDamageEvent(this, DamageCause.SUFFOCATION, 1));
                }

                Identifier block = this.getLevel().getBlock(this.getPosition()).getId();
                boolean ignore = block == BlockIds.LADDER || block == BlockIds.VINE || block == BlockIds.WEB;
                if (ignore || this.hasEffect(Effect.LEVITATION)) {
                    this.resetFallDistance();
                }

                if (!this.hasEffect(Effect.WATER_BREATHING) && this.isInsideOfWater()) {
                    if (this instanceof EntityWaterAnimal || (this instanceof Player && (((Player) this).isCreative() || ((Player) this).isSpectator()))) {
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
                    if (this instanceof EntityWaterAnimal) {
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

            if (this.vehicle == null) {
                for (Entity entity : this.getLevel().getNearbyEntities(this.boundingBox.grow(0.2f, 0, 0.2f), this)) {
                    if (entity instanceof Rideable) {
                        this.collidingWith(entity);
                    }
                }
            }

            // Used to check collisions with magma blocks
            Block block = this.getLevel().getLoadedBlock(this.getPosition().sub(0, 1, 0));
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

        Vector3f position = getPosition().add(0, this.getEyeHeight(), 0);
        for (Vector3i pos : BlockRayTrace.of(position, getDirectionVector(), maxDistance)) {
            Block block = this.getLevel().getLoadedBlock(pos);
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
        return this.data.getShort(EntityData.AIR);
    }

    public void setAirTicks(int ticks) {
        this.data.setShort(EntityData.AIR, ticks);
    }
}
