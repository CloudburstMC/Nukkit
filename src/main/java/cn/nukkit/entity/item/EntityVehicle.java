package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityInteractable;
import cn.nukkit.entity.EntityRideable;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.event.vehicle.EntityEnterVehicleEvent;
import cn.nukkit.event.vehicle.EntityExitVehicleEvent;
import cn.nukkit.event.vehicle.VehicleCreateEvent;
import cn.nukkit.event.vehicle.VehicleEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.SetEntityLinkPacket;
import java.util.Objects;

/**
 * author: MagicDroidX 
 * Nukkit Project
 */
public abstract class EntityVehicle extends EntityInteractable implements EntityRideable {

    public EntityVehicle(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public int getRollingAmplitude() {
        return this.getDataPropertyInt(DATA_HURT_TIME);
    }

    public void setRollingAmplitude(int time) {
        this.setDataProperty(new IntEntityData(DATA_HURT_TIME, time));
    }

    public int getRollingDirection() {
        return this.getDataPropertyInt(DATA_HURT_DIRECTION);
    }

    public void setRollingDirection(int direction) {
        this.setDataProperty(new IntEntityData(DATA_HURT_DIRECTION, direction));
    }

    public int getDamage() {
        return this.getDataPropertyInt(DATA_HEALTH); // false data name (should be DATA_DAMAGE_TAKEN)
    }

    public void setDamage(int damage) {
        this.setDataProperty(new IntEntityData(DATA_HEALTH, damage));
    }

    @Override
    public String getInteractButton() {
        return "Mount";
    }

    @Override
    public boolean canDoInteraction() {
        return linkedEntity == null;
    }

    /**
     * Mount or Dismounts an Entity from a vehicle
     *
     * @param entity The target Entity
     * @return {@code true} if the mounting successful
     */
    public boolean mountEntity(Entity entity) {
        Objects.requireNonNull(entity, "The target of the mounting entity can't be null");
        if (!(this instanceof EntityVehicle)) {
            return false;
        }
        this.PitchDelta = 0.0D;
        this.YawDelta = 0.0D;
        if (entity.riding != null) {
            VehicleEvent ev = new EntityExitVehicleEvent(entity, (EntityVehicle) this);
            server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            }
            SetEntityLinkPacket pk;

            pk = new SetEntityLinkPacket();
            pk.rider = getId(); //Weird Weird Weird
            pk.riding = entity.getId();
            pk.type = 3;
            Server.broadcastPacket(this.hasSpawned.values(), pk);

            if (entity instanceof Player) {
                pk = new SetEntityLinkPacket();
                pk.rider = getId();
                pk.riding = entity.getId();
                pk.type = 3;
                ((Player) entity).dataPacket(pk);
            }

            entity.riding = null;
            linkedEntity = null;
            entity.setDataFlag(DATA_FLAGS, DATA_FLAG_RIDING, false);
            return true;
        }
        VehicleEvent ev = new EntityEnterVehicleEvent(entity, (EntityVehicle) this);
        server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }

        SetEntityLinkPacket pk;

        pk = new SetEntityLinkPacket();
        pk.rider = this.getId();
        pk.riding = entity.getId();
        pk.type = 2;
        Server.broadcastPacket(this.hasSpawned.values(), pk);

        if (entity instanceof Player) {
            pk = new SetEntityLinkPacket();
            pk.rider = this.getId();
            pk.riding = 0;
            pk.type = 2;
            ((Player) entity).dataPacket(pk);
        }

        entity.riding = this;
        linkedEntity = entity;

        entity.setDataFlag(DATA_FLAGS, DATA_FLAG_RIDING, true);
        updateRiderPosition(getMountedYOffset());
        return true;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (justCreated) {
            VehicleCreateEvent event = new VehicleCreateEvent(this);
            getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                kill();
                return false;
            }
            justCreated = false;
        }

        // The rolling amplitude
        if (getRollingAmplitude() > 0) {
            setRollingAmplitude(getRollingAmplitude() - 1);
        }
        // The damage token
        if (getDamage() > 0) {
            setDamage(getDamage() - 1);
        }
        // A killer task
        if (y < -16) {
            kill();
        }
        // Movement code
        updateMovement();
        return true;
    }
    
    protected boolean rollingDirection = true;

    protected boolean performHurtAnimation(int damage) {
        if (damage >= this.getHealth()) {
            return false;
        }

        // Vehicle does not respond hurt animation on packets
        // It only respond on vehicle data flags. Such as these
        setRollingAmplitude(10);
        setRollingDirection(rollingDirection ? 1 : -1);
        rollingDirection = !rollingDirection;
        setDamage(getDamage() + damage);
        return true;
    }
}
