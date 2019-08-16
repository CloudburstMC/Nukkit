package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityMovingBlock;
import cn.nukkit.blockentity.BlockEntityPistonArm;
import cn.nukkit.event.block.BlockPistonChangeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.Faceable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author CreeperFace
 */
public abstract class BlockPistonBase extends BlockSolidMeta implements Faceable {

    public boolean sticky = false;

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
                this.setDamage(BlockFace.UP.getIndex());
            } else if (this.y - y > 0) {
                this.setDamage(BlockFace.DOWN.getIndex());
            } else {
                this.setDamage(player.getHorizontalFacing().getIndex());
            }
        } else {
            this.setDamage(player.getHorizontalFacing().getIndex());
        }
        this.level.setBlock(block, this, true, true);

        CompoundTag nbt = new CompoundTag("")
                .putString("id", BlockEntity.PISTON_ARM)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putInt("facing", this.getBlockFace().getIndex())
                .putBoolean("Sticky", this.sticky);

        new BlockEntityPistonArm(this.level.getChunk(getChunkX(), getChunkZ()), nbt);

        this.checkState(null);
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
    public int onUpdate(int type) {
        if (type != Level.BLOCK_UPDATE_NORMAL && type != Level.BLOCK_UPDATE_REDSTONE && type != Level.BLOCK_UPDATE_SCHEDULED) {
            return 0;
        } else {
            BlockEntity blockEntity = this.level.getBlockEntity(this);
            if (blockEntity instanceof BlockEntityPistonArm) {
                BlockEntityPistonArm arm = (BlockEntityPistonArm) blockEntity;
                boolean powered = this.isPowered();

                if (arm.powered != powered) {
                    this.level.getServer().getPluginManager().callEvent(new BlockPistonChangeEvent(this, powered ? 0 : 15, powered ? 15 : 0));

                    if (checkState(powered)) {
                        arm.powered = powered;

                        if (arm.chunk != null) {
                            arm.chunk.setChanged();
                        }
                    }
                }
            }

            return type;
        }
    }

    private boolean checkState(Boolean isPowered) {
        BlockFace facing = getBlockFace();
        if (isPowered == null) {
            isPowered = this.isPowered();
        }

        if (isPowered && !isExtended()) {
            if ((new BlocksCalculator(true)).canMove()) {
                if (!this.doMove(true)) {
                    return false;
                }

                this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_PISTON_OUT);
                return true;
            }
        } else if (!isPowered && isExtended()) {
            if (this.sticky) {
                Vector3 pos = this.add(0).getSide(facing, 2);
                Block block = this.level.getBlock(pos);

                if (canPush(block, facing.getOpposite(), false) && (!(block instanceof BlockFlowable) || block.getId() == PISTON || block.getId() == STICKY_PISTON)) {
                    if (!this.doMove(false)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else if (!this.doMove(false)) {
                return false;
            }

            this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_PISTON_IN);
            return true;
        }

        return false;
    }

    private boolean isPowered() {
        BlockFace face = getBlockFace();

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
        BlockFace direction = getBlockFace();
        BlocksCalculator calculator = new BlocksCalculator(extending);

        if (!calculator.canMove()) {
            return false;
        } else {
            List<Vector3> attached = Collections.emptyList();

            if (this.sticky || extending) {
                List<Block> destroyBlocks = calculator.getBlocksToDestroy();
                for (int i = destroyBlocks.size() - 1; i >= 0; --i) {
                    Block block = destroyBlocks.get(i);
                    this.level.useBreakOn(block, null, null, false);
                }

                List<Block> newBlocks = calculator.getBlocksToMove();
                attached = newBlocks.stream().map(b -> b.add(0)).collect(Collectors.toList());

                BlockFace side = extending ? direction : direction.getOpposite();

                for (Block newBlock : newBlocks) {
                    Vector3 oldPos = newBlock.add(0);
                    newBlock.position(newBlock.add(0).getSide(side));

                    BlockEntity blockEntity = this.level.getBlockEntity(oldPos);

                    this.level.setBlock(newBlock, Block.get(BlockID.MOVING_BLOCK));

                    CompoundTag nbt = BlockEntity.getDefaultCompound(newBlock, BlockEntity.MOVING_BLOCK)
                            .putInt("pistonPosX", this.getFloorX())
                            .putInt("pistonPosY", this.getFloorY())
                            .putInt("pistonPosZ", this.getFloorZ())
                            .putCompound("movingBlock", new CompoundTag()
                                    .putInt("id", newBlock.getId()) //only for nukkit purpose
                                    .putInt("meta", newBlock.getDamage()) //only for nukkit purpose
                                    .putShort("val", newBlock.getDamage())
                                    .putString("name", GlobalBlockPalette.getName(newBlock.getId(), newBlock.getDamage()))
                            );

                    if (blockEntity != null) {
                        blockEntity.saveNBT();

                        CompoundTag t = new CompoundTag(blockEntity.namedTag.getTags());
                        t.print(System.out);

                        nbt.putCompound("movingEntity", t);
                        blockEntity.close();
                    }

                    new BlockEntityMovingBlock(this.level.getChunk(newBlock.getChunkX(), newBlock.getChunkZ()), nbt);

                    if (this.level.getBlockIdAt(oldPos.getFloorX(), oldPos.getFloorY(), oldPos.getFloorZ()) != BlockID.MOVING_BLOCK) {
                        this.level.setBlock(oldPos, Block.get(BlockID.AIR));
                    }
                }
            }

            if (extending) {
                this.level.setBlock(this.getSide(direction), new BlockPistonHead(this.getDamage()));
            }

            BlockEntityPistonArm blockEntity = (BlockEntityPistonArm) this.level.getBlockEntity(this);
            blockEntity.move(extending, attached);
            return true;
        }
    }

    public static boolean canPush(Block block, BlockFace face, boolean destroyBlocks) {
        if (block.canBePushed() && block.getY() >= 0 && (face != BlockFace.DOWN || block.getY() != 0) &&
                block.getY() <= 255 && (face != BlockFace.UP || block.getY() != 255)) {

            if (block instanceof BlockFlowable) {
                return destroyBlocks;
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

        private final List<Block> toMove = new ArrayList<>();
        private final List<Block> toDestroy = new ArrayList<>();

        public BlocksCalculator(boolean extending) {
            this.pistonPos = getLocation();

            BlockFace face = getBlockFace();
            if (!extending) {
                this.armPos = pistonPos.getSide(face);
            }

            if (extending) {
                this.moveDirection = face;
                this.blockToMove = getSide(face);
            } else {
                this.moveDirection = face.getOpposite();
                this.blockToMove = getSide(face, 2);
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
                for (Block b : new ArrayList<>(this.toMove)) {
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

                    for (int step = count - 1; step >= 0; step--) {
                        this.toMove.add(origin.getSide(this.moveDirection.getOpposite(), step));
                        ++blockCount;
                    }

                    int steps = 1;

                    while (true) {
                        Block nextBlock = origin.getSide(this.moveDirection, steps);
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

                        if (nextBlock.getId() == AIR || nextBlock.equals(armPos)) {
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
