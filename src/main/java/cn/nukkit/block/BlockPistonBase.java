package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityPistonArm;
import cn.nukkit.event.block.BlockPistonChangeEvent;
import cn.nukkit.event.block.BlockPistonEvent;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.Faceable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CreeperFace
 */
public abstract class BlockPistonBase extends BlockSolidMeta implements Faceable {

    public boolean sticky;

    public BlockPistonBase() {
        this(0);
    }

    public BlockPistonBase(int meta) {
        super(meta);
    }

    public abstract int getPistonHeadBlockId();

    @Override
    public double getResistance() {
        return 1.5;
    }

    @Override
    public double getHardness() {
        return 0.5; // 1.5
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (Math.abs(player.getFloorX() - this.x) < 2 && Math.abs(player.getFloorZ() - this.z) < 2) {
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
        this.getLevel().setBlock(this, this, true, false);

        BlockEntityPistonArm be = (BlockEntityPistonArm) BlockEntity.createBlockEntity(BlockEntity.PISTON_ARM, this.getChunk(), new CompoundTag("")
                .putString("id", BlockEntity.PISTON_ARM)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putBoolean("Sticky", this.sticky));
        be.spawnToAll();

        this.checkState();
        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        this.level.setBlock(this, Block.get(BlockID.AIR), true, true);

        Block block = this.getSide(getFacing());
        if (block instanceof BlockPistonHead && ((BlockPistonHead) block).getBlockFace() == this.getFacing()) {
            block.onBreak(item);
        }
        return true;
    }

    public boolean isExtended() {
        BlockFace face = getFacing();
        Block block = getSide(face);
        return block instanceof BlockPistonHead && ((BlockPistonHead) block).getBlockFace() == face;
    }

    @Override
    public int onUpdate(int type) {
        if (type != 6 && type != 1) {
            return 0;
        } else {
            if (type == Level.BLOCK_UPDATE_REDSTONE) {
                RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
                getLevel().getServer().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return 0;
                }
            }
            this.checkState();
            return type;
        }
    }

    private void checkState() {
        BlockFace facing = getFacing();
        boolean isPowered = this.isPowered();
        boolean extended = isExtended();

        if (isPowered && !extended) {
            BlocksCalculator calculator = new BlocksCalculator(this, facing, true);
            if (calculator.canMove()) {
                if (!this.doMove(true, calculator)) {
                    return;
                }
                this.updateBlockEntity(true);
                this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_PISTON_OUT);
            }
            return;
        }

