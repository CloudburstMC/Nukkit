package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBell;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.block.BellRingEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

public class BlockBell extends BlockTransparentMeta implements Faceable {

    public static final int TYPE_ATTACHMENT_STANDING = 0;
    public static final int TYPE_ATTACHMENT_HANGING = 1;
    public static final int TYPE_ATTACHMENT_SIDE = 2;
    public static final int TYPE_ATTACHMENT_MULTIPLE = 3;

    public BlockBell() {
        this(0);
    }

    public BlockBell(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Bell";
    }

    @Override
    public int getId() {
        return BELL;
    }

    private boolean isConnectedTo(BlockFace connectedFace, int attachmentType, BlockFace blockFace) {
        BlockFace.Axis faceAxis = connectedFace.getAxis();
        switch (attachmentType) {
            case TYPE_ATTACHMENT_STANDING:
                if (faceAxis == BlockFace.Axis.Y) {
                    return connectedFace == BlockFace.DOWN;
                } else {
                    return blockFace.getAxis() != faceAxis;
                }
            case TYPE_ATTACHMENT_HANGING:
                return connectedFace == BlockFace.UP;
            case TYPE_ATTACHMENT_SIDE:
                return connectedFace == blockFace.getOpposite();
            case TYPE_ATTACHMENT_MULTIPLE:
                return connectedFace == blockFace || connectedFace == blockFace.getOpposite();
        }
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        int attachmentType = getAttachmentType();
        BlockFace blockFace = getBlockFace();
        boolean north = this.isConnectedTo(BlockFace.NORTH, attachmentType, blockFace);
        boolean south = this.isConnectedTo(BlockFace.SOUTH, attachmentType, blockFace);
        boolean west = this.isConnectedTo(BlockFace.WEST, attachmentType, blockFace);
        boolean east = this.isConnectedTo(BlockFace.EAST, attachmentType, blockFace);
        boolean up = this.isConnectedTo(BlockFace.UP, attachmentType, blockFace);
        boolean down = this.isConnectedTo(BlockFace.DOWN, attachmentType, blockFace);

        double n = north ? 0 : 0.25;
        double s = south ? 1 : 0.75;
        double w = west ? 0 : 0.25;
        double e = east ? 1 : 0.75;
        double d = down ? 0 : 0.25;
        double u = up ? 1 : 0.75;

        return new SimpleAxisAlignedBB(this.x + w, this.y + d, this.z + n, this.x + e, this.y + u, this.z + s);
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (entity instanceof EntityItem && entity.getMotion().lengthSquared() > 0.01) {
            AxisAlignedBB boundingBox = entity.getBoundingBox();
            AxisAlignedBB blockBoundingBox = this.getCollisionBoundingBox();
            if (boundingBox.intersectsWith(blockBoundingBox)) {
                Vector3 entityCenter = new Vector3(
                        (boundingBox.getMaxX() - boundingBox.getMinX()) / 2,
                        (boundingBox.getMaxY() - boundingBox.getMinY()) / 2,
                        (boundingBox.getMaxZ() - boundingBox.getMinZ()) / 2
                );

                Vector3 blockCenter = new Vector3(
                        (blockBoundingBox.getMaxX() - blockBoundingBox.getMinX()) / 2,
                        (blockBoundingBox.getMaxY() - blockBoundingBox.getMinY()) / 2,
                        (blockBoundingBox.getMaxZ() - blockBoundingBox.getMinZ()) / 2
                );
                Vector3 entityPos = entity.add(entityCenter);
                Vector3 blockPos = this.add(
                        blockBoundingBox.getMinX() - x + blockCenter.x,
                        blockBoundingBox.getMinY() - y + blockCenter.y,
                        blockBoundingBox.getMinZ() - z + blockCenter.z
                );

                Vector3 entityVector = entityPos.subtract(blockPos);
                entityVector = entityVector.normalize().multiply(0.4);
                entityVector.y = Math.max(0.15, entityVector.y);
                if (this.ring(entity, BellRingEvent.RingCause.DROPPED_ITEM)) {
                    entity.setMotion(entityVector);
                }
            }
        }
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return recalculateBoundingBox().expand(0.000001, 0.000001, 0.000001);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        return this.ring(player, player != null? BellRingEvent.RingCause.HUMAN_INTERACTION : BellRingEvent.RingCause.UNKNOWN);
    }

    public boolean ring(Entity causeEntity, BellRingEvent.RingCause cause) {
        return this.ring(causeEntity, cause, null);
    }

