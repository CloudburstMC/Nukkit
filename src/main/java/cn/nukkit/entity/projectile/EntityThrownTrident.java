package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.entity.weather.EntityLightning;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.ProjectileHitEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.AddEntityPacket;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author PetteriM1
 * @author GoodLucky777
 */
public class EntityThrownTrident extends EntityProjectile {

    public static final int NETWORK_ID = 73;

    public static final int DATA_SOURCE_ID = 17;
    
    // NBT data
    protected Item trident;
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private Vector3 collisionPos;
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private BlockVector3 stuckToBlockPos;
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private int favoredSlot;
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private boolean isCreative;
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private boolean player;
    
    // Enchantment
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private int loyaltyLevel;
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private boolean hasChanneling;
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private int riptideLevel;
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private int impalingLevel;
    
    // Default Values
    protected float gravity = 0.04f;
    
    protected float drag = 0.01f;
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private static final Vector3 defaultCollisionPos = new Vector3(0, 0, 0);
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private static final BlockVector3 defaultStuckToBlockPos = new BlockVector3(0, 0, 0);
    
    @Override
    public int getNetworkId() {
        return NETWORK_ID;
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
        return 0.35f;
    }

    @Override
    public float getGravity() {
        return 0.04f;
    }

    @Override
    public float getDrag() {
        return 0.01f;
    }
    