        if (!isPowered && extended) {
            if (this.sticky) {
                Vector3 pos = this.add(facing.getXOffset() << 1, facing.getYOffset() << 1, facing.getZOffset() << 1);
                Block block = this.level.getBlock(pos);

                if (block.getId() == AIR) {
                    this.level.setBlock(this.getSideVec(facing), Block.get(BlockID.AIR), true, true);
                }
                if (canPush(block, facing.getOpposite(), false) && (!(block instanceof BlockFlowable || block.breakWhenPushed()) || block.getId() == PISTON || block.getId() == STICKY_PISTON)) {
                    if (this.doMove(false, null)) {
                        this.updateBlockEntity(false);
                    }
                }
            } else {
                this.updateBlockEntity(false);
                this.level.setBlock(getSideVec(facing), Block.get(BlockID.AIR), true, true);
            }

            this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_PISTON_IN);
        }
    }

    public BlockFace getFacing() {
        BlockFace face = BlockFace.fromIndex(this.getDamage()).getOpposite();
        if (face == BlockFace.UP) return BlockFace.DOWN;
        if (face == BlockFace.DOWN) return BlockFace.UP;
        return face;
    }

    private boolean isPowered() {
        BlockFace face = getFacing();

        // Revert to opposite
        if (face == BlockFace.UP) face = BlockFace.DOWN;
        if (face == BlockFace.DOWN) face = BlockFace.UP;

        for (BlockFace side : BlockFace.values()) {
            if (side != face && this.level.isSidePowered(this.getSideVec(side), side)) {
                return true;
            }
        }

        if (this.level.isSidePowered(this, BlockFace.DOWN)) {
            return true;
        } else {
            Vector3 pos = this.getSideVec(BlockFace.UP);

            for (BlockFace side : BlockFace.values()) {
                if (side != BlockFace.DOWN && this.level.isSidePowered(pos.getSideVec(side), side)) {
                    return true;
                }
            }

            return false;
        }
    }

    private void updateBlockEntity(boolean extending) {
        BlockEntity blockEntity = this.level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntityPistonArm) {
            BlockEntityPistonArm arm = (BlockEntityPistonArm) blockEntity;
            if (arm.isExtended() != extending) {
                this.level.getServer().getPluginManager().callEvent(new BlockPistonChangeEvent(this, extending ? 0 : 15, extending ? 15 : 0));
                arm.setExtended(extending);
                arm.broadcastMove();
                if (arm.chunk != null) {
                    arm.chunk.setChanged();
                }
            }
        }
    }

    private boolean doMove(boolean extending, BlocksCalculator calculator) {
        BlockFace direction = getFacing();

        if (!extending) {
            this.level.setBlock(this.getSideVec(direction), Block.get(BlockID.AIR), true, false);
        }
        if (calculator == null) {
            calculator = new BlocksCalculator(this, direction, extending);
        }

        if (calculator.canMove()) {
            BlockPistonEvent event = new BlockPistonEvent(this, direction, calculator.getBlocksToMove(), calculator.getBlocksToDestroy(), extending);
            this.level.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return false;
            }

            List<Block> blocks = calculator.getBlocksToMove();
            if (!extending && blocks.isEmpty()) {
                this.level.setBlock(this.getSideVec(direction), Block.get(BlockID.AIR), false, true);
                return true;
            }

            Block pistonHead = null;
            if (extending) {
                pistonHead = this.getSide(direction);
                if (!pistonHead.canBePushed()) {
                    return false; // TODO: figure out why this happens
                }
            }

            List<Block> newBlocks = new ArrayList<>(blocks);
            List<Block> destroyBlocks = calculator.getBlocksToDestroy();
            BlockFace side = extending ? direction : direction.getOpposite();

            for (int i = destroyBlocks.size() - 1; i >= 0; --i) {
                Block block = destroyBlocks.get(i);
                this.level.useBreakOn(block);
            }

            for (int i = blocks.size() - 1; i >= 0; --i) {
                Block block = blocks.get(i);
                this.level.setBlock(block, Block.get(BlockID.AIR), true, false);
                Vector3 newPos = block.getSideVec(side);
                Block newBlock = newBlocks.get(i);

                // TODO: Change this to block entity
                this.level.setBlock(newPos, newBlock, true, false);

                if (newBlock instanceof BlockPistonBase) {
                    BlockEntityPistonArm be = (BlockEntityPistonArm) BlockEntity.createBlockEntity(BlockEntity.PISTON_ARM, newBlock.getChunk(), new CompoundTag("")
                            .putString("id", BlockEntity.PISTON_ARM)
                            .putInt("x", (int) newBlock.x)
                            .putInt("y", (int) newBlock.y)
                            .putInt("z", (int) newBlock.z)
                            .putBoolean("Sticky", newBlock.getId() == STICKY_PISTON));
                    be.spawnToAll();
                }
            }

            if (pistonHead != null) {
                this.level.setBlock(pistonHead, Block.get(this.getPistonHeadBlockId(), this.getDamage()), true, false);
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean canPush(Block block, BlockFace face, boolean destroyBlocks) {
        if (block.canBePushed() && block.getY() >= block.getLevel().getMinBlockY() && (face != BlockFace.DOWN || block.getY() != block.getLevel().getMinBlockY()) && block.getY() <= block.getLevel().getMaxBlockY() && (face != BlockFace.UP || block.getY() != block.getLevel().getMaxBlockY())) {
            if (!(block instanceof BlockPistonBase)) {
                if ((block instanceof BlockFlowable && !(block instanceof BlockEndPortal || block instanceof BlockNetherPortal)) || block.breakWhenPushed()) {
                    return destroyBlocks;
                }
            } else return !((BlockPistonBase) block).isExtended();
            return true;
        }
        return false;
    }

    public static class BlocksCalculator {
        private final Vector3 pistonPos;
        private final Block blockToMove;
        private final BlockFace moveDirection;

        private final List<Block> toMove = new ArrayList<>();
        private final List<Block> toDestroy = new ArrayList<>();

        protected Boolean canMove;

        public BlocksCalculator(Block pos, BlockFace facing, boolean extending) {
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
            return this.canMove == null ? this.canMove = this.eval() : this.canMove;
        }

        private boolean eval() {
            this.toMove.clear();
            this.toDestroy.clear();

            if (!canPush(this.blockToMove, this.moveDirection, false)) {
                if ((this.blockToMove instanceof BlockFlowable && !(this.blockToMove instanceof BlockEndPortal || this.blockToMove instanceof BlockNetherPortal)) || this.blockToMove.breakWhenPushed()) {
                    boolean exists = false;
                    for (Block b : this.toDestroy) {
                        if (b.x == this.blockToMove.x && b.y == this.blockToMove.y && b.z == this.blockToMove.z) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        this.toDestroy.add(this.blockToMove);
                    }
                    return true;
                } else {
                    return false;
                }
            } else if (!this.addBlockLine(this.blockToMove)) {
                return false;
            } else {
                /*if (false) { //todo?
                    for (Block b : this.toMove) {
                        if (b.getId() == SLIME_BLOCK && !this.addBranchingBlocks(b)) {
                            return false;
                        }
                    }
                }*/

                return true;
            }
        }

        private boolean addBlockLine(Block origin) {
            Block block = origin/*.clone()*/;

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
                    /*while (false && block.getId() == SLIME_BLOCK) {
                        block = origin.getSide(this.moveDirection.getOpposite(), count);

                        if (block.getId() == AIR || !canPush(block, this.moveDirection, false) || block.equals(this.pistonPos)) {
                            break;
                        }

                        ++count;

                        if (count + this.toMove.size() > 12) {
                            return false;
                        }
                    }*/

                    int blockCount = 0;

                    for (int step = count - 1; step >= 0; --step) {
                        Block aBlock = block.getSide(this.moveDirection.getOpposite(), step);
                        if (aBlock.breakWhenPushed()) {
                            return true; // shouldn't be possible?
                        }
                        this.toMove.add(aBlock);
                        ++blockCount;
                    }

                    int steps = 1;

                    while (true) {
                        Block nextBlock = block.getSide(this.moveDirection, steps);
                        int index = this.toMove.indexOf(nextBlock);

                        if (index > -1) {
                            this.reorderListAtCollision(blockCount, index);

                            /*for (int l = 0; l <= index + blockCount; ++l) {
                                Block b = this.toMove.get(l);

                                if (b.getId() == SLIME_BLOCK && !this.addBranchingBlocks(b)) {
                                    return false;
                                }
                            }*/

                            return true;
                        }

                        if (nextBlock.getId() == AIR) {
                            return true;
                        }

                        if (!canPush(nextBlock, this.moveDirection, true) || nextBlock.equals(this.pistonPos)) {
                            return false;
                        }

                        if ((nextBlock instanceof BlockFlowable && !(nextBlock instanceof BlockEndPortal || nextBlock instanceof BlockNetherPortal)) || nextBlock.breakWhenPushed()) {
                            boolean exists = false;
                            for (Block b : this.toDestroy) {
                                if (b.x == nextBlock.x && b.y == nextBlock.y && b.z == nextBlock.z) {
                                    exists = true;
                                    break;
                                }
                            }
                            if (!exists) {
                                this.toDestroy.add(nextBlock);
                            }
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
            List<Block> list = new ArrayList<>(this.toMove.subList(0, index));
            List<Block> list1 = new ArrayList<>(this.toMove.subList(this.toMove.size() - count, this.toMove.size()));
            List<Block> list2 = new ArrayList<>(this.toMove.subList(index, this.toMove.size() - count));
            this.toMove.clear();
            this.toMove.addAll(list);
            this.toMove.addAll(list1);
            this.toMove.addAll(list2);
        }

        /*private boolean addBranchingBlocks(Block block) {
            for (BlockFace face : BlockFace.values()) {
                if (face.getAxis() != this.moveDirection.getAxis() && !this.addBlockLine(block.getSide(face))) {
                    return false;
                }
            }

            return true;
        }*/

        public List<Block> getBlocksToMove() {
            return this.toMove;
        }

        public List<Block> getBlocksToDestroy() {
            return this.toDestroy;
        }
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x07);
    }
}
