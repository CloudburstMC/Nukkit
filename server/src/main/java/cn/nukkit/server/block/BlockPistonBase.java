package cn.nukkit.server.block;

import cn.nukkit.server.Player;
import cn.nukkit.server.blockentity.BlockEntity;
import cn.nukkit.server.blockentity.BlockEntityPistonArm;
import cn.nukkit.server.event.block.BlockPistonChangeEvent;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.level.Level;
import cn.nukkit.server.level.sound.PistonInSound;
import cn.nukkit.server.level.sound.PistonOutSound;
import cn.nukkit.server.math.BlockFace;
import cn.nukkit.server.math.Vector3;
import cn.nukkit.server.nbt.tag.CompoundTag;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CreeperFace
 */
public abstract class BlockPistonBase extends BlockSolid {

    public boolean sticky;

    public BlockPistonBase() {
        this(0);
    }

    public BlockPistonBase(int meta) {
        super(meta);
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (Math.abs(player.x - this.x) < 2 && Math.abs(player.z - this.z) < 2) {
            double y = player.y + player.getEyeHeight();

            if (y - this.y > 2) {
                this.meta = BlockFace.UP.getIndex();
            } else if (this.y - y > 0) {
                this.meta = BlockFace.DOWN.getIndex();
            } else {
                this.meta = player.getHorizontalFacing().getIndex();
            }
        } else {
            this.meta = player.getHorizontalFacing().getIndex();
        }
        this.level.setBlock(block, this, true, false);

        CompoundTag nbt = new CompoundTag("")
                .putString("id", BlockEntity.PISTON_ARM)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putBoolean("Sticky", this.sticky);

        BlockEntityPistonArm be = new BlockEntityPistonArm(this.level.getChunk((int) this.x >> 4, (int) this.z >> 4), nbt);

        //this.checkState();
        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        this.level.setBlock(this, new BlockAir(), true, true);

        Block block = this.getSide(getFacing());

        if (block instanceof BlockPistonHead && ((BlockPistonHead) block).getFacing() == this.getFacing()) {
            block.onBreak(item);
        }
        return true;
    }

    public boolean isExtended() {
        BlockFace face = getFacing();
        Block block = getSide(face);
        return block instanceof BlockPistonHead && ((BlockPistonHead) block).getFacing() == face;
    }

    @Override
    public int onUpdate(int type) {
        if (type != 6 && type != 1) {
            return 0;
        } else {
            BlockEntity blockEntity = this.level.getBlockEntity(this);
            if (blockEntity instanceof BlockEntityPistonArm) {
                BlockEntityPistonArm arm = (BlockEntityPistonArm) blockEntity;
                boolean powered = this.isPowered();
                if (arm.powered != powered) {
                    this.level.getServer().getPluginManager().callEvent(new BlockPistonChangeEvent(this, powered ? 0 : 15, powered ? 15 : 0));
                    arm.powered = !arm.powered;
                    if (arm.chunk != null) {
                        arm.chunk.setChanged();
                    }
                }
            }

            return type;
        }
    }

    private void checkState() {
        BlockFace facing = getFacing();
        boolean isPowered = this.isPowered();

        if (isPowered && !isExtended()) {
            if ((new BlocksCalculator(this.level, this, facing, true)).canMove()) {
                if (!this.doMove(true)) {
                    return;
                }

                this.level.addSound(new PistonOutSound(this));
            } else {
            }
        } else if (!isPowered && isExtended()) {
            //this.level.setBlock() TODO: set piston extension?

            if (this.sticky) {
                Vector3 pos = this.add(facing.getXOffset() * 2, facing.getYOffset() * 2, facing.getZOffset() * 2);
                Block block = this.level.getBlock(pos);

                if (block.getId() == AIR) {
                    this.level.setBlock(this.getLocation().getSide(facing), new BlockAir(), true, true);
                }
                if (canPush(block, facing.getOpposite(), false) && (!(block instanceof BlockFlowable) || block.getId() == PISTON || block.getId() == STICKY_PISTON)) {
                    this.doMove(false);
                }
            } else {
                this.level.setBlock(getLocation().getSide(facing), new BlockAir(), true, false);
            }

            this.level.addSound(new PistonInSound(this));
        }
    }

    public BlockFace getFacing() {
        return BlockFace.fromIndex(this.meta).getOpposite();
    }

    private boolean isPowered() {
        BlockFace face = getFacing();

        for (BlockFace side : BlockFace.values()) {
            if (side != face && this.level.isSidePowered(this.getLocation().getSide(side), side)) {
                return true;
            }
        }

        if (this.level.isSidePowered(this, BlockFace.DOWN)) {
            return true;
        } else {
            Vector3 pos = this.getLocation().up();

            for (BlockFace side : BlockFace.values()) {
                if (side != BlockFace.DOWN && this.level.isSidePowered(pos.getSide(side), side)) {
                    return true;
                }
            }

            return false;
        }
    }