    public boolean ring(Entity causeEntity, BellRingEvent.RingCause cause, BlockFace hitFace) {
        BlockEntityBell bell = this.getOrCreateBlockEntity();
        if (bell == null) {
            return true;
        }
        boolean addException = true;
        BlockFace blockFace = getBlockFace();
        if (hitFace == null) {
            if (causeEntity != null) {
                if (causeEntity instanceof EntityItem) {
                    Position blockMid = this.add(0.5, 0.5, 0.5);
                    Vector3 vector = causeEntity.subtract(blockMid).normalize();
                    int x = vector.x < 0? -1 : vector.x > 0? 1 : 0;
                    int z = vector.z < 0? -1 : vector.z > 0? 1 : 0;
                    if (x != 0 && z != 0) {
                        if (Math.abs(vector.x) < Math.abs(vector.z)) {
                            x = 0;
                        } else {
                            z = 0;
                        }
                    }
                    hitFace = blockFace;
                    for (BlockFace face : BlockFace.values()) {
                        if (face.getXOffset() == x && face.getZOffset() == z) {
                            hitFace = face;
                            break;
                        }
                    }
                } else {
                    hitFace = causeEntity.getDirection();
                }
            } else {
                hitFace = blockFace;
            }
        }
        switch (this.getAttachmentType()) {
            case TYPE_ATTACHMENT_STANDING:
                if (hitFace.getAxis() != blockFace.getAxis()) {
                    return false;
                }
                break;
            case TYPE_ATTACHMENT_MULTIPLE:
                if (hitFace.getAxis() == blockFace.getAxis()) {
                    return false;
                }
                break;
            case TYPE_ATTACHMENT_SIDE:
                if (hitFace.getAxis() == blockFace.getAxis()) {
                    addException = false;
                }
                break;
        }

        BellRingEvent event = new BellRingEvent(this, cause, causeEntity);
        this.level.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        bell.setDirection(hitFace.getOpposite().getHorizontalIndex());
        bell.setTicks(0);
        bell.setRinging(true);
        if (addException && causeEntity instanceof Player) {
            bell.spawnExceptions.add(causeEntity.getId());
        }
        this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BLOCK_BELL_HIT);
        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean checkSupport() {
        switch (this.getAttachmentType()) {
            case TYPE_ATTACHMENT_STANDING:
                if (this.checkSupport(this.down(), BlockFace.UP)) {
                    return true;
                }
                break;
            case TYPE_ATTACHMENT_HANGING:
                if (this.checkSupport(this.up(), BlockFace.DOWN)) {
                    return true;
                }
                break;
            case TYPE_ATTACHMENT_MULTIPLE:
                BlockFace blockFace = getBlockFace();
                if (this.checkSupport(this.getSide(blockFace), blockFace.getOpposite()) &&
                        this.checkSupport(this.getSide(blockFace.getOpposite()), blockFace)) {
                    return true;
                }
                break;
            case TYPE_ATTACHMENT_SIDE:
                blockFace = getBlockFace();
                if (this.checkSupport(this.getSide(blockFace.getOpposite()), blockFace)) {
                    return true;
                }
                break;
        }

        return false;
    }

    private boolean checkSupport(Block support, BlockFace attachmentFace) {
        if (!support.isTransparent()) {
            return true;
        }

        if (attachmentFace == BlockFace.DOWN) {
            switch (support.getId()) {
                case HOPPER_BLOCK:
                case IRON_BARS:
                    return true;
                default:
                    return support instanceof BlockFence;
            }
        }

        if (support instanceof BlockCauldron) {
            return attachmentFace == BlockFace.UP;
        }

        if (attachmentFace == BlockFace.UP) {
            return Block.canStayOnFullSolid(support);
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!checkSupport()) {
                this.level.useBreakOn(this);
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_REDSTONE) {
            if (level.isBlockPowered(this)) {
                if (!isToggled()) {
                    setToggled(true);
                    this.level.setBlock(this, this, true, true);
                    ring(null, BellRingEvent.RingCause.REDSTONE);
                }
            } else if (isToggled()) {
                setToggled(false);
                this.level.setBlock(this, this, true, true);
            }
            return type;
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (block.canBeReplaced() && block.getId() != AIR && !(block instanceof BlockLiquid)) {
            face = BlockFace.UP;
        }
        switch (face) {
            case UP:
                this.setAttachmentType(TYPE_ATTACHMENT_STANDING);
                this.setBlockFace(player.getDirection().getOpposite());
                break;
            case DOWN:
                this.setAttachmentType(TYPE_ATTACHMENT_HANGING);
                this.setBlockFace(player.getDirection().getOpposite());
                break;
            default:
                this.setBlockFace(face);
                if (this.checkSupport(block.getSide(face), face.getOpposite())) {
                    this.setAttachmentType(TYPE_ATTACHMENT_MULTIPLE);
                } else {
                    this.setAttachmentType(TYPE_ATTACHMENT_SIDE);
                }
        }
        if (!this.checkSupport()) {
            return false;
        }
        this.getLevel().setBlock(this, this, true, true);
        this.createBlockEntity();
        return true;
    }

    private BlockEntityBell createBlockEntity() {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.BELL);
        return (BlockEntityBell) BlockEntity.createBlockEntity(BlockEntity.BELL, this.getChunk(), nbt);
    }

    private BlockEntityBell getOrCreateBlockEntity() {
        BlockEntity blockEntity = this.getLevel().getBlockEntity(this);
        if (!(blockEntity instanceof BlockEntityBell)) {
            blockEntity = this.createBlockEntity();
        }
        return (BlockEntityBell) blockEntity;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getDamage() & 0b11);
    }

    public void setBlockFace(BlockFace face) {
        if (face.getHorizontalIndex() == -1) {
            return;
        }
        this.setDamage(this.getDamage() & (DATA_MASK ^ 0b11) | face.getHorizontalIndex());
    }

    public int getAttachmentType() {
        return (this.getDamage() & 0b1100) >> 2 & 0b11;
    }

    public void setAttachmentType(int attachmentType) {
        attachmentType = attachmentType & 0b11;
        this.setDamage(getDamage() & (DATA_MASK ^ 0b1100) | (attachmentType << 2));
    }

    public boolean isToggled() {
        return (this.getDamage() & 0b010000) == 0b010000;
    }

    public void setToggled(boolean toggled) {
        this.setDamage(this.getDamage() & (DATA_MASK ^ 0b010000) | (toggled? 0b010000 : 0b000000));
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public double getResistance() {
        return 5;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GOLD_BLOCK_COLOR;
    }

    @Override
    public boolean canBePushed() {
        return false; // prevent item loss issue with pistons until a working implementation
    }
}