    public EntityThrownTrident(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityThrownTrident(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        this(chunk, nbt, shootingEntity, false);
    }

    public EntityThrownTrident(FullChunk chunk, CompoundTag nbt, Entity shootingEntity, boolean critical) {
        super(chunk, nbt, shootingEntity);
    }
    
    @PowerNukkitOnly
    @Since("FUTURE")
    @Override
    public String getOriginalName() {
        return "Trident";
    }
    
    @Override
    protected void initEntity() {
        super.initEntity();
        
        this.closeOnCollide = false;
        
        this.hasAge = false;
        
        if (namedTag.contains("Trident")) {
            this.trident = NBTIO.getItemHelper(namedTag.getCompound("Trident"));
            this.loyaltyLevel = this.trident.getEnchantmentLevel(Enchantment.ID_TRIDENT_LOYALTY);
            this.hasChanneling = this.trident.hasEnchantment(Enchantment.ID_TRIDENT_CHANNELING);
            this.riptideLevel = this.trident.getEnchantmentLevel(Enchantment.ID_TRIDENT_RIPTIDE);
            this.impalingLevel = this.trident.getEnchantmentLevel(Enchantment.ID_TRIDENT_IMPALING);
        } else {
            this.trident = Item.get(0);
            this.loyaltyLevel = 0;
            this.hasChanneling = false;
            this.riptideLevel = 0;
            this.impalingLevel = 0;
        }
        
        if (namedTag.contains("damage")) {
            this.damage = namedTag.getDouble("damage");
        } else {
            this.damage = 8;
        }
        
        if (namedTag.contains("CollisionPos")) {
            ListTag<DoubleTag> collisionPosList = this.namedTag.getList("CollisionPos", DoubleTag.class);
            collisionPos = new Vector3(collisionPosList.get(0).data, collisionPosList.get(1).data, collisionPosList.get(2).data);
        } else {
            collisionPos = this.defaultCollisionPos.clone();
        }
        
        if (namedTag.contains("StuckToBlockPos")) {
            ListTag<IntTag> stuckToBlockPosList = this.namedTag.getList("StuckToBlockPos", IntTag.class);
            stuckToBlockPos = new BlockVector3(stuckToBlockPosList.get(0).data, stuckToBlockPosList.get(1).data, stuckToBlockPosList.get(2).data);
        } else {
            stuckToBlockPos = this.defaultStuckToBlockPos.clone();
        }
        
        if (namedTag.contains("favoredSlot")) {
            this.favoredSlot = namedTag.getInt("favoredSlot");
        } else {
            this.favoredSlot = -1;
        }
        
        if (namedTag.contains("isCreative")) {
            this.isCreative = namedTag.getBoolean("isCreative");
        } else {
            this.isCreative = false;
        }
        
        if (namedTag.contains("player")) {
            this.player = namedTag.getBoolean("player");
        } else {
            this.player = true;
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.put("Trident", NBTIO.putItemHelper(this.trident));
        this.namedTag.putList(new ListTag<DoubleTag>("CollisionPos")
            .add(new DoubleTag("0", this.collisionPos.x))
            .add(new DoubleTag("1", this.collisionPos.y))
            .add(new DoubleTag("2", this.collisionPos.z))
        );
        this.namedTag.putList(new ListTag<IntTag>("StuckToBlockPos")
            .add(new IntTag("0", this.stuckToBlockPos.x))
            .add(new IntTag("1", this.stuckToBlockPos.y))
            .add(new IntTag("2", this.stuckToBlockPos.z))
        );
        this.namedTag.putInt("favoredSlot", this.favoredSlot);
        this.namedTag.putBoolean("isCreative", this.isCreative);
        this.namedTag.putBoolean("player", this.player);
    }

    public Item getItem() {
        return this.trident != null ? this.trident.clone() : Item.get(0);
    }

    public void setItem(Item item) {
        this.trident = item.clone();
        this.loyaltyLevel = this.trident.getEnchantmentLevel(Enchantment.ID_TRIDENT_LOYALTY);
        this.hasChanneling = this.trident.hasEnchantment(Enchantment.ID_TRIDENT_CHANNELING);
        this.riptideLevel = this.trident.getEnchantmentLevel(Enchantment.ID_TRIDENT_RIPTIDE);
        this.impalingLevel = this.trident.getEnchantmentLevel(Enchantment.ID_TRIDENT_IMPALING);
    }

    public void setCritical() {
        this.setCritical(true);
    }

    public void setCritical(boolean value) {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_CRITICAL, value);
    }

    public boolean isCritical() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_CRITICAL);
    }

    @Override
    public int getResultDamage() {
        int base = super.getResultDamage();

        if (this.isCritical()) {
            base += ThreadLocalRandom.current().nextInt(base / 2 + 2);
        }

        return base;
    }

    @Override
    protected double getBaseDamage() {
        return 8;
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        this.timing.startTiming();

        if (this.isCollided && !this.hadCollision) {
            this.getLevel().addSound(this, Sound.ITEM_TRIDENT_HIT_GROUND);
        }

        boolean hasUpdate = super.onUpdate(currentTick);

        if (this.onGround || this.hadCollision) {
            this.setCritical(false);
        }
        
        if (this.noClip) {
            if (this.canReturnToShooter()) {
                Entity shooter = this.shootingEntity;
                Vector3 vector3 = new Vector3(shooter.x - this.x, shooter.y + shooter.getEyeHeight() - this.y, shooter.z - this.z);
                this.setPosition(new Vector3(this.x, this.y + vector3.y * 0.015 * ((double) loyaltyLevel), this.z));
                this.setMotion(this.getMotion().multiply(0.95).add(vector3.multiply(loyaltyLevel * 0.05)));
                hasUpdate = true;
            } else {
                if (level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS) && !this.closed) {
                    this.level.dropItem(this, this.trident);
                }
                this.close();
            }
        }
        
        this.timing.stopTiming();

        return hasUpdate;
    }

    @Override
    public void spawnTo(Player player) {
        AddEntityPacket pk = new AddEntityPacket();
        pk.type = NETWORK_ID;
        pk.entityUniqueId = this.getId();
        pk.entityRuntimeId = this.getId();
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.speedX = (float) this.motionX;
        pk.speedY = (float) this.motionY;
        pk.speedZ = (float) this.motionZ;
        pk.yaw = (float) this.yaw;
        pk.pitch = (float) this.pitch;
        pk.metadata = this.dataProperties;
        player.dataPacket(pk);

        super.spawnTo(player);
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    public void onCollideWithEntity(Entity entity) {
        if (this.noClip) {
            return;
        }
        
        this.server.getPluginManager().callEvent(new ProjectileHitEvent(this, MovingObjectPosition.fromEntity(entity)));
        float damage = this.getResultDamage();
        if (this.impalingLevel > 0 && (entity.isTouchingWater() || (entity.getLevel().isRaining() && entity.getLevel().canBlockSeeSky(entity)))) {
            damage = damage + (2.5f * (float) this.impalingLevel);
        }
        
        EntityDamageEvent ev;
        if (this.shootingEntity == null) {
            ev = new EntityDamageByEntityEvent(this, entity, DamageCause.PROJECTILE, damage);
        } else {
            ev = new EntityDamageByChildEntityEvent(this.shootingEntity, this, entity, DamageCause.PROJECTILE, damage);
        }
        entity.attack(ev);
        this.getLevel().addSound(this, Sound.ITEM_TRIDENT_HIT);
        this.hadCollision = true;
        this.setCollisionPos(this);
        this.setMotion(new Vector3(this.getMotion().getX() * -0.01, this.getMotion().getY() * -0.1, this.getMotion().getZ() * -0.01));
        
        if (this.hasChanneling) {
            if (this.level.isThundering() && this.level.canBlockSeeSky(this)) {
                Position pos = this.getPosition();
                EntityLightning lighting = new EntityLightning(pos.getChunk(), getDefaultNBT(pos));
                lighting.spawnToAll();
                this.getLevel().addSound(this, Sound.ITEM_TRIDENT_THUNDER);
            }
        }
        
        if (this.canReturnToShooter()) {
            this.getLevel().addSound(this, Sound.ITEM_TRIDENT_RETURN);
            this.setNoClip(true);
            this.hadCollision = false;
            this.setTridentRope(true);
        }
    }

    public Entity create(Object type, Position source, Object... args) {
        FullChunk chunk = source.getLevel().getChunk((int) source.x >> 4, (int) source.z >> 4);
        if (chunk == null) return null;

        CompoundTag nbt = Entity.getDefaultNBT(
                source.add(0.5, 0, 0.5), 
                null, 
                new Random().nextFloat() * 360, 0
        );

        return Entity.createEntity(type.toString(), chunk, nbt, args);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    protected void onCollideWithBlock(Position position, Vector3 motion) {
        if (this.noClip) {
            return;
        }
        
        for (Block collisionBlock : level.getCollisionBlocks(getBoundingBox().grow(0.1, 0.1, 0.1))) {
            this.setStuckToBlockPos(new BlockVector3(collisionBlock.getFloorX(), collisionBlock.getFloorY(), collisionBlock.getFloorZ()));
            if (this.canReturnToShooter()) {
                this.getLevel().addSound(this, Sound.ITEM_TRIDENT_RETURN);
                this.setNoClip(true);
                this.setTridentRope(true);
                return;
            }
            onCollideWithBlock(position, motion, collisionBlock);
        }
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Vector3 getCollisionPos() {
        return collisionPos;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setCollisionPos(Vector3 collisionPos) {
        this.collisionPos = collisionPos;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockVector3 getStuckToBlockPos() {
        return stuckToBlockPos;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setStuckToBlockPos(BlockVector3 stuckToBlockPos) {
        this.stuckToBlockPos = stuckToBlockPos;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getFavoredSlot() {
        return favoredSlot;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setFavoredSlot(int favoredSlot) {
        this.favoredSlot = favoredSlot;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isCreative() {
        return isCreative;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setCreative(boolean isCreative) {
        this.isCreative = isCreative;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isPlayer() {
        return player;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setPlayer(boolean player) {
        this.player = player;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getLoyaltyLevel() {
        return loyaltyLevel;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setLoyaltyLevel(int loyaltyLevel) {
        this.loyaltyLevel = loyaltyLevel;
        if (loyaltyLevel > 0) {
            this.trident.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_TRIDENT_LOYALTY).setLevel(loyaltyLevel));
        } else {
            // TODO: this.trident.removeEnchantment(Enchantment.ID_TRIDENT_LOYALTY);
        }
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean hasChanneling() {
        return hasChanneling;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setChanneling(boolean hasChanneling) {
        this.hasChanneling = hasChanneling;
        if (hasChanneling) {
            this.trident.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_TRIDENT_CHANNELING));
        } else {
            // TODO: this.trident.removeEnchantment(Enchantment.ID_TRIDENT_CHANNELING);
        }
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getRiptideLevel() {
        return riptideLevel;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setRiptideLevel(int riptideLevel) {
        this.riptideLevel = riptideLevel;
        if (riptideLevel > 0) {
            this.trident.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_TRIDENT_RIPTIDE).setLevel(riptideLevel));
        } else {
            // TODO: this.trident.removeEnchantment(Enchantment.ID_TRIDENT_RIPTIDE);
        }
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getImpalingLevel() {
        return impalingLevel;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setImpalingLevel(int impalingLevel) {
        this.impalingLevel = impalingLevel;
        if (impalingLevel > 0) {
            this.trident.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_TRIDENT_IMPALING).setLevel(impalingLevel));
        } else {
            // TODO: this.trident.removeEnchantment(Enchantment.ID_TRIDENT_IMPALING);
        }
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean getTridentRope() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_SHOW_TRIDENT_ROPE);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setTridentRope(boolean tridentRope) {
        if (tridentRope) {
            this.setDataProperty(new LongEntityData(DATA_OWNER_EID, this.shootingEntity.getId()));
        } else {
            this.setDataProperty(new LongEntityData(DATA_OWNER_EID, -1));
        }
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SHOW_TRIDENT_ROPE, tridentRope);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean canReturnToShooter() {
        if (this.loyaltyLevel <= 0) {
            return false;
        }
        
        if (this.getCollisionPos().equals(this.defaultCollisionPos) && this.getStuckToBlockPos().equals(this.defaultStuckToBlockPos)) {
            return false;
        }
        
        Entity shooter = this.shootingEntity;
        if (shooter != null) {
            if (shooter.isAlive() && shooter instanceof Player) {
                return !(((Player) shooter).isSpectator());
            }
        }
        return false;
    }
}