    private boolean doMove(boolean extending) {
        Vector3 pos = this.getLocation();
        BlockFace direction = getFacing();

        if (!extending) {
            this.level.setBlock(pos.getSide(direction), new BlockAir(), true, false);
        }

        BlocksCalculator calculator = new BlocksCalculator(this.level, this, direction, extending);

        if (!calculator.canMove()) {
            return false;
        } else {
            List<Block> blocks = calculator.getBlocksToMove();
            List<Block> newBlocks = new ArrayList<>();

            for (int i = 0; i < blocks.size(); ++i) {
                Block block = blocks.get(i);
                newBlocks.add(block);
            }

            List<Block> destroyBlocks = calculator.getBlocksToDestroy();
            BlockFace side = extending ? direction : direction.getOpposite();

            for (int i = destroyBlocks.size() - 1; i >= 0; --i) {
                Block block = destroyBlocks.get(i);
                this.level.useBreakOn(block);
            }

            for (int i = blocks.size() - 1; i >= 0; --i) {
                Block block = blocks.get(i);
                this.level.setBlock(block, new BlockAir());
                Vector3 newPos = block.getLocation().getSide(side);

                //TODO: change this to block entity
                this.level.setBlock(newPos, newBlocks.get(i));
            }

            Vector3 pistonHead = pos.getSide(direction);

            if (extending) {
                //extension block entity

                this.level.setBlock(pistonHead, new BlockPistonHead(this.meta));
            }

            return true;
        }
    }

    public static boolean canPush(Block block, BlockFace face, boolean destroyBlocks) {
        if (!block.canBePushed()) {
            return false;
        } else if (block.getY() >= 0 && (face != BlockFace.DOWN || block.getY() != 0)) {
            if (block.getY() <= 255 && (face != BlockFace.UP || block.getY() != 255)) {
                if (!(block instanceof BlockPistonBase)) {

                    if (block instanceof BlockFlowable) {
                        return destroyBlocks;
                    }
                } else if (((BlockPistonBase) block).isExtended()) {
                    return false;
                }

                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public class BlocksCalculator {

        private final Level level;
        private final Vector3 pistonPos;
        private final Block blockToMove;
        private final BlockFace moveDirection;

        private final List<Block> toMove = new ArrayList<>();
        private final List<Block> toDestroy = new ArrayList<>();

        public BlocksCalculator(Level level, Block pos, BlockFace facing, boolean extending) {
            this.level = level;
            this.pistonPos = pos.getLocation();

            if (extending) {
                this.moveDirection = facing;
                this.blockToMove = pos.getSide(facing);
            } else {
                this.moveDirection = facing.getOpposite();
                this.blockToMove = pos.getSide(facing, 2);
            }
        }

        public boolean canMove() {
            this.toMove.clear();
            this.toDestroy.clear();
            Block block = this.blockToMove;

            if (!canPush(block, this.moveDirection, false)) {
                if (block instanceof BlockFlowable) {
                    this.toDestroy.add(this.blockToMove);
                    return true;
                } else {
                    return false;
                }
            } else if (!this.addBlockLine(this.blockToMove)) {
                return false;
            } else {
                for (int i = 0; i < this.toMove.size(); ++i) {
                    Block b = this.toMove.get(i);

                    if (b.getId() == SLIME_BLOCK && !this.addBranchingBlocks(b)) {
                        return false;
                    }
                }

                return true;
            }
        }

        private boolean addBlockLine(Block origin) {
            Block block = origin.clone();

            if (block.getId() == AIR) {
                return true;
            } else if (!canPush(origin, this.moveDirection, false)) {
                return true;
            } else if (origin.equals(this.pistonPos)) {
                return true;
            } else if (this.toMove.contains(origin)) {
                return true;
            } else {
                int count = 1;

                if (count + this.toMove.size() > 12) {
                    return false;
                } else {
                    while (block.getId() == SLIME_BLOCK) {
                        block = origin.getSide(this.moveDirection.getOpposite(), count);

                        if (block.getId() == AIR || !canPush(block, this.moveDirection, false) || block.equals(this.pistonPos)) {
                            break;
                        }

                        ++count;

                        if (count + this.toMove.size() > 12) {
                            return false;
                        }
                    }

                    int blockCount = 0;

                    for (int step = count - 1; step >= 0; --step) {
                        this.toMove.add(block.getSide(this.moveDirection.getOpposite(), step));
                        ++blockCount;
                    }

                    int steps = 1;

                    while (true) {
                        Block nextBlock = block.getSide(this.moveDirection, steps);
                        int index = this.toMove.indexOf(nextBlock);

                        if (index > -1) {
                            this.reorderListAtCollision(blockCount, index);

                            for (int l = 0; l <= index + blockCount; ++l) {
                                Block b = this.toMove.get(l);

                                if (b.getId() == SLIME_BLOCK && !this.addBranchingBlocks(b)) {
                                    return false;
                                }
                            }

                            return true;
                        }

                        if (nextBlock.getId() == AIR) {
                            return true;
                        }

                        if (!canPush(nextBlock, this.moveDirection, true) || nextBlock.equals(this.pistonPos)) {
                            return false;
                        }

                        if (nextBlock instanceof BlockFlowable) {
                            this.toDestroy.add(nextBlock);
                            return true;
                        }

                        if (this.toMove.size() >= 12) {
                            return false;
                        }

                        this.toMove.add(nextBlock);
                        ++blockCount;
                        ++steps;
                    }
                }
            }
        }

        private void reorderListAtCollision(int count, int index) {
            List<Block> list = new ArrayList<>();
            List<Block> list1 = new ArrayList<>();
            List<Block> list2 = new ArrayList<>();
            list.addAll(this.toMove.subList(0, index));
            list1.addAll(this.toMove.subList(this.toMove.size() - count, this.toMove.size()));
            list2.addAll(this.toMove.subList(index, this.toMove.size() - count));
            this.toMove.clear();
            this.toMove.addAll(list);
            this.toMove.addAll(list1);
            this.toMove.addAll(list2);
        }

        private boolean addBranchingBlocks(Block block) {
            for (BlockFace face : BlockFace.values()) {
                if (face.getAxis() != this.moveDirection.getAxis() && !this.addBlockLine(block.getSide(face))) {
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
}