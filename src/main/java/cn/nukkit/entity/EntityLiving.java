package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockCactus;
import cn.nukkit.block.BlockMagma;
import cn.nukkit.entity.mob.*;
import cn.nukkit.entity.passive.EntityIronGolem;
import cn.nukkit.entity.passive.EntitySkeletonHorse;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.weather.EntityWeather;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArmor;
import cn.nukkit.item.ItemTurtleShell;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.BlockIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author MagicDroidX
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

    protected int attackTime;
    protected int knockBackTime;
    private float currentDamage;
    protected float movementSpeed = 0.1f;
    protected int turtleTicks;
    private boolean blocking;
    private boolean spinAttack;

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

        this.health = this.namedTag.getFloat("Health");
    }

    @Override
    public void setHealth(float health) {
        boolean wasAlive = this.isAlive();
        super.setHealth(health);
        if (this.isAlive() && !wasAlive) {
            EntityEventPacket pk = new EntityEventPacket();
            pk.eid = this.getId();
            pk.event = EntityEventPacket.RESPAWN;
            Server.broadcastPacket(this.hasSpawned.values(), pk);
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putFloat("Health", this.getHealth());
    }

    public boolean hasLineOfSight(Entity entity) {
        return true;
    }

    public void collidingWith(Entity ent) {
        ent.applyEntityCollision(this);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (this.noDamageTicks > 0) {
            return false;
        }

        float unmodifiedBaseDamage = source.getDamage();

        if (this.attackTime > 0) {
            if (unmodifiedBaseDamage > this.currentDamage) {
                source.setDamage(Math.max(0, unmodifiedBaseDamage - this.currentDamage)); // https://minecraft.fandom.com/wiki/Damage#Immunity
            } else {
                return false;
            }
        }

        if (this.blockedByShield(source)) {
            if (unmodifiedBaseDamage > this.currentDamage) {
                this.currentDamage = unmodifiedBaseDamage;
            }
            return false;
        }

        boolean attacked = super.attack(source);
        if (attacked) {
            if (source instanceof EntityDamageByEntityEvent) {
                Entity damager = ((EntityDamageByEntityEvent) source).getDamager();
                if (source instanceof EntityDamageByChildEntityEvent) {
                    damager = ((EntityDamageByChildEntityEvent) source).getChild();
                }

                if (damager.isOnFire() && !(damager instanceof Player)) {
                    this.setOnFire(this.server.getDifficulty() << 1);
                }

                double deltaX = this.x - damager.x;
                double deltaZ = this.z - damager.z;
                this.knockBack(damager, source.getDamage(), deltaX, deltaZ, ((EntityDamageByEntityEvent) source).getKnockBack());
            }

            EntityEventPacket pk = new EntityEventPacket();
            pk.eid = this.getId();
            pk.event = this.getHealth() < 1 ? EntityEventPacket.DEATH_ANIMATION : EntityEventPacket.HURT_ANIMATION;
            Server.broadcastPacket(this.hasSpawned.values(), pk);
        }

        if (!source.isCancelled()) { // attacked == false can also mean a totem was used
            this.updateAttackTime(source);
            if (unmodifiedBaseDamage > this.currentDamage) {
                this.currentDamage = unmodifiedBaseDamage;
            }
            this.scheduleUpdate();
        }

        return attacked;
    }

    protected boolean blockedByShield(EntityDamageEvent source) {
        if (!this.isBlocking()) {
            return false;
        }

        Entity damager = source instanceof EntityDamageByChildEntityEvent ? ((EntityDamageByChildEntityEvent) source).getChild() : source instanceof EntityDamageByEntityEvent ? ((EntityDamageByEntityEvent) source).getDamager() : null;
        if (damager == null || damager instanceof EntityWeather) {
            return false;
        }

        Vector3 entityPos = damager.getPosition();
        Vector3 direction = this.getDirectionVector();
        Vector3 normalizedVector = this.getPosition().subtract(entityPos).normalize();
        boolean blocked = (normalizedVector.x * direction.x) + (normalizedVector.z * direction.z) < 0.0;
        boolean knockBack = !(damager instanceof EntityProjectile);
        EntityDamageBlockedEvent event = new EntityDamageBlockedEvent(this, source, knockBack, true);
        if (!blocked || !source.canBeReducedByArmor() || damager instanceof EntityProjectile && ((EntityProjectile) damager).piercing > 0) {
            event.setCancelled();
        }

        getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        if (event.getKnockBackAttacker() && damager instanceof EntityLiving) {
            double deltaX = damager.getX() - this.getX();
            double deltaZ = damager.getZ() - this.getZ();
            this.updateAttackTime(source);
            ((EntityLiving) damager).knockBack(this, 0, deltaX, deltaZ, 0.25);
        }

        this.onBlock(damager, event, source);
        return true;
    }

    private void updateAttackTime(EntityDamageEvent source) {
        if (this.attackTime < 1) { // Does not reset the immunity period
            this.attackTime = source.getAttackCooldown();
        }
    }

    protected void onBlock(Entity damager, EntityDamageBlockedEvent event, EntityDamageEvent source) {
        if (event.getAnimation()) {
            this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_ITEM_SHIELD_BLOCK);
        }
    }

    public void knockBack(Entity attacker, double damage, double x, double z) {
        this.knockBack(attacker, damage, x, z, 0.3);
    }

    public void knockBack(Entity attacker, double damage, double x, double z, double base) {
        double f = Math.sqrt(x * x + z * z);
        if (f <= 0) {
            return;
        }

        if (this instanceof Player) {
            int netheritePieces = 0;
            for (Item armor : ((Player) this).getInventory().getArmorContents()) {
                if (armor.getTier() == ItemArmor.TIER_NETHERITE) {
                    netheritePieces++;
                }
            }
            if (netheritePieces > 0) {
                base *= 1 - 0.1 * netheritePieces;
            }
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

        this.resetFallDistance();

        this.setMotion(motion);

        this.knockBackTime = 10;
    }

    @Override
    public void kill() {
        if (!this.isAlive()) {
            return;
        }
        super.kill();
        EntityDeathEvent ev = new EntityDeathEvent(this, this.getDrops());
        this.server.getPluginManager().callEvent(ev);

        // Monster Hunter Achievement
        int id = ev.getEntity().getNetworkId();
        if (id == EntityEnderman.NETWORK_ID || id == EntityZombiePigman.NETWORK_ID || id == EntitySpider.NETWORK_ID || id == EntityCaveSpider.NETWORK_ID) {
            if (ev.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                Entity damager = ((EntityDamageByEntityEvent) ev.getEntity().getLastDamageCause()).getDamager();
                if (damager instanceof Player) {
                    ((Player) damager).awardAchievement("killEnemy");
                }
            }
        }

        if (this.level.getGameRules().getBoolean(GameRule.DO_MOB_LOOT) && this.lastDamageCause != null && DamageCause.VOID != this.lastDamageCause.getCause()) {
            if (ev.getEntity() instanceof BaseEntity) {
                BaseEntity baseEntity = (BaseEntity) ev.getEntity();
                if (baseEntity.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                    Entity damager = ((EntityDamageByEntityEvent) baseEntity.getLastDamageCause()).getDamager();
                    if (damager instanceof Player) {
                        this.getLevel().dropExpOrb(this, baseEntity.getKillExperience());

                        if (!this.dropsOnNaturalDeath()) {
                            for (cn.nukkit.item.Item item : ev.getDrops()) {
                                this.getLevel().dropItem(this, item);
                            }
                        }
                    }
                }
            }

            if (this.dropsOnNaturalDeath()) {
                for (cn.nukkit.item.Item item : ev.getDrops()) {
                    this.getLevel().dropItem(this, item);
                }
            }
        }
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        boolean inWater = this.isSubmerged();

        if (this instanceof Player && !this.closed) {
            Player p = (Player) this;
            boolean isBreathing = !inWater;

            PlayerInventory inv = p.getInventory();
            if (isBreathing && inv != null && inv.getHelmetFast() instanceof ItemTurtleShell) {
                turtleTicks = 200;
            } else if (turtleTicks > 0) {
                isBreathing = true;
                turtleTicks--;
            }

            if (p.isCreative() || p.isSpectator()) {
                isBreathing = true;
            }

            this.setDataFlagSelfOnly(DATA_FLAGS, DATA_FLAG_BREATHING, isBreathing);
        }

        boolean hasUpdate = super.entityBaseTick(tickDiff);

        if (this.isAlive()) {
            if (this.isInsideOfSolid()) {
                hasUpdate = true;
                this.attack(new EntityDamageEvent(this, DamageCause.SUFFOCATION, 1));
            }

            if (this.hasEffect(Effect.LEVITATION) || this.hasEffect(Effect.SLOW_FALLING)) {
                this.resetFallDistance();
            }

            if (inWater && !this.hasEffect(Effect.WATER_BREATHING) && !this.hasEffect(Effect.CONDUIT_POWER)) {
                if (this instanceof EntitySwimming || this instanceof EntityDrowned || this instanceof EntitySkeletonHorse || this instanceof EntityIronGolem ||
                        (this instanceof Player && (((Player) this).isCreative() || ((Player) this).isSpectator()))) {
                    this.setAirTicks(400);
                } else {
                    if (turtleTicks == 0) {
                        hasUpdate = true;
                        int airTicks = this.getAirTicks() - tickDiff;

                        if (airTicks <= -20) {
                            airTicks = 0;
                            if (!(this instanceof Player) || level.getGameRules().getBoolean(GameRule.DROWNING_DAMAGE)) {
                                this.attack(new EntityDamageEvent(this, DamageCause.DROWNING, 2));
                            }
                        }

                        this.setAirTicks(airTicks);
                    }
                }
            } else {
                if (this instanceof EntitySwimming) {
                    hasUpdate = true;
                    int airTicks = this.getAirTicks() - tickDiff;

                    if (airTicks <= -20) {
                        airTicks = 0;
                        this.attack(new EntityDamageEvent(this, DamageCause.SUFFOCATION, 2));
                    }

                    this.setAirTicks(airTicks);
                } else {
                    int airTicks = getAirTicks();
                    if (airTicks < 400) {
                        setAirTicks(Math.min(400, airTicks + tickDiff * 5));
                    }
                }
            }

            // Check collisions with blocks
            if ((this instanceof Player || this instanceof BaseEntity) && this.riding == null && this.age % (this instanceof Player ? 2 : 10) == 0) {
                int floorY = NukkitMath.floorDouble(this.y - 0.25);
                if (floorY != getFloorY()) {
                    Block block = this.level.getBlock(this.chunk, getFloorX(), floorY, getFloorZ(), false);
                    if (block instanceof BlockCactus) {
                        block.onEntityCollide(this);
                    } else if (block instanceof BlockMagma) {
                        block.onEntityCollide(this);
                    }
                }
            }

            if (this.attackTime > 0) {
                this.attackTime -= tickDiff;
                if (this.attackTime < 1) {
                    this.currentDamage = 0;
                }
                hasUpdate = true;
            }

            if (this.knockBackTime > 0) {
                this.knockBackTime -= tickDiff;
            }

            if (this.riding == null && this.age % 2 == 1 && !this.closed && this.isAlive()) {
                Entity[] e = level.getNearbyEntities(this.boundingBox.grow(0.20000000298023224, 0.0D, 0.20000000298023224), this);
                for (Entity entity : e) {
                    if (entity instanceof EntityRideable && !entity.closed && entity.isAlive()) {
                        this.collidingWith(entity);
                    }
                }
            }
        }

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
        return this.getLineOfSight(maxDistance, maxLength, transparent.keySet().toArray(new Integer[0]));
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

        return blocks.toArray(new Block[0]);
    }

    public Block getTargetBlock(int maxDistance) {
        return getTargetBlock(maxDistance, new Integer[]{});
    }

    @Deprecated
    public Block getTargetBlock(int maxDistance, Map<Integer, Object> transparent) {
        return getTargetBlock(maxDistance, transparent.keySet().toArray(new Integer[0]));
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
        } catch (Exception ignored) {}

        return null;
    }

    public void setMovementSpeed(float speed) {
        this.movementSpeed = speed;
    }

    public float getMovementSpeed() {
        return this.movementSpeed;
    }
    
    public int getAirTicks() {
        return this.airTicks;
    }

    public void setAirTicks(int ticks) {
        this.airTicks = ticks;
    }

    public boolean isBlocking() {
        return this.blocking;
    }

    public void setBlocking(boolean value) {
        if (this.blocking != value) {
            this.blocking = value;
            this.setDataFlag(DATA_FLAGS_EXTENDED, DATA_FLAG_BLOCKING, value);
        }
    }

    public boolean dropsOnNaturalDeath() {
        return true;
    }

    public boolean isSpinAttack() {
        return this.spinAttack;
    }

    public void setSpinAttack(boolean value) {
        if (this.spinAttack != value) {
            this.spinAttack = value;
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_SPIN_ATTACK, value);
        }
    }
}
