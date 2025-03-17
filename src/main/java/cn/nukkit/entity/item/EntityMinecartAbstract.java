package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.block.*;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityHopper;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityControllable;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.vehicle.VehicleMoveEvent;
import cn.nukkit.event.vehicle.VehicleUpdateEvent;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.*;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.MinecartType;
import cn.nukkit.utils.Rail;
import cn.nukkit.utils.Rail.Orientation;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by: larryTheCoder on 2017/6/26.
 * <p>
 * Nukkit Project,
 * Minecart and Riding Project,
 * Package cn.nukkit.entity.item in project Nukkit.
 */
public abstract class EntityMinecartAbstract extends EntityVehicle implements EntityControllable {

    private String entityName;
    private static final int[][][] matrix = {
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
    private double currentSpeed = 0;
    private Block blockInside;
    // Plugins modifiers
    private boolean slowWhenEmpty = true;
    private double derailedX = 0.5;
    private double derailedY = 0.5;
    private double derailedZ = 0.5;
    private double flyingX = 0.95;
    private double flyingY = 0.95;
    private double flyingZ = 0.95;
    private double maxSpeed = 0.4D;

    public abstract MinecartType getType();

    public abstract boolean isRideable();

    public EntityMinecartAbstract(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getHeight() {
        return 0.7F;
    }

    @Override
    public float getWidth() {
        return 0.98F;
    }

    @Override
    protected float getDrag() {
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
    public boolean hasCustomName() {
        return entityName != null;
    }

    @Override
    public boolean canDoInteraction() {
        return passengers.isEmpty() && this.blockInside == null;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(40);
        super.initEntity();

        this.setHealth(40);
        //this.prepareDataProperty(); // TODO: DATA_DISPLAY_ITEM NBTEntityData
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        // The damage token
        if (getHealth() < 20) {
            setHealth(getHealth() + 1);
        }

        // Entity variables
        lastX = x;
        lastY = y;
        lastZ = z;
        motionY -= 0.04;
        int dx = MathHelper.floor(x);
        int dy = MathHelper.floor(y);
        int dz = MathHelper.floor(z);

        // Some hack to check rails
        if (Rail.isRailBlock(level.getBlockIdAt(dx, dy - 1, dz))) {
            --dy;
        }

        Block block = level.getBlock(chunk, dx, dy, dz, true);

        // Ensure that the block is a rail
        if (Rail.isRailBlock(block)) {
            processMovement(dx, dy, dz, (BlockRail) block);
            // Activate the minecart/TNT
            if (block instanceof BlockRailActivator) {
                activate(dx, dy, dz, ((BlockRailActivator) block).isActive());
            }
        } else {
            setFalling();
        }

        // Minecart head
        pitch = 0;
        double diffX = this.lastX - this.x;
        double diffZ = this.lastZ - this.z;
        double yawToChange = yaw;
        if (diffX * diffX + diffZ * diffZ > 0.001D) {
            yawToChange = (Math.atan2(diffZ, diffX) * 180 / Math.PI);
        }

        // Reverse yaw if yaw is below 0
        if (yawToChange < 0) {
            // -90-(-90)-(-90) = 90
            yawToChange -= yawToChange - yawToChange;
        }

        setRotation(yawToChange, pitch);

        Location from = new Location(lastX, lastY, lastZ, lastYaw, lastPitch, level);
        Location to = new Location(this.x, this.y, this.z, this.yaw, this.pitch, level);

        this.getServer().getPluginManager().callEvent(new VehicleUpdateEvent(this));

        if (!from.equals(to)) {
            this.getServer().getPluginManager().callEvent(new VehicleMoveEvent(this, from, to));
        }

        // Collisions
        if (this instanceof InventoryHolder) {
            for (Entity entity : level.getNearbyEntities(boundingBox.grow(0.2D, 0, 0.2D), this)) {
                if (entity instanceof EntityMinecartAbstract && !passengers.contains(entity)) {
                    entity.applyEntityCollision(this);
                }
            }
        }

        if (this instanceof InventoryHolder) {
            AxisAlignedBB pickupArea = new SimpleAxisAlignedBB(this.x, this.y - 1, this.z, this.x + 1, this.y, this.z + 1);
            Block[] hopperPickupArray = this.level.getCollisionBlocks(this, pickupArea, false);
            if (hopperPickupArray.length >= 1) {
                Block hopper = hopperPickupArray[0];
                if (hopper instanceof BlockHopper) {
                    BlockEntity hopperBE = hopper.getLevel().getBlockEntityIfLoaded(hopper);
                    if (hopperBE instanceof BlockEntityHopper) {
                        ((BlockEntityHopper) hopperBE).setMinecartPickupInventory((InventoryHolder) this);
                    }
                }
                return true;
            }

            if (!(this instanceof EntityMinecartHopper)) {
                AxisAlignedBB pushArea = new SimpleAxisAlignedBB(this.x, this.y, this.z, this.x + 1, this.y + 2, this.z + 1);
                Block[] hopperPushArray = this.level.getCollisionBlocks(this, pushArea, false);
                if (hopperPushArray.length >= 1) {
                    Block hopper = hopperPushArray[0];
                    if (hopper instanceof BlockHopper) {
                        BlockEntity hopperBE = hopper.getLevel().getBlockEntityIfLoaded(hopper);
                        if (hopperBE instanceof BlockEntityHopper) {
                            ((BlockEntityHopper) hopperBE).setMinecartPushInventory((InventoryHolder) this);
                        }
                    }
                    return true;
                }
            }
        }

        // We call super here after movement code so block collision checks use up to date position
        return super.entityBaseTick(tickDiff) || this.getRollingAmplitude() > 0 || !(this.motionX == 0 && this.motionY == 0 && this.motionZ == 0);
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
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) this.lastDamageCause).getDamager();
            if (damager instanceof Player && ((Player) damager).isCreative()) {
                return;
            }
        }
        this.level.dropItem(this, Item.get(Item.MINECART));
    }

