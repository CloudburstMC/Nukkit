package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityMovingBlock;
import cn.nukkit.blockentity.BlockEntityPistonArm;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.blockstate.BlockStateRegistry;
import cn.nukkit.event.block.BlockPistonEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.MainLogger;
import cn.nukkit.utils.RedstoneComponent;
import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author CreeperFace
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent.", since = "1.4.0.0-PN")
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
@Log4j2
public abstract class BlockPistonBase extends BlockSolidMeta implements RedstoneComponent, Faceable, BlockEntityHolder<BlockEntityPistonArm> {

    public boolean sticky;

    public BlockPistonBase() {
        this(0);
    }

    public BlockPistonBase(int meta) {
        super(meta);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.PISTON_ARM;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public Class<? extends BlockEntityPistonArm> getBlockEntityClass() {
        return BlockEntityPistonArm.class;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    @PowerNukkitDifference(info = "Using new method for checking if powered", since = "1.4.0.0-PN")
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (player != null) {
            if (Math.abs(player.getFloorX() - this.x) <= 1 && Math.abs(player.getFloorZ() - this.z) <= 1) {
                double y = player.y + player.getEyeHeight();

                if (y - this.y > 2) {
                    this.setDamage(BlockFace.UP.getIndex());
                } else if (this.y - y > 0) {
                    this.setDamage(BlockFace.DOWN.getIndex());
                } else {
                    this.setDamage(player.getHorizontalFacing().getIndex());
                }
            } else {
                this.setDamage(player.getHorizontalFacing().getIndex());
            }
        }

        if(this.level.getBlockEntity(this) != null) {
            BlockEntity blockEntity = this.level.getBlockEntity(this);
            log.warn("Found unused BlockEntity at world={} x={} y={} z={} whilst attempting to place piston, closing it.", blockEntity.getLevel().getName(), blockEntity.getX(), blockEntity.getY(), blockEntity.getZ());
            blockEntity.saveNBT();
            blockEntity.close();
        }

        CompoundTag nbt = new CompoundTag()
                .putInt("facing", this.getBlockFace().getIndex())
                .putBoolean("Sticky", this.sticky)
                .putBoolean("powered", isGettingPower());


        BlockEntityPistonArm piston = BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt);
        if (piston == null) {
            return false;
        }
        
        this.checkState(piston.powered);
        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        this.level.setBlock(this, new BlockAir(), true, true);

        Block block = this.getSide(getBlockFace());

        if (block instanceof BlockPistonHead && ((BlockPistonHead) block).getBlockFace() == this.getBlockFace()) {
            block.onBreak(item);
        }
        return true;
    }

    public boolean isExtended() {
        BlockFace face = getBlockFace();
        Block block = getSide(face);

        return block instanceof BlockPistonHead && ((BlockPistonHead) block).getBlockFace() == face;
    }

