package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.ProjectileHitEvent;
import cn.nukkit.event.player.PlayerFishEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.randomitem.Fishing;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.BubbleParticle;
import cn.nukkit.level.particle.WaterParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.EntityEventPacket;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


/**
 * @author PetteriM1
 */
public class EntityFishingHook extends EntityProjectile {

    public static final int NETWORK_ID = 77;

    public static final int WAIT_CHANCE = 120;
    public static final int CHANCE = 40;

    public boolean chance = false;
    public int waitChance = WAIT_CHANCE * 2;
    public boolean attracted = false;
    public int attractTimer = 0;
    public boolean caught = false;
    public int coughtTimer = 0;

    public Vector3 fish = null;

    public Item rod = null;

    public EntityFishingHook(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityFishingHook(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        // https://github.com/PowerNukkit/PowerNukkit/issues/267
        if (age > 0) {
            close();
        }
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.2f;
    }

    @Override
    public float getLength() {
        return 0.2f;
    }

    @Override
    public float getHeight() {
        return 0.2f;
    }

    @Override
    public float getGravity() {
        return 0.05f;
    }

    @Override
    public float getDrag() {
        return 0.04f;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        boolean hasUpdate = false;
        long target = getDataPropertyLong(DATA_TARGET_EID);
        if (target != 0L) {
            Entity entity = getLevel().getEntity(target);
            if (entity == null || !entity.isAlive()) {
                setDataProperty(new LongEntityData(DATA_TARGET_EID, 0L));
            } else {
                Vector3f offset = entity.getMountedOffset(this);
                setPosition(new Vector3(entity.x + offset.x, entity.y + offset.y, entity.z + offset.z));
            }
            hasUpdate = true;
        }

        hasUpdate |= super.onUpdate(currentTick);
        if (hasUpdate) {
            return false;
        }

        if (this.isInsideOfWater()) {
            this.motionX = 0;
            this.motionY -= getGravity() * -0.04;
            this.motionZ = 0;
            hasUpdate = true;
        } else if (this.isCollided && this.keepMovement) {
            this.motionX = 0;
            this.motionY = 0;
            this.motionZ = 0;
            this.keepMovement = false;
            hasUpdate = true;
        }

        Random random = new Random();

        if (this.isInsideOfWater()) {
            if (!this.attracted) {
                if (this.waitChance > 0) {
                    --this.waitChance;
                }
                if (this.waitChance == 0) {
                    if (random.nextInt(100) < 90) {
                        this.attractTimer = (random.nextInt(40) + 20);
                        this.spawnFish();
                        this.caught = false;
                        this.attracted = true;
                    } else {
                        this.waitChance = WAIT_CHANCE;
                    }
                }
            } else if (!this.caught) {
                if (this.attractFish()) {
                    this.coughtTimer = (random.nextInt(20) + 30);
                    this.fishBites();
                    this.caught = true;
                }
            } else {
                if (this.coughtTimer > 0) {
                    --this.coughtTimer;
                }
                if (this.coughtTimer == 0) {
                    this.attracted = false;
                    this.caught = false;
                    this.waitChance = WAIT_CHANCE * 3;
                }
            }
        }

        return hasUpdate;
    }

    public int getWaterHeight() {
        for (int y = this.getFloorY(); y < 256; y++) {
            int id = this.level.getBlockIdAt(this.getFloorX(), y, this.getFloorZ());
            if (id == Block.AIR) {
                return y;
            }
        }
        return this.getFloorY();
    }

    public void fishBites() {
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = this.getId();
        pk.event = EntityEventPacket.FISH_HOOK_HOOK;
        Server.broadcastPacket(this.getViewers().values(), pk);

        EntityEventPacket bubblePk = new EntityEventPacket();
        bubblePk.eid = this.getId();
        bubblePk.event = EntityEventPacket.FISH_HOOK_BUBBLE;
        Server.broadcastPacket(this.getViewers().values(), bubblePk);

        EntityEventPacket teasePk = new EntityEventPacket();
        teasePk.eid = this.getId();
        teasePk.event = EntityEventPacket.FISH_HOOK_TEASE;
        Server.broadcastPacket(this.getViewers().values(), teasePk);

        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            this.level.addParticle(new BubbleParticle(this.setComponents(
                    this.x + random.nextDouble() * 0.5 - 0.25,
                    this.getWaterHeight(),
                    this.z + random.nextDouble() * 0.5 - 0.25
            )));
        }
    }

