package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockWater;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.FloatEntityData;
import cn.nukkit.entity.passive.EntityWaterAnimal;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.vehicle.VehicleMoveEvent;
import cn.nukkit.event.vehicle.VehicleUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.*;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AnimatePacket;
import cn.nukkit.network.protocol.SetEntityLinkPacket;

import java.util.ArrayList;

/**
 * Created by yescallop on 2016/2/13.
 */
public class EntityBoat extends EntityVehicle {

    public static final int NETWORK_ID = 90;

    public static final Vector3f RIDER_PLAYER_OFFSET = new Vector3f(0, 1.02001f, 0);
    public static final Vector3f RIDER_OFFSET = new Vector3f(0, -0.2f, 0);

    public static final Vector3f PASSENGER_OFFSET = new Vector3f(-0.6f);
    public static final Vector3f RIDER_PASSENGER_OFFSET = new Vector3f(0.2f);

    public static final int RIDER_INDEX = 0;
    public static final int PASSENGER_INDEX = 1;

    public static final double SINKING_DEPTH = 0.07;
    public static final double SINKING_SPEED = 0.0005;
    public static final double SINKING_MAX_SPEED = 0.005;

    protected boolean sinking = true;
    public int woodID;

    public EntityBoat(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(40);
        super.initEntity();

        this.setHealth(40);

        if (this.namedTag.contains("Variant")) {
            this.woodID = this.namedTag.getInt("Variant");
        }
        this.dataProperties.putInt(DATA_VARIANT, this.woodID);
        this.dataProperties.putBoolean(DATA_IS_BUOYANT, true);
        this.dataProperties.putString(DATA_BUOYANCY_DATA, "{\"apply_gravity\":true,\"base_buoyancy\":1.0,\"big_wave_probability\":0.02999999932944775,\"big_wave_speed\":10.0,\"drag_down_on_buoyancy_removed\":0.0,\"liquid_blocks\":[\"minecraft:water\",\"minecraft:flowing_water\"],\"simulate_waves\":true}");
    }

    @Override
    public float getHeight() {
        return 0.455f;
    }

    @Override
    public float getWidth() {
        return 1.4f;
    }

    @Override
    protected float getDrag() {
        return 0.1f;
    }

    @Override
    protected float getGravity() {
        return 0.04f;
    }

    @Override
    public float getBaseOffset() {
        return 0.375f;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (invulnerable) {
            return false;
        } else {
            source.setDamage(source.getDamage() * 2);

            boolean attack = super.attack(source);

            if (isAlive()) {
                performHurtAnimation();
            }

            return attack;
        }
    }

    @Override
    public void close() {
        super.close();

        if (!passengers.isEmpty()) {
            for (Entity passenger : new ArrayList<>(passengers)) {
                dismountEntity(passenger);
                passenger.riding = null; // Make sure it's really removed even if a plugin tries to cancel it
            }
        }
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        boolean hasUpdate = false;

        double waterDiff = getWaterLevel();

        if (!hasControllingPassenger()) {
            if (waterDiff > SINKING_DEPTH && !sinking) {
                sinking = true;
            } else if (waterDiff < -0.07 && sinking) {
                sinking = false;
            }

            if (waterDiff < -0.07) {
                this.motionY = Math.min(0.05, this.motionY + 0.005);
            } else if (waterDiff < 0 || !sinking) {
                this.motionY = this.motionY > SINKING_MAX_SPEED ? Math.max(this.motionY - 0.02, SINKING_MAX_SPEED) : this.motionY + SINKING_SPEED;
            }
        }

        if (this.checkObstruction(this.x, this.y, this.z)) {
            hasUpdate = true;
        }

        double friction = 1 - this.getDrag();

        if (this.onGround && (Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionZ) > 0.00001)) {
            friction *= this.getLevel().getBlock(this.chunk, getFloorX(), getFloorY() - 1, getFloorZ(), false).getFrictionFactor();
        }

        this.motionX *= friction;

        if (!hasControllingPassenger()) {
            if (waterDiff > SINKING_DEPTH || sinking) {
                this.motionY = waterDiff > 0.5 ? this.motionY - this.getGravity() : (this.motionY - SINKING_SPEED < -0.005 ? this.motionY : this.motionY - SINKING_SPEED);
            }
        }

        this.motionZ *= friction;

        Location from = new Location(lastX, lastY, lastZ, lastYaw, lastPitch, level);
        Location to = new Location(this.x, this.y, this.z, this.yaw, this.pitch, level);

