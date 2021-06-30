package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockWater;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.FloatEntityData;
import cn.nukkit.entity.passive.EntityWaterAnimal;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.vehicle.VehicleMoveEvent;
import cn.nukkit.event.vehicle.VehicleUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.AnimatePacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.SetEntityLinkPacket;
import cn.nukkit.network.protocol.types.EntityLink;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static cn.nukkit.network.protocol.SetEntityLinkPacket.TYPE_PASSENGER;

/**
 * @author yescallop
 * @since 2016/2/13
 */
public class EntityBoat extends EntityVehicle {

    public static final int NETWORK_ID = 90;

    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", by = "Cloudburst Nukkit", 
            reason = "Was removed because it is already defined in Entity.DATA_VARIANT",
            replaceWith = "Entity.DATA_VARIANT")
    @PowerNukkitOnly public static final int DATA_WOOD_ID = 20;
    
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
    private int ticksInWater;
    private final Set<Entity> ignoreCollision = new HashSet<>(2);
    
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", 
            reason = "Unreliable direct field access", replaceWith = "getVariant(), setVariant(int)")
    @Since("1.4.0.0-PN") public int woodID;

    public EntityBoat(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        this.setMaxHealth(40);
        this.setHealth(40);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        if (this.namedTag.contains("Variant")) {
            woodID = this.namedTag.getInt("Variant");
        } else if (this.namedTag.contains("woodID")) {
            woodID = this.namedTag.getByte("woodID");
        }

        this.setDataFlag(DATA_FLAGS, DATA_FLAG_GRAVITY, true);
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_STACKABLE, true);
        this.dataProperties.putInt(DATA_VARIANT, woodID);
        this.dataProperties.putBoolean(DATA_IS_BUOYANT, true);
        this.dataProperties.putString(DATA_BUOYANCY_DATA, "{\"apply_gravity\":true,\"base_buoyancy\":1.0,\"big_wave_probability\":0.02999999932944775,\"big_wave_speed\":10.0,\"drag_down_on_buoyancy_removed\":0.0,\"liquid_blocks\":[\"minecraft:water\",\"minecraft:flowing_water\"],\"simulate_waves\":true}");
        this.dataProperties.putInt(DATA_MAX_AIR, 300);
        this.dataProperties.putLong(DATA_OWNER_EID, -1);
        this.dataProperties.putFloat(DATA_PADDLE_TIME_LEFT, 0);
        this.dataProperties.putFloat(DATA_PADDLE_TIME_RIGHT, 0);
        this.dataProperties.putByte(DATA_CONTROLLING_RIDER_SEAT_NUMBER, 0);
        this.dataProperties.putInt(DATA_LIMITED_LIFE, -1);
        this.dataProperties.putByte(DATA_ALWAYS_SHOW_NAMETAG, -1);
        this.dataProperties.putFloat(DATA_AMBIENT_SOUND_INTERVAL, 8F);
        this.dataProperties.putFloat(DATA_AMBIENT_SOUND_INTERVAL_RANGE, 16F);
        this.dataProperties.putString(DATA_AMBIENT_SOUND_EVENT_NAME, "ambient");
        this.dataProperties.putFloat(DATA_FALL_DAMAGE_MULTIPLIER, 1F);
        entityCollisionReduction = -0.5;
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
        return 0.03999999910593033F;
    }

    @Override
    public float getBaseOffset() {
        return 0.375F;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public String getInteractButtonText() {
        return "action.interact.ride.boat";
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

        for (Entity linkedEntity : this.passengers) {
            linkedEntity.riding = null;
        }
    }

    @Override
    protected DataPacket createAddEntityPacket() {
        AddEntityPacket addEntity = new AddEntityPacket();
        addEntity.type = 0;
        addEntity.id = "minecraft:boat";
        addEntity.entityUniqueId = this.getId();
        addEntity.entityRuntimeId = this.getId();
        addEntity.yaw = (float) this.yaw;
        addEntity.headYaw = (float) this.yaw;
        addEntity.pitch = (float) this.pitch;
        addEntity.x = (float) this.x;
        addEntity.y = (float) this.y + getBaseOffset();
        addEntity.z = (float) this.z;
        addEntity.speedX = (float) this.motionX;
        addEntity.speedY = (float) this.motionY;
        addEntity.speedZ = (float) this.motionZ;
        addEntity.metadata = this.dataProperties;

        addEntity.links = new EntityLink[this.passengers.size()];
        for (int i = 0; i < addEntity.links.length; i++) {
            addEntity.links[i] = new EntityLink(this.getId(), this.passengers.get(i).getId(), i == 0 ? EntityLink.TYPE_RIDER : TYPE_PASSENGER, false, false);
        }

        return addEntity;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        int tickDiff = currentTick - this.lastUpdate;

        if (tickDiff <= 0 && !this.justCreated) {
            return true;
        }

        this.lastUpdate = currentTick;

        boolean hasUpdate = this.entityBaseTick(tickDiff);

        if (this.isAlive()) {
            hasUpdate = this.updateBoat(tickDiff) || hasUpdate;
        }

        return hasUpdate || !this.onGround || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001;
    }

    private boolean updateBoat(int tickDiff) {
        // The rolling amplitude
        if (getRollingAmplitude() > 0) {
            setRollingAmplitude(getRollingAmplitude() - 1);
        }

        // A killer task
        if (y < -16) {
            kill();
            return false;
        }

        boolean hasUpdated = false;
        double waterDiff = getWaterLevel();
        if (!hasControllingPassenger()) {
            hasUpdated = computeBuoyancy(waterDiff);
            Iterator<Entity> iterator = ignoreCollision.iterator();
            while (iterator.hasNext()) {
                Entity ignored = iterator.next();
                if (!ignored.isValid() || ignored.isClosed() || !ignored.isAlive()
                        || !ignored.getBoundingBox().intersectsWith(getBoundingBox().grow(0.5, 0.5, 0.5))) {
                    iterator.remove();
                    hasUpdated = true;
                }
            }
            moveBoat(waterDiff);
        } else {
            updateMovement();
        }
        hasUpdated = hasUpdated || positionChanged;
        if (waterDiff >= -SINKING_DEPTH) {
            if (ticksInWater != 0) {
                ticksInWater = 0;
                hasUpdated = true;
            }
            //hasUpdated = collectCollidingEntities() || hasUpdated;
        } else {
            hasUpdated = true;
            ticksInWater += tickDiff;
            if (ticksInWater >= 3 * 20) {
                for (int i = passengers.size() - 1; i >= 0; i--) {
                    dismountEntity(passengers.get(i));
                }
            }
        }
        this.getServer().getPluginManager().callEvent(new VehicleUpdateEvent(this));
        return hasUpdated;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return super.canCollideWith(entity) && !isPassenger(entity);
    }

    @Override
    public boolean canDoInteraction() {
        return passengers.size() < 2;
    }

    private void moveBoat(double waterDiff) {
        checkObstruction(this.x, this.y, this.z);
        move(this.motionX, this.motionY, this.motionZ);

        double friction = 1 - this.getDrag();

        if (this.onGround && (Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionZ) > 0.00001)) {
            friction *= this.getLevel().getBlock(this.temporalVector.setComponents((int) Math.floor(this.x), (int) Math.floor(this.y - 1), (int) Math.floor(this.z) - 1)).getFrictionFactor();
        }

        this.motionX *= friction;

        if (!onGround || waterDiff > SINKING_DEPTH/* || sinking*/) {
            this.motionY = waterDiff > 0.5 ? this.motionY - this.getGravity() : (this.motionY - SINKING_SPEED < -SINKING_MAX_SPEED ? this.motionY : this.motionY - SINKING_SPEED);
        }

        this.motionZ *= friction;

        Location from = new Location(lastX, lastY, lastZ, lastYaw, lastPitch, level);
        Location to = new Location(this.x, this.y, this.z, this.yaw, this.pitch, level);

        if (!from.equals(to)) {
            this.getServer().getPluginManager().callEvent(new VehicleMoveEvent(this, from, to));
        }

        //TODO: lily pad collision
        this.updateMovement();
    }

    private boolean collectCollidingEntities() {
        if (this.passengers.size() >= 2) {
            return false;
        }

        for (Entity entity : this.level.getCollidingEntities(this.boundingBox.grow(0.20000000298023224, 0.0D, 0.20000000298023224), this)) {
            if (entity.riding != null || !(entity instanceof EntityLiving) || entity instanceof Player || entity instanceof EntityWaterAnimal || isPassenger(entity)) {
                continue;
            }

            this.mountEntity(entity);

            if (this.passengers.size() >= 2) {
                break;
            }
        }

        return true;
    }

    private boolean computeBuoyancy(double waterDiff) {
        boolean hasUpdated = false;
        waterDiff -= getBaseOffset()/4;
        if (waterDiff > SINKING_DEPTH && !sinking) {
            sinking = true;
        } else if (waterDiff < -SINKING_DEPTH && sinking) {
            sinking = false;
        }

        if (waterDiff < -SINKING_DEPTH/1.7) {
            this.motionY = Math.min(0.05/10, this.motionY + 0.005);
            hasUpdated = true;
        } else if (waterDiff < 0 || !sinking) {
            this.motionY = this.motionY > (SINKING_MAX_SPEED/2) ? Math.max(this.motionY - 0.02, (SINKING_MAX_SPEED/2)) : this.motionY + SINKING_SPEED;
            hasUpdated = true;
        }
        return hasUpdated;
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
            (ent = this.passengers.get(0)).setSeatPosition(getMountedOffset(ent));
            super.updatePassengerPosition(ent);

            if (sendLinks) {
                broadcastLinkPacket(ent, SetEntityLinkPacket.TYPE_RIDE);
            }
        } else if (passengers.size() == 2) {
            if (!((ent = passengers.get(0)) instanceof Player)) { //swap
                Entity passenger2 = passengers.get(1);

                if (passenger2 instanceof Player) {
                    this.passengers.set(0, passenger2);
                    this.passengers.set(1, ent);

                    ent = passenger2;
                }
            }

            ent.setSeatPosition(getMountedOffset(ent).add(RIDER_PASSENGER_OFFSET));
            super.updatePassengerPosition(ent);
            if (sendLinks) {
                broadcastLinkPacket(ent, SetEntityLinkPacket.TYPE_RIDE);
            }

            (ent = this.passengers.get(1)).setSeatPosition(getMountedOffset(ent).add(PASSENGER_OFFSET));

            super.updatePassengerPosition(ent);

            if (sendLinks) {
                broadcastLinkPacket(ent, SetEntityLinkPacket.TYPE_PASSENGER);
            }

            //float yawDiff = ent.getId() % 2 == 0 ? 90 : 270;
            //ent.setRotation(this.yaw + yawDiff, ent.pitch);
            //ent.updateMovement();
        } else {
            for (Entity passenger : passengers) {
                super.updatePassengerPosition(passenger);
            }
        }
    }

    public double getWaterLevel() {
        double maxY = this.boundingBox.getMinY() + getBaseOffset();
        AxisAlignedBB.BBConsumer<Double> consumer = new AxisAlignedBB.BBConsumer<Double>() {

            private double diffY = Double.MAX_VALUE;

            @Override
            public void accept(int x, int y, int z) {
                Block block = EntityBoat.this.level.getBlock(EntityBoat.this.temporalVector.setComponents(x, y, z));

                if (block instanceof BlockWater || ((block = block.getLevelBlockAtLayer(1)) instanceof BlockWater)) {
                    double level = block.getMaxY();

                    diffY = Math.min(maxY - level, diffY);
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
    public boolean mountEntity(Entity entity) {
        boolean player = this.passengers.size() >= 1 && this.passengers.get(0) instanceof Player;
        byte mode = SetEntityLinkPacket.TYPE_PASSENGER;

        if (!player && (entity instanceof Player || this.passengers.size() == 0)) {
            mode = SetEntityLinkPacket.TYPE_RIDE;
        }

        return super.mountEntity(entity, mode);
    }

    @Override
    public boolean mountEntity(Entity entity, byte mode) {
        boolean r = super.mountEntity(entity, mode);
        if (entity.riding == this) {
            updatePassengers(true);

            entity.setDataProperty(new ByteEntityData(DATA_RIDER_ROTATION_LOCKED, 1));
            entity.setDataProperty(new FloatEntityData(DATA_RIDER_MAX_ROTATION, 90));
            entity.setDataProperty(new FloatEntityData(DATA_RIDER_ROTATION_OFFSET, -90));
            entity.setDataProperty(new FloatEntityData(DATA_RIDER_MIN_ROTATION, this.passengers.indexOf(entity) == 1 ? -90 : 1));
            entity.setRotation(yaw, entity.pitch);
            entity.updateMovement();
        }
        return r;
    }

    @Override
    protected void updatePassengerPosition(Entity passenger) {
        updatePassengers();
    }

    @Override
    public boolean dismountEntity(Entity entity) {
        boolean r = super.dismountEntity(entity);

        updatePassengers();
        entity.setDataProperty(new ByteEntityData(DATA_RIDER_ROTATION_LOCKED, 0));
        if (entity instanceof EntityHuman) {
            ignoreCollision.add(entity);
        }

        return r;
    }

    @Override
    public boolean isControlling(Entity entity) {
        return entity instanceof Player && this.passengers.indexOf(entity) == 0;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (this.passengers.size() >= 2 || getWaterLevel() < -SINKING_DEPTH) {
            return false;
        }

        super.mountEntity(player);
        return super.onInteract(player, item, clickedPos);
    }

    @Override
    public Vector3f getMountedOffset(Entity entity) {
        return entity instanceof Player ? RIDER_PLAYER_OFFSET : RIDER_OFFSET;
    }

    public void onPaddle(AnimatePacket.Action animation, float value) {
        int propertyId = animation == AnimatePacket.Action.ROW_RIGHT ? DATA_PADDLE_TIME_RIGHT : DATA_PADDLE_TIME_LEFT;

        if (Float.compare(getDataPropertyFloat(propertyId), value) != 0) {
            this.setDataProperty(new FloatEntityData(propertyId, value));
        }
    }

    @Override
    public void applyEntityCollision(Entity entity) {
        if (this.riding == null && !hasControllingPassenger() && entity.riding != this
                && !entity.passengers.contains(this) && !ignoreCollision.contains(entity)) {
            if (!entity.boundingBox.intersectsWith(this.boundingBox.grow(0.20000000298023224, -0.1, 0.20000000298023224))
                    || entity instanceof Player && ((Player) entity).isSpectator()) {
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

    @PowerNukkitDifference(info = "Fixes a dupe issue when attacking too quickly", since = "1.3.1.2-PN")
    @Override
    public void kill() {
        if (!isAlive()) {
            return;
        }
        super.kill();

        if (level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
            this.level.dropItem(this, Item.get(ItemID.BOAT, this.woodID));
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putInt("Variant", this.woodID); // Correct way in Bedrock Edition
        this.namedTag.putByte("woodID", this.woodID); // Compatibility with Cloudburst Nukkit
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getVariant() {
        return this.woodID;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setVariant(int variant) {
        this.woodID = variant;
        this.dataProperties.putInt(DATA_VARIANT, variant);
    }


    @PowerNukkitOnly
    @Since("FUTURE")
    @Override
    public String getOriginalName() {
        return "Boat";
    }
}