    @Override
    @PowerNukkitDifference(info = "Using new method for checking if powered + update all around redstone torches, " +
            "even if the piston can't move.", since = "1.4.0.0-PN")
    public int onUpdate(int type) {
        if (type != Level.BLOCK_UPDATE_NORMAL && type != Level.BLOCK_UPDATE_REDSTONE && type != Level.BLOCK_UPDATE_SCHEDULED) {
            return 0;
        } else {
            if (!this.level.getServer().isRedstoneEnabled()) {
                return 0;
            }

            // We can't use getOrCreateBlockEntity(), because the update method is called on block place,
            // before the "real" BlockEntity is set. That means, if we'd use the other method here,
            // it would create two BlockEntities.
            BlockEntityPistonArm arm = this.getBlockEntity();

            boolean powered = this.isGettingPower();
            this.updateAroundRedstoneTorches(powered);

            if (arm == null || !arm.finished)
                return 0;

            if (arm.state % 2 == 0 && arm.powered != powered && checkState(powered)) {
                arm.powered = powered;

                if (arm.chunk != null) {
                    arm.chunk.setChanged();
                }
            }

            return type;
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private void updateAroundRedstoneTorches(boolean powered) {
        for (BlockFace side : BlockFace.values()) {
            if ((getSide(side) instanceof BlockRedstoneTorch && powered)
                    || (getSide(side) instanceof BlockRedstoneTorchUnlit && !powered)) {
                BlockTorch torch = (BlockTorch) getSide(side);

                BlockTorch.TorchAttachment torchAttachment = torch.getTorchAttachment();
                Block support = torch.getSide(torchAttachment.getAttachedFace());

                if (support.getLocation().equals(this.getLocation())) {
                    torch.onUpdate(Level.BLOCK_UPDATE_REDSTONE);
                }
            }
        }
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @PowerNukkitDifference(info = "Using new method for checking if powered", since = "1.4.0.0-PN")
    private boolean checkState(Boolean isPowered) {
        if (!this.level.getServer().isRedstoneEnabled()) {
            return false;
        }

        if (isPowered == null) {
            isPowered = this.isGettingPower();
        }

        if (isPowered && !isExtended()) {
            if (!this.doMove(true)) {
                return false;
            }

            this.getLevel().addSound(this, Sound.TILE_PISTON_OUT);
            return true;
        } else if (!isPowered && isExtended()) {
            if (!this.doMove(false)) {
                return false;
            }

            this.getLevel().addSound(this, Sound.TILE_PISTON_IN);
            return true;
        }

        return false;
    }

    @PowerNukkitDifference(info = "Piston shouldn't be powered from redstone under it.", since = "1.4.0.0-PN")
    @Override
    public boolean isGettingPower() {
        BlockFace face = getBlockFace();

        for (BlockFace side : BlockFace.values()) {
            if (side == face) {
                continue;
            }

            Block b = this.getSide(side);

            if (b.getId() == Block.REDSTONE_WIRE && b.getDamage() > 0 && b.y >= this.getY()) {
                return true;
            }

            if (this.level.isSidePowered(b, side)) {
                return true;
            }
        }

        return false;
    }

    @Deprecated @DeprecationDetails(reason = "New method; keeping for plugin compatibility.", replaceWith = "#isGettingPower()", since = "1.4.0.0-PN", by = "PowerNukkit")
    public boolean isPowered() {
        return this.isGettingPower();
    }

    private boolean doMove(boolean extending) {
        BlockFace direction = getBlockFace();
        BlocksCalculator calculator = new BlocksCalculator(level, this, getBlockFace(), extending, sticky);

        boolean canMove = calculator.canMove();

        if (!canMove && extending) {
            return false;
        }

        List<BlockVector3> attached = Collections.emptyList();

        BlockPistonEvent event = new BlockPistonEvent(this, direction, calculator.getBlocksToMove(), calculator.getBlocksToDestroy(), extending);
        this.level.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        if (canMove && (this.sticky || extending)) {
            List<Block> destroyBlocks = calculator.getBlocksToDestroy();
            for (int i = destroyBlocks.size() - 1; i >= 0; --i) {
                Block block = destroyBlocks.get(i);
                this.level.useBreakOn(block, null, null, false);
            }

            List<Block> newBlocks = calculator.getBlocksToMove();

            attached = newBlocks.stream().map(Vector3::asBlockVector3).collect(Collectors.toList());

            BlockFace side = extending ? direction : direction.getOpposite();

            List<CompoundTag> tags = new ArrayList<>();
            for (Block oldBlock : newBlocks) {
                CompoundTag tag = new CompoundTag();
                BlockEntity be = this.level.getBlockEntity(oldBlock);
                if (be != null && !(be instanceof BlockEntityMovingBlock)) {
                    be.saveNBT();
                    tag = new CompoundTag(be.namedTag.getTags());

                    be.close();
                }
                tags.add(tag);
            }

            int i = 0;
            for (Block newBlock : newBlocks) {
                Vector3 oldPos = newBlock.add(0);
                newBlock.position(newBlock.add(0).getSide(side));
                
                CompoundTag nbt = new CompoundTag()
                        .putInt("pistonPosX", this.getFloorX())
                        .putInt("pistonPosY", this.getFloorY())
                        .putInt("pistonPosZ", this.getFloorZ())
                        .putCompound("movingBlock", new CompoundTag()
                                .putInt("id", newBlock.getId()) //only for nukkit purpose
                                .putInt("meta", newBlock.getDamage()) //only for nukkit purpose
                                .putShort("val", newBlock.getDamage())
                                .putString("name", BlockStateRegistry.getPersistenceName(newBlock.getId()))
                        );

                if (!tags.get(i).isEmpty()) {
                    nbt.putCompound("movingEntity", tags.get(i));
                }

                BlockEntityHolder.setBlockAndCreateEntity((BlockEntityHolder<?>) BlockState.of(BlockID.MOVING_BLOCK).getBlock(newBlock), 
                        true, true, nbt);

                if (this.level.getBlockIdAt(oldPos.getFloorX(), oldPos.getFloorY(), oldPos.getFloorZ()) != BlockID.MOVING_BLOCK) {
                    this.level.setBlock(oldPos, Block.get(BlockID.AIR));
                }
                i++;
            }
        }

        if (extending) {
            this.level.setBlock(this.getSide(direction), createHead(this.getDamage()));
        }

        BlockEntityPistonArm blockEntity = getOrCreateBlockEntity();
        blockEntity.move(extending, attached);
        return true;
    }
    
    protected BlockPistonHead createHead(int damage) {
        return (BlockPistonHead) Block.get(getPistonHeadBlockId(), damage);
    }

    public abstract int getPistonHeadBlockId();

    public static boolean canPush(Block block, BlockFace face, boolean destroyBlocks, boolean extending) {
        if (
                block.getY() >= 0 && (face != BlockFace.DOWN || block.getY() != 0) &&
                        block.getY() <= 255 && (face != BlockFace.UP || block.getY() != 255)
        ) {
            if (extending && !block.canBePushed() || !extending && !block.canBePulled()) {
                return false;
            }

            if (block.breaksWhenMoved()) {
                return destroyBlocks || block.sticksToPiston();
            }

            BlockEntity be = block.level.getBlockEntity(block);
            return be == null || be.isMovable();
        }

        return false;
    }

    public class BlocksCalculator {

        private final Vector3 pistonPos;
        private Vector3 armPos;
        private final Block blockToMove;
        private final BlockFace moveDirection;
        private final boolean extending;
        private final boolean sticky;

        private final List<Block> toMove = new ArrayList<>();
        private final List<Block> toDestroy = new ArrayList<>();

        /**
         * @param level Unused, needed for compatibility with Cloudburst Nukkit plugins
         */
        public BlocksCalculator(Level level, Block block, BlockFace facing, boolean extending) {
            this(level, block, facing, extending, false);
        }

        /**
         * @param level Unused, needed for compatibility with Cloudburst Nukkit plugins
         */
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        public BlocksCalculator(Level level, Block pos, BlockFace face, boolean extending, boolean sticky) {
            this.pistonPos = pos.getLocation();
            this.extending = extending;
            this.sticky = sticky;

            if (!extending) {
                this.armPos = pistonPos.getSide(face);
            }

            if (extending) {
                this.moveDirection = face;
                this.blockToMove = pos.getSide(face);
            } else {
                this.moveDirection = face.getOpposite();
                if (sticky) {
                    this.blockToMove = pos.getSide(face, 2);
                } else {
                    this.blockToMove = null;
                }
            }
        }

        public boolean canMove() {
            if (!sticky && !extending) {
                return true;
            }

            this.toMove.clear();
            this.toDestroy.clear();
            Block block = this.blockToMove;

            if (!canPush(block, this.moveDirection, true, extending)) {
                return false;
            }

            if (block.breaksWhenMoved()) {
                if (extending || block.sticksToPiston()) {
                    this.toDestroy.add(this.blockToMove);
                }

                return true;
            }

            if (!this.addBlockLine(this.blockToMove, this.blockToMove.getSide(this.moveDirection.getOpposite()), true)) {
                return false;
            }

            for (int i = 0; i < this.toMove.size(); ++i) {
                Block b = this.toMove.get(i);

                int blockId = b.getId();
                if ((blockId == SLIME_BLOCK || blockId == HONEY_BLOCK) && !this.addBranchingBlocks(b)) {
                    return false;
                }
            }

            return true;
        }

        @PowerNukkitDifference(info = "Fix honeyblock on piston facing direction" +
                "+ fix block pushing limit for slime/honey blocks" +
                "+ fix that honey/slime blocks could be retracted when the piston retracts in facing direction",
                since = "1.4.0.0-PN")
        private boolean addBlockLine(Block origin, Block from, boolean mainBlockLine) {
            Block block = origin.clone();

            if (block.getId() == AIR) {
                return true;
            }

            if (!mainBlockLine && (block.getId() == SLIME_BLOCK && from.getId() == HONEY_BLOCK
                    || block.getId() == HONEY_BLOCK && from.getId() == SLIME_BLOCK)) {
                return true;
            }

            if (!canPush(origin, this.moveDirection, false, extending)) {
                return true;
            }

            if (origin.equals(this.pistonPos)) {
                return true;
            }

            if (this.toMove.contains(origin)) {
                return true;
            }

            if (this.toMove.size() >= 12) {
                return false;
            }

            this.toMove.add(block);

            int count = 1;
            List<Block> sticked = new ArrayList<>();

            while (block.getId() == SLIME_BLOCK || block.getId() == HONEY_BLOCK) {
                Block oldBlock = block.clone();
                block = origin.getSide(this.moveDirection.getOpposite(), count);

                if (!extending && (block.getId() == SLIME_BLOCK && oldBlock.getId() == HONEY_BLOCK
                        || block.getId() == HONEY_BLOCK && oldBlock.getId() == SLIME_BLOCK)) {
                    break;
                }

                if (block.getId() == AIR || !canPush(block, this.moveDirection, false, extending) || block.equals(this.pistonPos)) {
                    break;
                }

                if (block.breaksWhenMoved() && block.sticksToPiston()) {
                    this.toDestroy.add(block);
                    break;
                }

                if (count + this.toMove.size() > 12) {
                    return false;
                }
                count++;

                sticked.add(block);
            }

            int stickedCount = sticked.size();

            if (stickedCount > 0) {
                this.toMove.addAll(Lists.reverse(sticked));
            }

            int step = 1;

            while (true) {
                Block nextBlock = origin.getSide(this.moveDirection, step);
                int index = this.toMove.indexOf(nextBlock);

                if (index > -1) {
                    this.reorderListAtCollision(stickedCount, index);

                    for (int i = 0; i <= index + stickedCount; ++i) {
                        Block b = this.toMove.get(i);

                        if ((b.getId() == SLIME_BLOCK || b.getId() == HONEY_BLOCK) && !this.addBranchingBlocks(b)) {
                            return false;
                        }
                    }

                    return true;
                }

                if (nextBlock.getId() == AIR || nextBlock.equals(armPos)) {
                    return true;
                }

                if (!canPush(nextBlock, this.moveDirection, true, extending) || nextBlock.equals(this.pistonPos)) {
                    return false;
                }

                if (nextBlock.breaksWhenMoved()) {
                    this.toDestroy.add(nextBlock);
                    return true;
                }

                if (this.toMove.size() >= 12) {
                    return false;
                }

                this.toMove.add(nextBlock);
                ++stickedCount;
                ++step;
            }
        }

        private void reorderListAtCollision(int count, int index) {
            List<Block> list = new ArrayList<>(this.toMove.subList(0, index));
            List<Block> list1 = new ArrayList<>(this.toMove.subList(this.toMove.size() - count, this.toMove.size()));
            List<Block> list2 = new ArrayList<>(this.toMove.subList(index, this.toMove.size() - count));
            this.toMove.clear();
            this.toMove.addAll(list);
            this.toMove.addAll(list1);
            this.toMove.addAll(list2);
        }

        private boolean addBranchingBlocks(Block block) {
            for (BlockFace face : BlockFace.values()) {
                if (face.getAxis() != this.moveDirection.getAxis() && !this.addBlockLine(block.getSide(face), block, false)) {
                    return false;
                }
            }

            return true;
        }

        public List<Block> getBlocksToMove() {
            return this.toMove;
        }

        public List<Block> getBlocksToDestroy() {
            return this.toDestroy;
        }
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public BlockFace getBlockFace() {
        BlockFace face = BlockFace.fromIndex(this.getDamage());

        return face.getHorizontalIndex() >= 0 ? face.getOpposite() : face;
    }
}
