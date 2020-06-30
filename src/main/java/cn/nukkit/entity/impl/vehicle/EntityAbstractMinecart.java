package cn.nukkit.entity.impl.vehicle;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockRail;
import cn.nukkit.block.BlockRailActivator;
import cn.nukkit.block.BlockRailPowered;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.EntityLiving;
import cn.nukkit.entity.impl.Human;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.vehicle.VehicleMoveEvent;
import cn.nukkit.event.vehicle.VehicleUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.Location;
import cn.nukkit.level.gamerule.GameRules;
import cn.nukkit.level.particle.SmokeParticle;
import cn.nukkit.math.MathHelper;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.MinecartType;
import cn.nukkit.utils.Rail;
import cn.nukkit.utils.Rail.Orientation;
import com.nukkitx.math.GenericMath;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

import java.util.Iterator;
import java.util.Objects;

import static com.nukkitx.protocol.bedrock.data.entity.EntityData.*;

/**
 * Created by: larryTheCoder on 2017/6/26.
 * <p>
 * Nukkit Project,
 * Minecart and Riding Project,
 * Package cn.nukkit.entity.item in project Nukkit.
 */
public abstract class EntityAbstractMinecart extends EntityVehicle {

    private String entityName;
    private static final int[][][] matrix = new int[][][]{
            {{0, 0, -1}, {0, 0, 1}},
            {{-1, 0, 0}, {1, 0, 0}},
            {{-1, -1, 0}, {1, 0, 0}},
            {{-1, 0, 0}, {1, -1, 0}},
            {{0, 0, -1}, {0, -1, 1}},
            {{0, -1, -1}, {0, 0, 1}},
            {{0, 0, 1}, {1, 0, 0}},
            {{0, 0, 1}, {-1, 0, 0}},
            {{0, 0, -1}, {-1, 0, 0}},
            {{0, 0, -1}, {1, 0, 0}}
    };
    private float currentSpeed = 0;
    // Plugins modifiers
    private boolean slowWhenEmpty = true;
    private Vector3f derailed = Vector3f.from(0.5, 0.5, 0.5);
    private Vector3f flying = Vector3f.from(0.95, 0.95, 0.95);
    private float maxSpeed = 0.4f;

    public EntityAbstractMinecart(EntityType<?> type, Location location) {
        super(type, location);

        setMaxHealth(40);
        setHealth(40);
    }

    public abstract boolean isRideable();

    public abstract MinecartType getMinecartType();

    @Override
    public float getHeight() {
        return 0.7F;
    }

    @Override
    public float getWidth() {
        return 0.98F;
    }

    @Override
    public float getDrag() {
        return 0.1F;
    }

    public void setName(String name) {
        entityName = name;
    }

    @Override
    public String getName() {
        return entityName;
    }

    @Override
    public float getBaseOffset() {
        return 0.35F;
    }

    @Override
    public boolean hasNameTag() {
        return entityName != null;
    }

