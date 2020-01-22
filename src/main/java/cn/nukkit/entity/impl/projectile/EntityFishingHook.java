package cn.nukkit.entity.impl.projectile;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.EntityTypes;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.entity.misc.DroppedItem;
import cn.nukkit.entity.projectile.FishingHook;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.ProjectileHitEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.randomitem.Fishing;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.particle.BubbleParticle;
import cn.nukkit.level.particle.WaterParticle;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.player.Player;
import cn.nukkit.registry.EntityRegistry;
import cn.nukkit.utils.Identifier;

import javax.annotation.Nullable;
import java.util.Random;

import static cn.nukkit.block.BlockIds.AIR;


/**
 * Created by PetteriM1
 */
public class EntityFishingHook extends EntityProjectile implements FishingHook {

    public static final int WAIT_CHANCE = 120;
    public static final int CHANCE = 40;

    public boolean chance = false;
    public int waitChance = WAIT_CHANCE * 2;
    public boolean attracted = false;
    public int attractTimer = 0;
    public boolean caught = false;
    public int coughtTimer = 0;

    public Vector3f fish = null;

    private Item rod;

    public EntityFishingHook(EntityType<FishingHook> type, Chunk chunk, CompoundTag nbt) {
        this(type, chunk, nbt, null);
    }

    public EntityFishingHook(EntityType<FishingHook> type, Chunk chunk, CompoundTag nbt, BaseEntity shootingEntity) {
        super(type, chunk, nbt, shootingEntity);
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
        return 0.07f;
    }

    @Override
    public float getDrag() {
        return 0.05f;
    }

    @Nullable
    public Item getRod() {
        return rod;
    }

    public void setRod(@Nullable Item rod) {
        this.rod = rod;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        boolean hasUpdate = super.onUpdate(currentTick);
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
            Identifier id = this.level.getBlockIdAt(this.getFloorX(), y, this.getFloorZ());
            if (id == AIR) {
                return y;
            }
        }
        return this.getFloorY();
    }

    public void fishBites() {
        EntityEventPacket pk = new EntityEventPacket();
        pk.entityRuntimeId = this.getUniqueId();
        pk.event = EntityEventPacket.FISH_HOOK_HOOK;
        Server.broadcastPacket(this.level.getPlayers().values(), pk);

        EntityEventPacket bubblePk = new EntityEventPacket();
        bubblePk.entityRuntimeId = this.getUniqueId();
        bubblePk.event = EntityEventPacket.FISH_HOOK_BUBBLE;
        Server.broadcastPacket(this.level.getPlayers().values(), bubblePk);

        EntityEventPacket teasePk = new EntityEventPacket();
        teasePk.entityRuntimeId = this.getUniqueId();
        teasePk.event = EntityEventPacket.FISH_HOOK_TEASE;
        Server.broadcastPacket(this.level.getPlayers().values(), teasePk);

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
        this.fish = new Vector3f(
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

    public void reelLine() {
        if (this.shootingEntity instanceof Player && this.caught) {
            Item item = Fishing.getFishingResult(this.rod);
            int experience = new Random().nextInt((3 - 1) + 1) + 1;
            Vector3f motion;

            if (this.shootingEntity != null) {
                motion = this.shootingEntity.subtract(this).multiply(0.1);
                motion.y += Math.sqrt(this.shootingEntity.distance(this)) * 0.08;
            } else {
                motion = new Vector3f();
            }

            CompoundTag itemTag = NBTIO.putItemHelper(item);
            itemTag.setName("Item");

            DroppedItem itemEntity = EntityRegistry.get().newEntity(EntityTypes.ITEM,
                    this.level.getChunk(this.getChunkX(), this.getChunkZ()),
                    new CompoundTag()
                            .putList(new ListTag<DoubleTag>("Pos")
                                    .add(new DoubleTag("", this.getX()))
                                    .add(new DoubleTag("", this.getWaterHeight()))
                                    .add(new DoubleTag("", this.getZ())))
                            .putList(new ListTag<DoubleTag>("Motion")
                                    .add(new DoubleTag("", motion.x))
                                    .add(new DoubleTag("", motion.y))
                                    .add(new DoubleTag("", motion.z)))
                            .putList(new ListTag<FloatTag>("Rotation")
                                    .add(new FloatTag("", new Random().nextFloat() * 360))
                                    .add(new FloatTag("", 0)))
                            .putShort("Health", 5).putCompound("Item", itemTag).putShort("PickupDelay", 1));

            if (this.shootingEntity != null && this.shootingEntity instanceof Player) {
                itemEntity.setOwner(this.shootingEntity);
            }
            itemEntity.spawnToAll();

            Player player = (Player) this.shootingEntity;
            if (experience > 0) {
                player.addExperience(experience);
            }
        }
        if (this.shootingEntity instanceof Player) {
            EntityEventPacket pk = new EntityEventPacket();
            pk.entityRuntimeId = this.getUniqueId();
            pk.event = EntityEventPacket.FISH_HOOK_TEASE;
            Server.broadcastPacket(this.level.getPlayers().values(), pk);
        }
        if (!this.closed) {
            this.kill();
            this.close();
        }
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

        entity.attack(ev);
    }

    @Override
    public boolean isCritical() {
        return false;
    }

    @Override
    public void setCritical(boolean critical) {
        // no-op
    }
}