    @Override
    public void kill() {
        if (!this.isAlive()) {
            return;
        }

        super.kill();

        if (level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
            this.dropItem();
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
    public boolean onInteract(Player p, Item item, Vector3 clickedPos) {
        if (!isRideable()) {
            return false;
        }

        if (!passengers.isEmpty()) {
            return false;
        }

        if (blockInside == null) {
            mountEntity(p);
        }

        return super.onInteract(p, item, clickedPos);
    }

    @Override
    public void applyEntityCollision(cn.nukkit.entity.Entity entity) {
        if (entity != riding && !(entity instanceof Player && ((Player) entity).getGamemode() == Player.SPECTATOR)) {
            double motiveX = entity.x - x;
            double motiveZ = entity.z - z;
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
                motiveX *= 0.10000000149011612D;
                motiveZ *= 0.10000000149011612D;
                motiveX *= 1 + entityCollisionReduction;
                motiveZ *= 1 + entityCollisionReduction;
                motiveX *= 0.5D;
                motiveZ *= 0.5D;
                if (entity instanceof EntityMinecartAbstract) {
                    EntityMinecartAbstract mine = (EntityMinecartAbstract) entity;
                    double desinityX = mine.x - x;
                    double desinityZ = mine.z - z;
                    Vector3 vector = new Vector3(desinityX, 0, desinityZ).normalize();
                    Vector3 vec = new Vector3(MathHelper.cos((float) yaw * 0.017453292F), 0, MathHelper.sin((float) yaw * 0.017453292F)).normalize();
                    double desinityXZ = Math.abs(vector.dot(vec));

                    if (desinityXZ < 0.800000011920929D) {
                        return;
                    }

                    double motX = mine.motionX + motionX;
                    double motZ = mine.motionZ + motionZ;

                    if (mine.getType().getId() == 2 && getType().getId() != 2) {
                        motionX *= 0.20000000298023224D;
                        motionZ *= 0.20000000298023224D;
                        motionX += mine.motionX - motiveX;
                        motionZ += mine.motionZ - motiveZ;
                        mine.motionX *= 0.949999988079071D;
                        mine.motionZ *= 0.949999988079071D;
                    } else if (mine.getType().getId() != 2 && getType().getId() == 2) {
                        mine.motionX *= 0.20000000298023224D;
                        mine.motionZ *= 0.20000000298023224D;
                        motionX += mine.motionX + motiveX;
                        motionZ += mine.motionZ + motiveZ;
                        motionX *= 0.949999988079071D;
                        motionZ *= 0.949999988079071D;
                    } else {
                        motX /= 2;
                        motZ /= 2;
                        motionX *= 0.20000000298023224D;
                        motionZ *= 0.20000000298023224D;
                        motionX += motX - motiveX;
                        motionZ += motZ - motiveZ;
                        mine.motionX *= 0.20000000298023224D;
                        mine.motionZ *= 0.20000000298023224D;
                        mine.motionX += motX + motiveX;
                        mine.motionZ += motZ + motiveZ;
                    }
                } else {
                    motionX -= motiveX;
                    motionZ -= motiveZ;
                }
            }
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        saveEntityData();
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    protected void activate(int x, int y, int z, boolean flag) {
    }

    private boolean hasUpdated = false;

    private void setFalling() {
        motionX = NukkitMath.clamp(motionX, -maxSpeed, maxSpeed);
        motionZ = NukkitMath.clamp(motionZ, -maxSpeed, maxSpeed);

        if (!hasUpdated) {
            for (cn.nukkit.entity.Entity linked : passengers) {
                linked.setSeatPosition(getMountedOffset(linked).add(0, 0.35f));
                updatePassengerPosition(linked);
            }

            hasUpdated = true;
        }

        if (onGround) {
            motionX *= derailedX;
            motionY *= derailedY;
            motionZ *= derailedZ;
        }

        move(motionX, motionY, motionZ);
        if (!onGround) {
            motionX *= flyingX;
            motionY *= flyingY;
            motionZ *= flyingZ;
        }
    }

    private final Vector3 tempMoveVec = new Vector3(0, 0, 0);

    private void processMovement(int dx, int dy, int dz, BlockRail block) {
        fallDistance = 0.0F;
        Vector3 vector = getNextRail(x, y, z);

        int y = dy;
        boolean isPowered = false;
        boolean isSlowed = false;

        if (block instanceof BlockRailPowered) {
            isPowered = block.isActive();
            isSlowed = !block.isActive();
        }

        switch (Orientation.byMetadata(block.getRealMeta())) {
            case ASCENDING_NORTH:
                motionX -= 0.0078125D;
                y += 1;
                break;
            case ASCENDING_SOUTH:
                motionX += 0.0078125D;
                y += 1;
                break;
            case ASCENDING_EAST:
                motionZ += 0.0078125D;
                y += 1;
                break;
            case ASCENDING_WEST:
                motionZ -= 0.0078125D;
                y += 1;
                break;
        }

        int[][] facing = matrix[block.getRealMeta()];
        double facing1 = facing[1][0] - facing[0][0];
        double facing2 = facing[1][2] - facing[0][2];
        double speedOnTurns = Math.sqrt(facing1 * facing1 + facing2 * facing2);
        double realFacing = motionX * facing1 + motionZ * facing2;

        if (realFacing < 0) {
            facing1 = -facing1;
            facing2 = -facing2;
        }

        double squareOfFame = Math.sqrt(motionX * motionX + motionZ * motionZ);

        if (squareOfFame > 2) {
            squareOfFame = 2;
        }

        motionX = squareOfFame * facing1 / speedOnTurns;
        motionZ = squareOfFame * facing2 / speedOnTurns;
        double expectedSpeed;
        double playerYawNeg; // PlayerYawNegative
        double playerYawPos; // PlayerYawPositive
        double motion;

        cn.nukkit.entity.Entity linked = getPassenger();

        if (linked instanceof EntityLiving) {
            expectedSpeed = currentSpeed;
            if (expectedSpeed > 0) {
                // This is a trajectory (Angle of elevation)
                playerYawNeg = -Math.sin(linked.yaw * Math.PI / 180.0F);
                playerYawPos = Math.cos(linked.yaw * Math.PI / 180.0F);
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
            expectedSpeed = Math.sqrt(motionX * motionX + motionZ * motionZ);
            if (expectedSpeed < 0.03D) {
                motionX *= 0;
                motionY *= 0;
                motionZ *= 0;
            } else {
                motionX *= 0.5D;
                motionY *= 0;
                motionZ *= 0.5D;
            }
        }

        playerYawNeg = (double) dx + 0.5D + (double) facing[0][0] * 0.5D;
        playerYawPos = (double) dz + 0.5D + (double) facing[0][2] * 0.5D;
        motion = (double) dx + 0.5D + (double) facing[1][0] * 0.5D;
        double wallOfFame = (double) dz + 0.5D + (double) facing[1][2] * 0.5D;

        facing1 = motion - playerYawNeg;
        facing2 = wallOfFame - playerYawPos;
        double motX;
        double motZ;

        if (facing1 == 0) {
            //x = (double) dx + 0.5D;
            expectedSpeed = z - (double) dz;
        } else if (facing2 == 0) {
            //z = (double) dz + 0.5D;
            expectedSpeed = x - (double) dx;
        } else {
            motX = x - playerYawNeg;
            motZ = z - playerYawPos;
            expectedSpeed = (motX * facing1 + motZ * facing2) * 2;
        }

        x = playerYawNeg + facing1 * expectedSpeed;
        z = playerYawPos + facing2 * expectedSpeed;
        this.y = y; // Idk why we set these before setPosition
        setPosition(tempMoveVec.setComponents(x, y, z));

        motX = motionX;
        motZ = motionZ;
        if (!passengers.isEmpty()) {
            motX *= 0.75D;
            motZ *= 0.75D;
        }
        motX = NukkitMath.clamp(motX, -maxSpeed, maxSpeed);
        motZ = NukkitMath.clamp(motZ, -maxSpeed, maxSpeed);

        move(motX, 0, motZ);
        if (facing[0][1] != 0 && MathHelper.floor(x) - dx == facing[0][0] && MathHelper.floor(z) - dz == facing[0][2]) {
            setPosition(tempMoveVec.setComponents(x, y + (double) facing[0][1], z));
        } else if (facing[1][1] != 0 && MathHelper.floor(x) - dx == facing[1][0] && MathHelper.floor(z) - dz == facing[1][2]) {
            setPosition(tempMoveVec.setComponents(x, y + (double) facing[1][1], z));
        }

        applyDrag();
        Vector3 vector1 = getNextRail(x, y, z);

        if (vector1 != null && vector != null) {
            double d14 = (vector.y - vector1.y) * 0.05D;

            squareOfFame = Math.sqrt(motionX * motionX + motionZ * motionZ);
            if (squareOfFame > 0) {
                motionX = motionX / squareOfFame * (squareOfFame + d14);
                motionZ = motionZ / squareOfFame * (squareOfFame + d14);
            }

            setPosition(tempMoveVec.setComponents(x, vector1.y, z));
        }

        int floorX = MathHelper.floor(x);
        int floorZ = MathHelper.floor(z);

        if (floorX != dx || floorZ != dz) {
            squareOfFame = Math.sqrt(motionX * motionX + motionZ * motionZ);
            motionX = squareOfFame * (double) (floorX - dx);
            motionZ = squareOfFame * (double) (floorZ - dz);
        }

        if (isPowered) {
            double newMovie = Math.sqrt(motionX * motionX + motionZ * motionZ);

            if (newMovie > 0.01D) {
                double nextMovie = 0.06D;

                motionX += motionX / newMovie * nextMovie;
                motionZ += motionZ / newMovie * nextMovie;
            } else if (block.getOrientation() == Orientation.STRAIGHT_NORTH_SOUTH) {
                if (level.getBlock(chunk, dx - 1, dy, dz, true).isNormalBlock()) {
                    motionX = 0.02D;
                } else if (level.getBlock(chunk, dx + 1, dy, dz, true).isNormalBlock()) {
                    motionX = -0.02;
                }
            } else if (block.getOrientation() == Orientation.STRAIGHT_EAST_WEST) {
                if (level.getBlock(chunk, dx, dy, dz - 1, true).isNormalBlock()) {
                    motionZ = 0.02D;
                } else if (level.getBlock(chunk, dx, dy, dz + 1, true).isNormalBlock()) {
                    motionZ = -0.02;
                }
            }
        }
    }

    private void applyDrag() {
        if (!passengers.isEmpty() || !slowWhenEmpty) {
            motionX *= 0.996999979019165D;
            motionY *= 0.0D;
            motionZ *= 0.996999979019165D;
        } else {
            motionX *= 0.9599999785423279D;
            motionY *= 0.0D;
            motionZ *= 0.9599999785423279D;
        }
    }

    private Vector3 getNextRail(double dx, double dy, double dz) {
        int checkX = MathHelper.floor(dx);
        int checkY = MathHelper.floor(dy);
        int checkZ = MathHelper.floor(dz);

        if (Rail.isRailBlock(level.getBlockIdAt(checkX, checkY - 1, checkZ))) {
            --checkY;
        }

        Block block = level.getBlock(chunk, checkX, checkY, checkZ, true);

        if (Rail.isRailBlock(block)) {
            int[][] facing = matrix[((BlockRail) block).getRealMeta()];
            double rail;
            // Genisys mistake (Doesn't check surrounding more exactly)
            double nextOne = (double) checkX + 0.5D + (double) facing[0][0] * 0.5D;
            double nextTwo = (double) checkY + 0.5D + (double) facing[0][1] * 0.5D;
            double nextThree = (double) checkZ + 0.5D + (double) facing[0][2] * 0.5D;
            double nextFour = (double) checkX + 0.5D + (double) facing[1][0] * 0.5D;
            double nextFive = (double) checkY + 0.5D + (double) facing[1][1] * 0.5D;
            double nextSix = (double) checkZ + 0.5D + (double) facing[1][2] * 0.5D;
            double nextSeven = nextFour - nextOne;
            double nextEight = (nextFive - nextTwo) * 2;
            double nextMax = nextSix - nextThree;

            if (nextSeven == 0) {
                rail = dz - (double) checkZ;
            } else if (nextMax == 0) {
                rail = dx - (double) checkX;
            } else {
                double whatOne = dx - nextOne;
                double whatTwo = dz - nextThree;

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

            return new Vector3(dx, dy, dz);
        } else {
            return null;
        }
    }

    @Override
    public void onPlayerInput(Player player, double strafe, double forward) {
        this.setCurrentSpeed(forward);
    }

    /**
     * Used to multiply the minecart current speed
     *
     * @param speed The speed of the minecart that will be calculated
     */
    public void setCurrentSpeed(double speed) {
        this.currentSpeed = speed;
    }

    /*private void prepareDataProperty() {
        setRollingAmplitude(0);
        setRollingDirection(1);
        if (namedTag.contains("CustomDisplayTile")) {
            if (namedTag.getBoolean("CustomDisplayTile")) {
                int display = namedTag.getInt("DisplayTile");
                int offSet = namedTag.getInt("DisplayOffset");
                setDataProperty(new ByteEntityData(DATA_HAS_DISPLAY, 1));
                setDataProperty(new IntEntityData(DATA_DISPLAY_ITEM, display));
                setDataProperty(new IntEntityData(DATA_DISPLAY_OFFSET, offSet));
            }
        } else {
            int display = blockInside == null ? 0
                    : blockInside.getId()
                    | blockInside.getDamage() << 16;
            if (display == 0) {
                setDataProperty(new ByteEntityData(DATA_HAS_DISPLAY, 0));
                return;
            }
            setDataProperty(new ByteEntityData(DATA_HAS_DISPLAY, 1));
            setDataProperty(new IntEntityData(DATA_DISPLAY_ITEM, display));
            setDataProperty(new IntEntityData(DATA_DISPLAY_OFFSET, 6));
        }
    }*/

    private void saveEntityData() {
        boolean hasDisplay = super.getDataPropertyByte(DATA_HAS_DISPLAY) == 1 || blockInside != null;
        int display;
        int offSet;
        namedTag.putBoolean("CustomDisplayTile", hasDisplay);
        if (hasDisplay) {
            display = blockInside.getId() | blockInside.getDamage() << 16;
            offSet = getDataPropertyInt(DATA_DISPLAY_OFFSET);
            namedTag.putInt("DisplayTile", display);
            namedTag.putInt("DisplayOffset", offSet);
        }
    }

    /**
     * Set the minecart display block
     *
     * @param block The block that will changed. Set {@code null} for BlockAir
     * @return {@code true} if the block is normal block
     */
    public boolean setDisplayBlock(Block block) {
        return setDisplayBlock(block, true);
    }

    /**
     * Set the minecart display block
     *
     * @param block The block that will changed. Set {@code null} for BlockAir
     * @param update Do update for the block. (This state changes if you want to show the block)
     * @return {@code true} if the block is normal block
     */
    public boolean setDisplayBlock(Block block, boolean update) {
        if (!update) {
            if (block.isNormalBlock()) {
                blockInside = block;
            } else {
                blockInside = null;
            }
            return true;
        }
        //TODO: DATA_DISPLAY_ITEM NBTEntityData
        if (block != null) {
            if (block.isNormalBlock()) {
                blockInside = block;
                int display = blockInside.getId()
                        | blockInside.getDamage() << 16;
                setDataProperty(new ByteEntityData(DATA_HAS_DISPLAY, 1));
                setDataProperty(new IntEntityData(DATA_DISPLAY_ITEM, display));
                setDisplayBlockOffset(6);
            }
        } else {
            // Set block to air (default).
            blockInside = null;
            setDataProperty(new ByteEntityData(DATA_HAS_DISPLAY, 0));
            setDataProperty(new IntEntityData(DATA_DISPLAY_ITEM, 0));
            setDisplayBlockOffset(0);
        }
        return true;
    }

    /**
     * Get the minecart display block
     *
     * @return Block of minecart display block
     */
    public Block getDisplayBlock() {
        return blockInside;
    }

    /**
     * Set the block offset.
     *
     * @param offset The offset
     */
    public void setDisplayBlockOffset(int offset) {
        setDataProperty(new IntEntityData(DATA_DISPLAY_OFFSET, offset));
    }

    /**
     * Get the block display offset
     *
     * @return integer
     */
    public int getDisplayBlockOffset() {
        return super.getDataPropertyInt(DATA_DISPLAY_OFFSET);
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

    public Vector3 getFlyingVelocityMod() {
        return new Vector3(flyingX, flyingY, flyingZ);
    }

    public void setFlyingVelocityMod(Vector3 flying) {
        Objects.requireNonNull(flying, "Flying velocity modifiers cannot be null");
        flyingX = flying.getX();
        flyingY = flying.getY();
        flyingZ = flying.getZ();
    }

    public Vector3 getDerailedVelocityMod() {
        return new Vector3(derailedX, derailedY, derailedZ);
    }

    public void setDerailedVelocityMod(Vector3 derailed) {
        Objects.requireNonNull(derailed, "Derailed velocity modifiers cannot be null");
        derailedX = derailed.getX();
        derailedY = derailed.getY();
        derailedZ = derailed.getZ();
    }

    public void setMaximumSpeed(double speed) {
        maxSpeed = speed;
    }

    @Override
    public String getInteractButtonText() {
        return "";
    }
}
