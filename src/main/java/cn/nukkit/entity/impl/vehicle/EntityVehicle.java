package cn.nukkit.entity.impl.vehicle;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityInteractable;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.Rideable;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.vehicle.VehicleDamageEvent;
import cn.nukkit.event.vehicle.VehicleDestroyEvent;
import cn.nukkit.level.Location;
import cn.nukkit.player.Player;

import static com.nukkitx.protocol.bedrock.data.entity.EntityData.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityVehicle extends BaseEntity implements Rideable, EntityInteractable {

    public EntityVehicle(EntityType<?> type, Location location) {
        super(type, location);
    }

    public int getRollingAmplitude() {
        return this.data.getInt(HURT_TIME);
    }

    public void setRollingAmplitude(int time) {
        this.data.setInt(HURT_TIME, time);
    }

    public int getRollingDirection() {
        return this.data.getInt(HURT_DIRECTION);
    }

    public void setRollingDirection(int direction) {
        this.data.setInt(HURT_DIRECTION, direction);
    }

    public int getDamage() {
        return this.data.getInt(HEALTH); // false data name (should be DATA_DAMAGE_TAKEN)
    }

    public void setDamage(int damage) {
        this.data.setInt(HEALTH, damage);
    }

    @Override
    public String getInteractButtonText() {
        return "Mount";
    }

    @Override
    public boolean canDoInteraction() {
        return passengers.isEmpty();
    }

    @Override
    public boolean onUpdate(int currentTick) {
        // The rolling amplitude
        if (getRollingAmplitude() > 0) {
            setRollingAmplitude(getRollingAmplitude() - 1);
        }

        // A killer task
        if (this.getY() < -16) {
            kill();
        }
        // Movement code
        updateMovement();
        return true;
    }

    protected boolean rollingDirection = true;

    protected boolean performHurtAnimation() {
        setRollingAmplitude(9);
        setRollingDirection(rollingDirection ? 1 : -1);
        rollingDirection = !rollingDirection;
        return true;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        VehicleDamageEvent event = new VehicleDamageEvent(this, source.getEntity(), source.getFinalDamage());
        getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        boolean instantKill = false;

        if (source instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) source).getDamager();
            instantKill = damager instanceof Player && ((Player) damager).isCreative();
        }

        if (instantKill || getHealth() - source.getFinalDamage() < 1) {
            VehicleDestroyEvent event2 = new VehicleDestroyEvent(this, source.getEntity());
            getServer().getPluginManager().callEvent(event2);

            if (event2.isCancelled()) {
                return false;
            }
        }

        if (instantKill) {
            source.setDamage(1000);
        }

        return super.attack(source);
    }
}
