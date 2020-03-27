package cn.nukkit.entity.impl.projectile;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.EntityTypes;
import cn.nukkit.entity.misc.DroppedItem;
import cn.nukkit.entity.projectile.FishingHook;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.ProjectileHitEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.randomitem.Fishing;
import cn.nukkit.level.Location;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.particle.BubbleParticle;
import cn.nukkit.level.particle.WaterParticle;
import cn.nukkit.player.Player;
import cn.nukkit.registry.EntityRegistry;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.data.EntityEventType;
import com.nukkitx.protocol.bedrock.packet.EntityEventPacket;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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

    public EntityFishingHook(EntityType<FishingHook> type, Location location) {
        super(type, location);
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
            this.motion = Vector3f.from(0, getGravity() * -0.04, 0);
            hasUpdate = true;
        } else if (this.isCollided && this.keepMovement) {
            this.motion = Vector3f.ZERO;
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
        for (int y = this.getPosition().getFloorY(); y < 256; y++) {
            Identifier id = this.getLevel().getBlockId(getPosition().getFloorX(), y, getPosition().getFloorZ());
            if (id == AIR) {
                return y;
            }
        }
        return this.getPosition().getFloorY();
    }

    public void fishBites() {
        EntityEventPacket hookPacket = new EntityEventPacket();
        hookPacket.setRuntimeEntityId(this.getRuntimeId());
        hookPacket.setType(EntityEventType.FISH_HOOK_HOOK);
        Server.broadcastPacket(this.getViewers(), hookPacket);

        EntityEventPacket bubblePacket = new EntityEventPacket();
        bubblePacket.setRuntimeEntityId(this.getRuntimeId());
        bubblePacket.setType(EntityEventType.FISH_HOOK_BUBBLE);
        Server.broadcastPacket(this.getViewers(), bubblePacket);

        EntityEventPacket teasePacket = new EntityEventPacket();
        teasePacket.setRuntimeEntityId(this.getRuntimeId());
        teasePacket.setType(EntityEventType.FISH_HOOK_LURED);
        Server.broadcastPacket(this.getViewers(), teasePacket);

        Random random = ThreadLocalRandom.current();
        for (int i = 0; i < 5; i++) {
            this.getLevel().addParticle(new BubbleParticle(Vector3f.from(
                    this.getX() + random.nextDouble() * 0.5 - 0.25,
                    this.getWaterHeight(),
                    this.getZ() + random.nextDouble() * 0.5 - 0.25
            )));
        }
    }

    public void spawnFish() {
        Random random = new Random();
        this.fish = Vector3f.from(
                this.getX() + (random.nextDouble() * 1.2 + 1) * (random.nextBoolean() ? -1 : 1),
                this.getWaterHeight(),
                this.getZ() + (random.nextDouble() * 1.2 + 1) * (random.nextBoolean() ? -1 : 1)
        );
    }

    public boolean attractFish() {
        double multiply = 0.1;
        this.fish = Vector3f.from(
                this.fish.getX() + (this.getX() - this.fish.getX()) * multiply,
                this.fish.getY(),
                this.fish.getZ() + (this.getZ() - this.fish.getZ()) * multiply
        );
        if (new Random().nextInt(100) < 85) {
            this.getLevel().addParticle(new WaterParticle(this.fish));
        }
        double dist = Math.abs(Math.sqrt(this.getX() * this.getX() + this.getZ() * this.getZ()) - Math.sqrt(this.fish.getX() * this.fish.getX() + this.fish.getZ() * this.fish.getZ()));
        if (dist < 0.15) {
            return true;
        }
        return false;
    }

    public void reelLine() {
        Entity owner = this.getOwner();
        if (owner instanceof Player && this.caught) {
            Item item = Fishing.getFishingResult(this.rod);
            int experience = new Random().nextInt((3 - 1) + 1) + 1;
            Vector3f motion;

            motion = owner.getPosition().sub(this.getPosition()).mul(0.1);
            motion = motion.add(0, Math.sqrt(owner.getPosition().distance(this.getPosition())) * 0.08, 0);

            DroppedItem droppedItem = EntityRegistry.get().newEntity(EntityTypes.ITEM, this.getLocation());
            droppedItem.setMotion(motion);
            droppedItem.setHealth(5);
            droppedItem.setItem(item);
            droppedItem.setPickupDelay(1);
            droppedItem.setOwner(owner);
            droppedItem.spawnToAll();

            Player player = (Player) owner;
            if (experience > 0) {
                player.addExperience(experience);
            }
        }
        if (owner instanceof Player) {
            EntityEventPacket packet = new EntityEventPacket();
            packet.setRuntimeEntityId(this.getRuntimeId());
            packet.setType(EntityEventType.FISH_HOOK_LURED);
            Server.broadcastPacket(this.getViewers(), packet);
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

        Entity owner = this.getOwner();

        EntityDamageEvent ev;
        if (owner == null) {
            ev = new EntityDamageByEntityEvent(this, entity, DamageCause.PROJECTILE, damage);
        } else {
            ev = new EntityDamageByChildEntityEvent(owner, this, entity, DamageCause.PROJECTILE, damage);
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
