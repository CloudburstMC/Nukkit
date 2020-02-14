package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityTypes;
import cn.nukkit.blockentity.Piston;
import cn.nukkit.event.block.BlockPistonChangeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockEntityRegistry;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.data.SoundEvent;

import java.util.ArrayList;
import java.util.List;

import static cn.nukkit.block.BlockIds.*;
import static com.nukkitx.math.vector.Vector3i.UP;

/**
 * @author CreeperFace
 */
public abstract class BlockPistonBase extends BlockSolid implements Faceable {

    public boolean sticky;

    public BlockPistonBase(Identifier id) {
        super(id);
    }

    @Override
    public float getResistance() {
        return 2.5f;
    }

    @Override
    public float getHardness() {
        return 0.5f;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (Math.abs(player.getX() - this.getX()) < 2 && Math.abs(player.getZ() - this.getZ()) < 2) {
            float y = player.getY() + player.getEyeHeight();

            if (y - this.getY() > 2) {
                this.setDamage(BlockFace.UP.getIndex());
            } else if (this.getY() - y > 0) {
                this.setDamage(BlockFace.DOWN.getIndex());
            } else {
                this.setDamage(player.getHorizontalFacing().getIndex());
            }
        } else {
            this.setDamage(player.getHorizontalFacing().getIndex());
        }
        this.level.setBlock(block.getPosition(), this, true, false);

        Piston piston = BlockEntityRegistry.get().newEntity(BlockEntityTypes.PISTON, this.getChunk(), this.getPosition());
        piston.setSticky(this.sticky);

        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        this.level.setBlock(this.getPosition(), Block.get(AIR), true, true);

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
            BlockEntity blockEntity = this.level.getBlockEntity(this.getPosition());
            if (blockEntity instanceof Piston) {
                Piston piston = (Piston) blockEntity;
                boolean powered = this.isPowered();
                if (piston.isPowered() != powered) {
                    this.level.getServer().getPluginManager().callEvent(new BlockPistonChangeEvent(this, powered ? 0 : 15, powered ? 15 : 0));
                    piston.setPowered(!piston.isPowered());
                }
            }

            return type;
        }
    }

    public static boolean canPush(Block block, BlockFace face, boolean destroyBlocks) {
        if (block.canBePushed() && block.getY() >= 0 && (face != BlockFace.DOWN || block.getY() != 0) &&
                block.getY() <= 255 && (face != BlockFace.UP || block.getY() != 255)) {
            if (!(block instanceof BlockPistonBase)) {

                if (block instanceof FloodableBlock) {
                    return destroyBlocks;
                }
            } else return !((BlockPistonBase) block).isExtended();
            return true;
        }
        return false;

    }

    public BlockFace getFacing() {
        return BlockFace.fromIndex(this.getMeta()).getOpposite();
    }

    private boolean isPowered() {
        BlockFace face = getFacing();

        for (BlockFace side : BlockFace.values()) {
            if (side != face && this.level.isSidePowered(side.getOffset(this.getPosition()), side)) {
                return true;
            }
        }

        if (this.level.isSidePowered(this.getPosition(), BlockFace.DOWN)) {
            return true;
        } else {
            Vector3i pos = this.getPosition().add(UP);

            for (BlockFace side : BlockFace.values()) {
                if (side != BlockFace.DOWN && this.level.isSidePowered(side.getOffset(pos), side)) {
                    return true;
                }
            }

            return false;
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

                this.getLevel().addLevelSoundEvent(this.getPosition(), SoundEvent.PISTON_OUT);
            } else {
            }
        } else if (!isPowered && isExtended()) {
            //this.level.setBlock() TODO: set piston extension?

            if (this.sticky) {
                Vector3i pos = this.getPosition().add(facing.getXOffset() * 2, facing.getYOffset() * 2, facing.getZOffset() * 2);
                Block block = this.level.getBlock(pos);

                if (block.getId() == AIR) {
                    this.level.setBlock(facing.getOffset(this.getPosition()), Block.get(AIR), true, true);
                }
                if (canPush(block, facing.getOpposite(), false) && (!(block instanceof FloodableBlock) || block.getId() == PISTON || block.getId() == STICKY_PISTON)) {
                    this.doMove(false);
                }
            } else {
                this.level.setBlock(facing.getOffset(this.getPosition()), Block.get(AIR), true, false);
            }

            this.getLevel().addLevelSoundEvent(this.getPosition(), SoundEvent.PISTON_IN);
        }
    }

    private boolean doMove(boolean extending) {
        Vector3i pos = this.getPosition();
        BlockFace direction = getFacing();

        if (!extending) {
            this.level.setBlock(direction.getOffset(pos), Block.get(AIR), true, false);
        }

        BlocksCalculator calculator = new BlocksCalculator(this.level, this, direction, extending);

        if (!calculator.canMove()) {
            return false;
        } else {
            List<Block> blocks = calculator.getBlocksToMove();

            List<Block> newBlocks = new ArrayList<>(blocks);

            List<Block> destroyBlocks = calculator.getBlocksToDestroy();
            BlockFace side = extending ? direction : direction.getOpposite();

            for (int i = destroyBlocks.size() - 1; i >= 0; --i) {
                Block block = destroyBlocks.get(i);
                this.level.useBreakOn(block.getPosition());
            }

            for (int i = blocks.size() - 1; i >= 0; --i) {
                Block block = blocks.get(i);
                this.level.setBlock(block.getPosition(), Block.get(AIR));
                Vector3i newPos = side.getOffset(block.getPosition());

                //TODO: change this to block entity
                this.level.setBlock(newPos, newBlocks.get(i));
            }

            Vector3i pistonHead = direction.getOffset(pos);

            if (extending) {
                //extension block entity
                this.level.setBlock(pistonHead, Block.get(PISTON_ARM_COLLISION, this.getMeta()));
            }

            return true;
        }
    }

    @Override
    public Item toItem() {
        return Item.get(id, 0);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getMeta() & 0x07);
    }

    public class BlocksCalculator {

        private final Level level;
        private final Vector3i pistonPos;
        private final Block blockToMove;
        private final BlockFace moveDirection;

        private final List<Block> toMove = new ArrayList<>();
        private final List<Block> toDestroy = new ArrayList<>();

        public BlocksCalculator(Level level, Block pos, BlockFace facing, boolean extending) {
            this.level = level;
            this.pistonPos = pos.getPosition();

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
                if (block instanceof FloodableBlock) {
                    this.toDestroy.add(this.blockToMove);
                    return true;
                } else {
                    return false;
                }
            } else if (!this.addBlockLine(this.blockToMove)) {
                return false;
            } else {
                for (Block b : this.toMove) {
                    if (b.getId() == SLIME && !this.addBranchingBlocks(b)) {
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
                    while (block.getId() == SLIME) {
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

                                if (b.getId() == SLIME && !this.addBranchingBlocks(b)) {
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

                        if (nextBlock instanceof FloodableBlock) {
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