    public void spawnFish() {
        Random random = new Random();
        this.fish = new Vector3(
                this.x + (random.nextDouble() * 1.2 + 1) * (random.nextBoolean() ? -1 : 1),
                this.getWaterHeight(),
                this.z + (random.nextDouble() * 1.2 + 1) * (random.nextBoolean() ? -1 : 1)
        );
    }

    public boolean attractFish() {
        double multiply = 0.1;
        this.fish.setComponents(
                this.fish.x + (this.x - this.fish.x) * multiply,
                this.fish.y,
                this.fish.z + (this.z - this.fish.z) * multiply
        );
        if (new Random().nextInt(100) < 85) {
            this.level.addParticle(new WaterParticle(this.fish));
        }
        double dist = Math.abs(Math.sqrt(this.x * this.x + this.z * this.z) - Math.sqrt(this.fish.x * this.fish.x + this.fish.z * this.fish.z));
        if (dist < 0.15) {
            return true;
        }
        return false;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "May create custom EntityItem")
    public void reelLine() {
        if (this.shootingEntity instanceof Player && this.caught) {
            Player player = (Player) this.shootingEntity;
            Item item = Fishing.getFishingResult(this.rod);
            int experience = ThreadLocalRandom.current().nextInt(3) + 1;
            Vector3 motion = player.subtract(this).multiply(0.1);
            motion.y += Math.sqrt(player.distance(this)) * 0.08;

            PlayerFishEvent event = new PlayerFishEvent(player, this, item, experience, motion);
            this.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                EntityItem itemEntity = (EntityItem) Entity.createEntity(EntityItem.NETWORK_ID,
                        this.level.getChunk((int) this.x >> 4, (int) this.z >> 4, true),
                    Entity.getDefaultNBT(
                            new Vector3(this.x, this.getWaterHeight(), this.z), 
                            event.getMotion(), ThreadLocalRandom.current().nextFloat() * 360, 
                            0
                    ).putCompound("Item", NBTIO.putItemHelper(event.getLoot()))
                            .putShort("Health", 5)
                            .putShort("PickupDelay", 1));

                if (itemEntity != null) {
                    itemEntity.setOwner(player.getName());
                    itemEntity.spawnToAll();
                    player.addExperience(event.getExperience());
                }
            }
        }
        this.close();
    }

    @Override
    public void spawnTo(Player player) {
        AddEntityPacket pk = new AddEntityPacket();
        pk.entityRuntimeId = this.getId();
        pk.entityUniqueId = this.getId();
        pk.type = NETWORK_ID;
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.speedX = (float) this.motionX;
        pk.speedY = (float) this.motionY;
        pk.speedZ = (float) this.motionZ;
        pk.yaw = (float) this.yaw;
        pk.pitch = (float) this.pitch;

        long ownerId = -1;
        if (this.shootingEntity != null) {
            ownerId = this.shootingEntity.getId();
        }
        pk.metadata = this.dataProperties.putLong(DATA_OWNER_EID, ownerId);
        player.dataPacket(pk);
        super.spawnTo(player);
    }

    @Override
    public boolean canCollide() {
        return getDataPropertyLong(DATA_TARGET_EID) == 0L;
    }

    @Override
    public void onCollideWithEntity(Entity entity) {
        this.server.getPluginManager().callEvent(new ProjectileHitEvent(this, MovingObjectPosition.fromEntity(entity)));
        float damage = this.getResultDamage();

        EntityDamageEvent ev;
        if (this.shootingEntity == null) {
            ev = new EntityDamageByEntityEvent(this, entity, DamageCause.PROJECTILE, damage);
        } else {
            ev = new EntityDamageByChildEntityEvent(this.shootingEntity, this, entity, DamageCause.PROJECTILE, damage);
        }

        if (entity.attack(ev)) {
            setDataProperty(new LongEntityData(DATA_TARGET_EID, entity.getId()));
        }
    }
    
    
    @Override
    public String getName() {
        return "Fishing Hook";
    }
}