    @Override
    public boolean canDoInteraction() {
        return passengers.isEmpty() && this.getDisplayBlock() == null;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        setRollingAmplitude(0);
        setRollingDirection(1);
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        if (tag.getBoolean("CustomDisplayTile")) {
            this.setDisplay(true);

            int id;
            int meta;
            BlockRegistry registry = BlockRegistry.get();
            if (tag.contains("DisplayTile") && tag.contains("DisplayData")) {
                id = tag.getInt("DisplayTile");
                meta = tag.getInt("DisplayData");
            } else {
                CompoundTag plantTag = tag.getCompound("DisplayBlock");
                id = registry.getLegacyId(plantTag.getString("name"));
                meta = plantTag.getShort("val");
            }
            this.setDisplayBlock(registry.getBlock(id, meta));

            this.setDisplayBlockOffset(tag.getInt("DisplayOffset"));
        }
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        if (this.hasDisplay()) {
            tag.booleanTag("CustomDisplayTile", true);

            Block block = this.getDisplayBlock();
            tag.tag(CompoundTag.builder()
                    .stringTag("name", block.getId().toString())
                    .shortTag("val", (short) block.getMeta())
                    .build("DisplayBlock"));

            tag.intTag("DisplayOffset", this.getDisplayOffset());
        }
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        if (!this.isAlive()) {
            ++this.deadTicks;
            if (this.deadTicks >= 10) {
                this.despawnFromAll();
                this.close();
            }
            return this.deadTicks < 10;
        }

        int tickDiff = currentTick - this.lastUpdate;

        if (tickDiff <= 0) {
            return false;
        }

        this.lastUpdate = currentTick;

        if (isAlive()) {
            super.onUpdate(currentTick);

            // The damage token
            if (getHealth() < 20) {
                setHealth(getHealth() + 1);
            }

            // Entity variables
            this.lastPosition = this.position;
            this.motion = this.motion.sub(0, 0.04, 0);
            int dx = this.position.getFloorX();
            int dy = this.position.getFloorY();
            int dz = this.position.getFloorZ();

            // Some hack to check rails
            if (Rail.isRailBlock(this.getLevel().getBlockId(dx, dy - 1, dz))) {
                --dy;
            }

            Block block = this.getLevel().getBlock(dx, dy, dz);

            // Ensure that the block is a rail
            if (Rail.isRailBlock(block)) {
                processMovement(dx, dy, dz, (BlockRail) block);
                // Activate the minecart/TNT
                if (block instanceof BlockRailActivator && ((BlockRailActivator) block).isActive()) {
                    activate(dx, dy, dz, (block.getMeta() & 0x8) != 0);
                }
            } else {
                setFalling();
            }
            checkBlockCollision();

            // Minecart head
            pitch = 0;
            float diffX = this.lastPosition.getX() - this.getX();
            float diffZ = this.lastPosition.getZ() - this.getZ();
            float yawToChange = yaw;
            if (diffX * diffX + diffZ * diffZ > 0.001D) {
                yawToChange = (float) (Math.atan2(diffZ, diffX) * 180 / Math.PI);
            }

            // Reverse yaw if yaw is below 0
            if (yawToChange < 0) {
                // -90-(-90)-(-90) = 90
                yawToChange -= yawToChange - yawToChange;
            }

            setRotation(yawToChange, this.getPitch());

            Location from = Location.from(this.lastPosition, lastYaw, lastPitch, this.getLevel());
            Location to = Location.from(this.position, this.yaw, this.pitch, this.getLevel());

            this.getServer().getPluginManager().callEvent(new VehicleUpdateEvent(this));

            if (!from.equals(to)) {
                this.getServer().getPluginManager().callEvent(new VehicleMoveEvent(this, from, to));
            }

            // Collisions
            for (Entity entity : this.getLevel().getNearbyEntities(boundingBox.grow(0.2f, 0, 0.2f), this)) {
                if (!passengers.contains(entity) && entity instanceof EntityAbstractMinecart) {
                    entity.onEntityCollision(this);
                }
            }

            Iterator<Entity> linkedIterator = this.passengers.iterator();

            while (linkedIterator.hasNext()) {
                Entity linked = linkedIterator.next();

                if (!linked.isAlive()) {
                    if (linked.getVehicle() == this) {
                        linked.dismount(this);
                    }

                    linkedIterator.remove();
                }
            }

            // No need to onGround or Motion diff! This always have an update
            return true;
        }

        return false;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (invulnerable) {
            return false;
        } else {
            source.setDamage(source.getDamage() * 15);

            boolean attack = super.attack(source);

            if (isAlive()) {
                performHurtAnimation();
            }

            return attack;
        }
    }

    public void dropItem() {
        this.getLevel().dropItem(this.getPosition(), Item.get(ItemIds.MINECART));
    }

    @Override
    public void kill() {
        super.kill();

        if (this.getLevel().getGameRules().get(GameRules.DO_ENTITY_DROPS)) {
            dropItem();
        }
    }