        this.getServer().getPluginManager().callEvent(new VehicleUpdateEvent(this));

        if (!from.equals(to)) {
            this.getServer().getPluginManager().callEvent(new VehicleMoveEvent(this, from, to));
        }

        this.move(this.motionX, this.motionY, this.motionZ);

        if (this.age % 5 == 0) {
            if (!this.passengers.isEmpty() && this.passengers.get(0) instanceof Player) {
                Block[] blocks = this.level.getCollisionBlocks(this.getBoundingBox().grow(0.1, 0.3, 0.1));
                for (Block b : blocks) {
                    if (b.getId() == Block.LILY_PAD) {
                        this.level.setBlockAt((int) b.x, (int) b.y, (int) b.z, 0, 0);
                        this.level.dropItem(b, Item.get(Item.LILY_PAD, 0, 1));
                    }
                }
            }
        }

        // We call super here after movement code so block collision checks use up to date position
        return super.entityBaseTick(tickDiff) || hasUpdate || !this.onGround || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001;
    }

    public void updatePassengers() {
        updatePassengers(false);
    }

    public void updatePassengers(boolean sendLinks) {
        if (this.passengers.isEmpty()) {
            return;
        }

        for (Entity passenger : new ArrayList<>(passengers)) {
            if (!passenger.isAlive()) {
                dismountEntity(passenger);
            }
        }

        Entity ent;

        if (passengers.size() == 1) {
            (ent = this.passengers.get(RIDER_INDEX)).setSeatPosition(getMountedOffset(ent));
            super.updatePassengerPosition(ent);

            if (sendLinks) {
                broadcastLinkPacket(ent, SetEntityLinkPacket.TYPE_RIDE);
            }
        } else if (passengers.size() == 2) {
            if (!((ent = passengers.get(RIDER_INDEX)) instanceof Player)) { //swap
                Entity passenger2 = passengers.get(PASSENGER_INDEX);

                if (passenger2 instanceof Player) {
                    this.passengers.set(RIDER_INDEX, passenger2);
                    this.passengers.set(PASSENGER_INDEX, ent);

                    ent = passenger2;
                }
            }

            ent.setSeatPosition(getMountedOffset(ent).add(RIDER_PASSENGER_OFFSET));
            super.updatePassengerPosition(ent);
            if (sendLinks) {
                broadcastLinkPacket(ent, SetEntityLinkPacket.TYPE_RIDE);
            }

            (ent = this.passengers.get(PASSENGER_INDEX)).setSeatPosition(getMountedOffset(ent).add(PASSENGER_OFFSET));

            super.updatePassengerPosition(ent);

            if (sendLinks) {
                broadcastLinkPacket(ent, SetEntityLinkPacket.TYPE_PASSENGER);
            }

            float yawDiff = ent.getId() % 2 == 0 ? 90 : 270;
            ent.setRotation(this.yaw + yawDiff, ent.pitch);
            ent.updateMovement();
        } else {
            for (Entity passenger : passengers) {
                super.updatePassengerPosition(passenger);
            }
        }
    }

    public double getWaterLevel() {
        double maxY = this.boundingBox.getMinY() + getBaseOffset();
        AxisAlignedBB.BBConsumer<Double> consumer = new SimpleAxisAlignedBB.BBConsumer<Double>() {

            private double diffY = Double.MAX_VALUE;

            @Override
            public void accept(int x, int y, int z) {
                Block block = EntityBoat.this.level.getBlock(EntityBoat.this.temporalVector.setComponents(x, y, z));
                if (block instanceof BlockWater) {
                    diffY = Math.min(maxY - block.getMaxY(), diffY);
                }
            }

            @Override
            public Double get() {
                return diffY;
            }
        };

        this.boundingBox.forEach(consumer);

        return consumer.get();
    }

    @Override
    public boolean mountEntity(Entity entity, byte mode) {
        boolean player = !this.passengers.isEmpty() && this.passengers.get(RIDER_INDEX) instanceof Player;
        mode = SetEntityLinkPacket.TYPE_PASSENGER;

        if (!player && (entity instanceof Player || this.passengers.isEmpty())) {
            mode = SetEntityLinkPacket.TYPE_RIDE;
        }

        boolean r = super.mountEntity(entity, mode);

        if (r) {
            updatePassengers(true);

            entity.setDataProperty(new ByteEntityData(DATA_RIDER_ROTATION_LOCKED, 1), !(entity instanceof Player));
            if (entity instanceof Player) {
                entity.setDataProperty(new FloatEntityData(DATA_RIDER_MAX_ROTATION, 90), false);
                entity.setDataProperty(new FloatEntityData(DATA_RIDER_MIN_ROTATION, 1), false);
                entity.setDataProperty(new FloatEntityData(DATA_RIDER_ROTATION_OFFSET, -90), false);
                entity.sendData(((Player) entity));
            }
        }

        return r;
    }

    @Override
    protected void updatePassengerPosition(Entity passenger) {
        updatePassengers();
    }

    @Override
    public boolean dismountEntity(Entity entity, boolean sendLinks) {
        boolean r = super.dismountEntity(entity, sendLinks);

        if (r) {
            updatePassengers();
            entity.setDataProperty(new ByteEntityData(DATA_RIDER_ROTATION_LOCKED, 0), true);
        }
        return r;
    }

    @Override
    public boolean isControlling(Entity entity) {
        return entity instanceof Player && this.passengers.indexOf(entity) == RIDER_INDEX;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (this.isFull() || getWaterLevel() < -SINKING_DEPTH) {
            return false;
        }

        this.mountEntity(player);
        return super.onInteract(player, item, clickedPos);
    }

    @Override
    public Vector3f getMountedOffset(Entity entity) {
        return entity instanceof Player ? RIDER_PLAYER_OFFSET : RIDER_OFFSET;
    }

    public void onPaddle(AnimatePacket.Action animation, float value) {
        int propertyId = animation == AnimatePacket.Action.ROW_RIGHT ? DATA_PADDLE_TIME_RIGHT : DATA_PADDLE_TIME_LEFT;

        if (getDataPropertyFloat(propertyId) != value) {
            this.setDataProperty(new FloatEntityData(propertyId, value));
        }
    }

    @Override
    public void applyEntityCollision(Entity entity) {
        if (entity instanceof Player && entity.riding != this) {
            ((Player) entity).resetInAirTicks(); // Hack: Don't kick players standing on a boat for flying
        }

        if (this.passengers.size() < 2) {
            if (!(entity.riding != null || !(entity instanceof EntityLiving) || entity instanceof Player || entity instanceof EntityWaterAnimal || this.isPassenger(entity))) {
                this.mountEntity(entity);
            }
        }

        if (this.riding == null && entity.riding != this && !entity.passengers.contains(this)) {
            if (!entity.boundingBox.intersectsWith(this.boundingBox.grow(0.20000000298023224, -0.1, 0.20000000298023224))
                    || entity instanceof Player && ((Player) entity).getGamemode() == Player.SPECTATOR) {
                return;
            }

            double diffX = entity.x - this.x;
            double diffZ = entity.z - this.z;

            double direction = NukkitMath.getDirection(diffX, diffZ);

            if (direction >= 0.009999999776482582D) {
                direction = Math.sqrt(direction);
                diffX /= direction;
                diffZ /= direction;

                double d3 = Math.min(1 / direction, 1);

                diffX *= d3;
                diffZ *= d3;
                diffX *= 0.05000000074505806;
                diffZ *= 0.05000000074505806;
                diffX *= 1 + entityCollisionReduction;
                diffZ *= 1 + entityCollisionReduction;

                if (this.riding == null) {
                    motionX -= diffX;
                    motionZ -= diffZ;
                }
            }
        }
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    @Override
    public void kill() {
        if (!this.isAlive()) {
            return;
        }

        super.kill();

        if (level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
            if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
                Entity damager = ((EntityDamageByEntityEvent) this.lastDamageCause).getDamager();
                if (damager instanceof Player && ((Player) damager).isCreative()) {
                    return;
                }
            }
            this.dropItem();
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putInt("Variant", this.woodID);
    }

    public int getVariant() {
        return this.woodID;
    }

    public void setVariant(int variant) {
        this.woodID = variant;
        this.dataProperties.putInt(DATA_VARIANT, variant);
    }

    public void onInput(double x, double y, double z, double yaw) {
        this.setPositionAndRotation(this.temporalVector.setComponents(x, y - this.getBaseOffset(), z), yaw % 360, 0);
    }

    public boolean isFull() {
        return this.passengers.size() >= 2;
    }

    @Override
    public String getInteractButtonText() {
        return !this.isFull() ? "action.interact.ride.boat" : "";
    }

    protected void dropItem() {
        this.level.dropItem(this, Item.get(Item.BOAT, this.woodID));
    }
}
