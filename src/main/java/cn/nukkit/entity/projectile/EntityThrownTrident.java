package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.entity.weather.EntityLightning;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.ProjectileHitEvent;
import cn.nukkit.event.weather.LightningStrikeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class EntityThrownTrident extends EntityProjectile {

    public static final int NETWORK_ID = 73;

    protected Item trident;

    protected int pickupMode;

    private Vector3 collisionPos;

    private BlockVector3 stuckToBlockPos;

    private int favoredSlot;

    private boolean player;

    private int loyaltyLevel;

    private boolean hasChanneling;
    private boolean didHit;

    private int impalingLevel;

    private static final Vector3 defaultCollisionPos = new Vector3(0, 0, 0);

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
        super(chunk, nbt, shootingEntity);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.pickupMode = namedTag.contains("pickup") ? namedTag.getByte("pickup") : PICKUP_ANY;

        if (namedTag.contains("Trident")) {
            this.trident = NBTIO.getItemHelper(namedTag.getCompound("Trident"));
            this.loyaltyLevel = this.trident.getEnchantmentLevel(Enchantment.ID_TRIDENT_LOYALTY);
            this.hasChanneling = this.trident.hasEnchantment(Enchantment.ID_TRIDENT_CHANNELING);
            this.impalingLevel = this.trident.getEnchantmentLevel(Enchantment.ID_TRIDENT_IMPALING);
        } else {
            this.trident = Item.get(0);
            this.loyaltyLevel = 0;
            this.hasChanneling = false;
            this.impalingLevel = 0;
        }

        if (namedTag.contains("CollisionPos")) {
            ListTag<DoubleTag> collisionPosList = this.namedTag.getList("CollisionPos", DoubleTag.class);
            collisionPos = new Vector3(collisionPosList.get(0).data, collisionPosList.get(1).data, collisionPosList.get(2).data);
        } else {
            collisionPos = defaultCollisionPos.clone();
        }

        if (namedTag.contains("StuckToBlockPos")) {
            ListTag<IntTag> stuckToBlockPosList = this.namedTag.getList("StuckToBlockPos", IntTag.class);
            stuckToBlockPos = new BlockVector3(stuckToBlockPosList.get(0).data, stuckToBlockPosList.get(1).data, stuckToBlockPosList.get(2).data);
        } else {
            stuckToBlockPos = defaultStuckToBlockPos.clone();
        }

        if (namedTag.contains("favoredSlot")) {
            this.favoredSlot = namedTag.getInt("favoredSlot");
        } else {
            this.favoredSlot = -1;
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
        this.namedTag.putByte("pickup", this.pickupMode);
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
        this.namedTag.putBoolean("player", this.player);
    }

    public Item getItem() {
        return this.trident != null ? this.trident.clone() : Item.get(0);
    }

    public void setItem(Item item) {
        this.trident = item.clone();
        this.loyaltyLevel = this.trident.getEnchantmentLevel(Enchantment.ID_TRIDENT_LOYALTY);
        this.hasChanneling = this.trident.hasEnchantment(Enchantment.ID_TRIDENT_CHANNELING);
        this.impalingLevel = this.trident.getEnchantmentLevel(Enchantment.ID_TRIDENT_IMPALING);
    }

    @Override
    protected double getBaseDamage() {
        return 8;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        if (this.age > 1200 && this.pickupMode < 1) { // On Bedrock tridents shouldn't despawn
            this.close();
            return false;
        }

        boolean hasUpdate = super.onUpdate(currentTick);

        if (this.noClip) {
            if (this.canReturnToShooter()) {
                Entity shooter = this.shootingEntity;
                Vector3 vector3 = new Vector3(shooter.x - this.x, shooter.y + shooter.getEyeHeight() - this.y, shooter.z - this.z);
                this.setPosition(new Vector3(this.x, this.y + vector3.y * 0.015 * ((double) loyaltyLevel), this.z));
                this.setMotion(this.getMotion().multiply(0.95).add(vector3.multiply(loyaltyLevel * 0.05)));
                hasUpdate = true;
            } else {
                if (!this.closed && level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
                    this.level.dropItem(this, this.trident);
                }
                this.close();
            }
        }

        return hasUpdate;
    }

    @Override
    public void onCollideWithEntity(Entity entity) {
        if (this.noClip) {
            return;
        }

        this.server.getPluginManager().callEvent(new ProjectileHitEvent(this, MovingObjectPosition.fromEntity(entity)));
        float damage = this.getResultDamage();

        if (this.impalingLevel > 0 && (entity.isInsideOfWater() || (entity.getLevel().isRaining() && entity.canSeeSky()))) {
            damage = damage + (2.5f * (float) this.impalingLevel);
        }

        EntityDamageEvent ev;
        if (this.shootingEntity == null) {
            ev = new EntityDamageByEntityEvent(this, entity, DamageCause.PROJECTILE, damage, knockBack);
        } else {
            ev = new EntityDamageByChildEntityEvent(this.shootingEntity, this, entity, DamageCause.PROJECTILE, damage, knockBack);
        }
        entity.attack(ev);
        this.hadCollision = true;
        this.onHit();
        this.setCollisionPos(this);
        this.setMotion(new Vector3(this.getMotion().getX() * -0.01, this.getMotion().getY() * -0.1, this.getMotion().getZ() * -0.01));

        if (this.hasChanneling && !this.didHit) {
            this.didHit = true;
            if (level.isThundering() && this.canSeeSky()) {
                EntityLightning bolt = (EntityLightning) Entity.createEntity(EntityLightning.NETWORK_ID, this.getChunk(), getDefaultNBT(this));
                LightningStrikeEvent strikeEvent = new LightningStrikeEvent(level, bolt);
                server.getPluginManager().callEvent(strikeEvent);
                if (!strikeEvent.isCancelled()) {
                    bolt.spawnToAll();
                    level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_ITEM_TRIDENT_THUNDER);
                } else {
                    bolt.setEffect(false);
                }
            }
        }

        if (this.canReturnToShooter()) {
            this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_ITEM_TRIDENT_RETURN);
            this.noClip = true;
            this.hadCollision = false;
            this.setRope(true);
        }
    }

    @Override
    public void onHit() {
        this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_ITEM_TRIDENT_HIT);
    }

    @Override
    public void onHitGround(Vector3 moveVector) {
        if (this.noClip) {
            return;
        }
        super.onHitGround(moveVector);

        this.setStuckToBlockPos(new BlockVector3(moveVector.getFloorX(), moveVector.getFloorY(), moveVector.getFloorZ()));
        if (this.canReturnToShooter()) {
            this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_ITEM_TRIDENT_RETURN);
            this.noClip = true;
            this.setRope(true);
        }
    }

    public Vector3 getCollisionPos() {
        return collisionPos;
    }

    public void setCollisionPos(Vector3 collisionPos) {
        this.collisionPos = collisionPos;
    }

    public BlockVector3 getStuckToBlockPos() {
        return stuckToBlockPos;
    }

    public void setStuckToBlockPos(BlockVector3 stuckToBlockPos) {
        this.stuckToBlockPos = stuckToBlockPos;
    }

    public int getFavoredSlot() {
        return favoredSlot;
    }

    public void setFavoredSlot(int favoredSlot) {
        this.favoredSlot = favoredSlot;
    }

    public boolean shotByPlayer() {
        return player;
    }

    public void setShotByPlayer(boolean player) {
        this.player = player;
    }

    public void setRope(boolean tridentRope) {
        if (tridentRope) {
            this.setDataProperty(new LongEntityData(DATA_OWNER_EID, this.shootingEntity.getId()));
        } else {
            this.setDataProperty(new LongEntityData(DATA_OWNER_EID, -1));
        }
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SHOW_TRIDENT_ROPE, tridentRope);
    }

    private boolean canReturnToShooter() {
        if (this.loyaltyLevel <= 0) {
            return false;
        }

        if (this.getCollisionPos().equals(defaultCollisionPos) && this.getStuckToBlockPos().equals(defaultStuckToBlockPos)) {
            return false;
        }

        Entity shooter = this.shootingEntity;
        if (shooter != null) {
            return shooter instanceof Player && shooter.isAlive() && !shooter.isClosed() && shooter.getLevel().getId() == this.getLevel().getId() && !(((Player) shooter).isSpectator());
        }
        return false;
    }

    public int getPickupMode() {
        return this.pickupMode;
    }

    public void setPickupMode(int pickupMode) {
        this.pickupMode = pickupMode;
    }
}