    @Override
    public void close() {
        super.close();

        for (Entity entity : passengers) {
            entity.dismount(this);
        }

        SmokeParticle particle = new SmokeParticle(this.getPosition());
        this.getLevel().addParticle(particle);
    }

    @Override
    public boolean onInteract(Player p, Item item, Vector3f clickedPos) {
        if (!passengers.isEmpty() && isRideable()) {
            return false;
        }

        if (!this.hasDisplay()) {
            p.mount(this);
        }

        return super.onInteract(p, item, clickedPos);
    }

    @Override
    public void onEntityCollision(Entity entity) {
        if (entity != vehicle) {
            if (entity instanceof EntityLiving
                    && !(entity instanceof Human)
                    && this.motion.getX() * this.motion.getX() + this.motion.getZ() * this.motion.getZ() > 0.01D
                    && passengers.isEmpty()
                    && entity.getVehicle() == null
                    && !this.hasDisplay()) {
                if (vehicle == null) {
                    entity.mount(this);// TODO: rewrite (weird riding)
                }
            }

            double motiveX = entity.getX() - this.getX();
            double motiveZ = entity.getZ() - this.getZ();
            double square = motiveX * motiveX + motiveZ * motiveZ;

            if (square >= 9.999999747378752E-5D) {
                square = Math.sqrt(square);
                motiveX /= square;
                motiveZ /= square;
                double next = 1 / square;

                if (next > 1) {
                    next = 1;
                }

                motiveX *= next;
                motiveZ *= next;
                motiveX *= 0.1f;
                motiveZ *= 0.1f;
                motiveX *= 1 + entityCollisionReduction;
                motiveZ *= 1 + entityCollisionReduction;
                motiveX *= 0.5D;
                motiveZ *= 0.5D;
                if (entity instanceof EntityAbstractMinecart) {
                    EntityAbstractMinecart mine = (EntityAbstractMinecart) entity;
                    float desinityX = mine.getX() - this.getX();
                    float desinityZ = mine.getZ() - this.getZ();
                    Vector3f vector = Vector3f.from(desinityX, 0, desinityZ).normalize();
                    Vector3f vec = Vector3f.from(MathHelper.cos(yaw * 0.017453292F), 0, MathHelper.sin(yaw * 0.017453292F)).normalize();
                    float desinityXZ = Math.abs(vector.dot(vec));

                    if (desinityXZ < 0.8f) {
                        return;
                    }

                    double motX = mine.getMotion().getX() + this.motion.getX();
                    double motZ = mine.getMotion().getZ() + this.motion.getZ();

                    if (mine.getMinecartType().getId() == 2 && getMinecartType().getId() != 2) {
                        this.motion = this.motion.mul(0.2, 1, 0.2)
                                .add(mine.getMotion().getX() - motiveX, 0, mine.getMotion().getZ() - motiveZ)
                                .mul(0.95, 1, 0.95);
                    } else if (mine.getMinecartType().getId() != 2 && getMinecartType().getId() == 2) {
                        this.motion = this.motion.mul(0.2, 1, 0.2)
                                .add(mine.getMotion().getX() + motiveX, 0, mine.getMotion().getZ() + motiveZ)
                                .mul(0.95, 1, 0.95);
                    } else {
                        motX /= 2;
                        motZ /= 2;
                        this.motion = this.motion.mul(0.2, 1, 0.2)
                                .add(motX - motiveX, 0, motZ - motiveZ)
                                .mul(0.2, 1, 0.2)
                                .add(motX + motiveX, 0, motZ + motiveZ);
                    }
                } else {
                    this.motion = motion.sub(motiveX, 0, motiveZ);
                }
            }
        }
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    protected void activate(int x, int y, int z, boolean flag) {
    }

    private boolean hasUpdated = false;

    private void setFalling() {
        this.motion = Vector3f.from(
                GenericMath.clamp(this.motion.getX(), -getMaxSpeed(), getMaxSpeed()),
                this.motion.getY(),
                GenericMath.clamp(this.motion.getZ(), -getMaxSpeed(), getMaxSpeed())
        );

        if (!hasUpdated) {
            for (Entity linked : passengers) {
                linked.setSeatPosition(getMountedOffset(linked).add(0, 0.35f, 0));
                updatePassengerPosition(linked);
            }

            hasUpdated = true;
        }

        if (onGround) {
            this.motion = this.motion.mul(derailed);
        }

        this.move(this.motion);
        if (!onGround) {
            this.motion = this.motion.mul(flying);
        }
    }

    private void processMovement(int dx, int dy, int dz, BlockRail block) {
        fallDistance = 0.0F;
        Vector3f vector = getNextRail(this.getPosition());

        int y = dy;

        boolean isPowered = false;
        boolean isSlowed = false;

        if (block instanceof BlockRailPowered) {
            isPowered = block.isActive();
            isSlowed = !block.isActive();
        }

        float motionX = this.motion.getX();
        float motionY = this.motion.getY();
        float motionZ = this.motion.getZ();

        switch (Orientation.byMetadata(block.getRealMeta())) {
            case ASCENDING_NORTH:
                motionX -= 0.0078125f;
                y += 1;
                break;
            case ASCENDING_SOUTH:
                motionX += 0.0078125f;
                y += 1;
                break;
            case ASCENDING_EAST:
                motionZ += 0.0078125f;
                y += 1;
                break;
            case ASCENDING_WEST:
                motionZ -= 0.0078125f;
                y += 1;
                break;
        }

        int[][] facing = matrix[block.getRealMeta()];
        float facing1 = facing[1][0] - facing[0][0];
        float facing2 = facing[1][2] - facing[0][2];
        float speedOnTurns = (float) Math.sqrt(facing1 * facing1 + facing2 * facing2);
        float realFacing = motionX * facing1 + motionZ * facing2;

        if (realFacing < 0) {
            facing1 = -facing1;
            facing2 = -facing2;
        }

        float squareOfFame = (float) Math.sqrt(motionX * motionX + motionZ * motionZ);

        if (squareOfFame > 2) {
            squareOfFame = 2;
        }

        motionX = squareOfFame * facing1 / speedOnTurns;
        motionZ = squareOfFame * facing2 / speedOnTurns;
        float expectedSpeed;
        float playerYawNeg; // PlayerYawNegative
        float playerYawPos; // PlayerYawPositive
        float motion;

        Entity linked = getPassenger();

        if (linked instanceof EntityLiving) {
            expectedSpeed = currentSpeed;
            if (expectedSpeed > 0) {
                // This is a trajectory (Angle of elevation)
                playerYawNeg = (float) -Math.sin(linked.getLocation().getYaw() * Math.PI / 180.0F);
                playerYawPos = (float) Math.cos(linked.getLocation().getYaw() * Math.PI / 180.0F);
                motion = motionX * motionX + motionZ * motionZ;
                if (motion < 0.01D) {
                    motionX += playerYawNeg * 0.1D;
                    motionZ += playerYawPos * 0.1D;

                    isSlowed = false;
                }
            }
        }

        //http://minecraft.gamepedia.com/Powered_Rail#Rail
        if (isSlowed) {
            expectedSpeed = (float) Math.sqrt(motionX * motionX + motionZ * motionZ);
            if (expectedSpeed < 0.03D) {
                this.motion = Vector3f.ZERO;
            } else {
                this.motion = this.motion.mul(0.5, 0, 0.5);
            }
        }

        playerYawNeg = dx + 0.5f + facing[0][0] * 0.5f;
        playerYawPos = dz + 0.5f + facing[0][2] * 0.5f;
        motion = dx + 0.5f + facing[1][0] * 0.5f;
        float wallOfFame = dz + 0.5f + facing[1][2] * 0.5f;

        facing1 = motion - playerYawNeg;
        facing2 = wallOfFame - playerYawPos;
        float motX;
        float motZ;
        float x = this.getX();
        float z = this.getZ();

        if (facing1 == 0) {
            x = dx + 0.5f;
            expectedSpeed = z - dz;
        } else if (facing2 == 0) {
            z = dz + 0.5f;
            expectedSpeed = x - dx;
        } else {
            motX = x - playerYawNeg;
            motZ = z - playerYawPos;
            expectedSpeed = (motX * facing1 + motZ * facing2) * 2;
        }

        x = playerYawNeg + facing1 * expectedSpeed;
        z = playerYawPos + facing2 * expectedSpeed;
        setPosition(Vector3f.from(x, y, z));

        motX = motionX;
        motZ = motionZ;
        if (!passengers.isEmpty()) {
            motX *= 0.75D;
            motZ *= 0.75D;
        }
        motX = (float) GenericMath.clamp(motX, -getMaxSpeed(), getMaxSpeed());
        motZ = (float) GenericMath.clamp(motZ, -getMaxSpeed(), getMaxSpeed());

        move(motX, 0, motZ);
        if (facing[0][1] != 0 && MathHelper.floor(x) - dx == facing[0][0] && MathHelper.floor(z) - dz == facing[0][2]) {
            setPosition(Vector3f.from(x, y + facing[0][1], z));
        } else if (facing[1][1] != 0 && MathHelper.floor(x) - dx == facing[1][0] && MathHelper.floor(z) - dz == facing[1][2]) {
            setPosition(Vector3f.from(x, y + facing[1][1], z));
        }

        applyDrag();
        Vector3f vector1 = getNextRail(x, y, z);

        if (vector1 != null && vector != null) {
            float d14 = (vector.getY() - vector1.getY()) * 0.05f;

            squareOfFame = (float) Math.sqrt(motionX * motionX + motionZ * motionZ);
            if (squareOfFame > 0) {
                motionX = motionX / squareOfFame * (squareOfFame + d14);
                motionZ = motionZ / squareOfFame * (squareOfFame + d14);
            }

            setPosition(Vector3f.from(x, vector1.getY(), z));
        }

        int floorX = MathHelper.floor(x);
        int floorZ = MathHelper.floor(z);

        if (floorX != dx || floorZ != dz) {
            squareOfFame = (float) Math.sqrt(motionX * motionX + motionZ * motionZ);
            motionX = squareOfFame * (floorX - dx);
            motionZ = squareOfFame * (floorZ - dz);
        }

        if (isPowered) {
            double newMovie = Math.sqrt(motionX * motionX + motionZ * motionZ);

            if (newMovie > 0.01D) {
                double nextMovie = 0.06D;

                motionX += motionX / newMovie * nextMovie;
                motionZ += motionZ / newMovie * nextMovie;
            } else if (block.getOrientation() == Orientation.STRAIGHT_NORTH_SOUTH) {
                if (level.getBlock(dx - 1, dy, dz).isNormalBlock()) {
                    motionX = 0.02f;
                } else if (level.getBlock(dx + 1, dy, dz).isNormalBlock()) {
                    motionX = -0.02f;
                }
            } else if (block.getOrientation() == Orientation.STRAIGHT_EAST_WEST) {
                if (level.getBlock(dx, dy, dz - 1).isNormalBlock()) {
                    motionZ = 0.02f;
                } else if (level.getBlock(dx, dy, dz + 1).isNormalBlock()) {
                    motionZ = -0.02f;
                }
            }
        }
        this.motion = Vector3f.from(motionX, motionY, motionZ);
    }

    private void applyDrag() {
        if (!passengers.isEmpty() || !slowWhenEmpty) {
            this.motion = this.motion.mul(0.997, 0, 0.997);
        } else {
            this.motion = this.motion.mul(0.96, 0, 0.96);
        }
    }

    private Vector3f getNextRail(Vector3f d) {
        return getNextRail(d.getX(), d.getY(), d.getZ());
    }

    private Vector3f getNextRail(float dx, float dy, float dz) {
        int checkX = MathHelper.floor(dx);
        int checkY = MathHelper.floor(dy);
        int checkZ = MathHelper.floor(dz);

        if (Rail.isRailBlock(level.getBlockId(checkX, checkY - 1, checkZ))) {
            --checkY;
        }

        Block block = level.getBlock(checkX, checkY, checkZ);

        if (Rail.isRailBlock(block)) {
            int[][] facing = matrix[((BlockRail) block).getRealMeta()];
            float rail;
            // Genisys mistake (Doesn't check surrounding more exactly)
            float nextOne = checkX + 0.5f + facing[0][0] * 0.5f;
            float nextTwo = checkY + 0.5f + facing[0][1] * 0.5f;
            float nextThree = checkZ + 0.5f + facing[0][2] * 0.5f;
            float nextFour = checkX + 0.5f + facing[1][0] * 0.5f;
            float nextFive = checkY + 0.5f + facing[1][1] * 0.5f;
            float nextSix = checkZ + 0.5f + facing[1][2] * 0.5f;
            float nextSeven = nextFour - nextOne;
            float nextEight = (nextFive - nextTwo) * 2;
            float nextMax = nextSix - nextThree;

            if (nextSeven == 0) {
                rail = dz - checkZ;
            } else if (nextMax == 0) {
                rail = dx - checkX;
            } else {
                float whatOne = dx - nextOne;
                float whatTwo = dz - nextThree;

                rail = (whatOne * nextSeven + whatTwo * nextMax) * 2;
            }

            dx = nextOne + nextSeven * rail;
            dy = nextTwo + nextEight * rail;
            dz = nextThree + nextMax * rail;
            if (nextEight < 0) {
                ++dy;
            }

            if (nextEight > 0) {
                dy += 0.5D;
            }

            return Vector3f.from(dx, dy, dz);
        } else {
            return null;
        }
    }

    /**
     * Used to multiply the minecart current speed
     *
     * @param speed The speed of the minecart that will be calculated
     */
    public void setCurrentSpeed(float speed) {
        currentSpeed = speed;
    }

    /**
     * Get the block display offset
     *
     * @return integer
     */
    public int getDisplayOffset() {
        return this.data.getInt(DISPLAY_OFFSET);
    }

    /**
     * Set the block offset.
     *
     * @param offset The offset
     */
    public void setDisplayBlockOffset(int offset) {
        this.data.setInt(DISPLAY_OFFSET, offset);
    }

    public Block getDisplayBlock() {
        int runtimeId = this.data.getInt(DISPLAY_ITEM);
        return BlockRegistry.get().getBlock(runtimeId).clone();
    }

    public void setDisplayBlock(Block block) {
        int runtimeId = BlockRegistry.get().getRuntimeId(block);
        this.data.setInt(DISPLAY_ITEM, runtimeId);
    }

    public boolean hasDisplay() {
        return this.data.getBoolean(CUSTOM_DISPLAY);
    }

    public void setDisplay(boolean display) {
        this.data.setBoolean(CUSTOM_DISPLAY, true);
    }

    /**
     * Is the minecart can be slowed when empty?
     *
     * @return boolean
     */
    public boolean isSlowWhenEmpty() {
        return slowWhenEmpty;
    }

    /**
     * Set the minecart slowdown flag
     *
     * @param slow The slowdown flag
     */
    public void setSlowWhenEmpty(boolean slow) {
        slowWhenEmpty = slow;
    }

    public Vector3f getFlyingVelocityMod() {
        return flying;
    }

    public void setFlyingVelocityMod(Vector3f flying) {
        Objects.requireNonNull(flying, "Flying velocity modifiers cannot be null");
        this.flying = flying;
    }

    public Vector3f getDerailedVelocityMod() {
        return derailed;
    }

    public void setDerailedVelocityMod(Vector3f derailed) {
        Objects.requireNonNull(derailed, "Derailed velocity modifiers cannot be null");
        this.derailed = derailed;
    }

    public void setMaximumSpeed(float speed) {
        maxSpeed = speed;
    }
}
