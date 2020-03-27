package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityTypes;
import cn.nukkit.blockentity.MovingBlock;
import cn.nukkit.blockentity.Piston;
import cn.nukkit.blockentity.impl.MovingBlockEntity;
import cn.nukkit.blockentity.impl.PistonBlockEntity;
import cn.nukkit.event.block.BlockPistonEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockEntityRegistry;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;
import com.google.common.collect.Lists;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.protocol.bedrock.data.SoundEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static cn.nukkit.block.BlockIds.*;

/**
 * @author CreeperFace
 */
public abstract class BlockPistonBase extends BlockSolid implements Faceable {

    public boolean sticky = false;

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
        Vector3f playerPos = player.getPosition();

        if (Math.abs(playerPos.getFloorX() - this.getX()) <= 1 && Math.abs(playerPos.getFloorZ() - this.getZ()) <= 1) {
            double y = player.getY() + player.getEyeHeight();

            if (y - this.getY() > 2) {
                this.setMeta(BlockFace.UP.getIndex());
            } else if (this.getY() - y > 0) {
                this.setMeta(BlockFace.DOWN.getIndex());
            } else {
                this.setMeta(player.getHorizontalFacing().getIndex());
            }
        } else {
            this.setMeta(player.getHorizontalFacing().getIndex());
        }
        this.level.setBlock(block.getPosition(), this, true, true);

        Piston piston = BlockEntityRegistry.get().newEntity(BlockEntityTypes.PISTON, this.getChunk(), this.getPosition());
        piston.setSticky(this.sticky);
        piston.setPowered(isPowered());

        this.checkState(piston.isPowered());
        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        super.onBreak(item);

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
            if (!this.level.getServer().isRedstoneEnabled()) {
                return 0;
            }

            BlockEntity blockEntity = this.level.getBlockEntity(this.getPosition());
            if (blockEntity instanceof PistonBlockEntity) {
                PistonBlockEntity arm = (PistonBlockEntity) blockEntity;
                boolean powered = this.isPowered();

                if (arm.getState() % 2 == 0 && arm.powered != powered && checkState(powered)) {
                    arm.powered = powered;

                    if (arm.getChunk() != null) {
                        arm.getChunk().setDirty();
                    }
                }
            }

            return type;
        }
    }

    private boolean checkState(Boolean isPowered) {
        if (!this.level.getServer().isRedstoneEnabled()) {
            return false;
        }

        if (isPowered == null) {
            isPowered = this.isPowered();
        }

        if (isPowered && !isExtended()) {
            if (!this.doMove(true)) {
                return false;
            }

            this.getLevel().addLevelSoundEvent(this.getPosition(), SoundEvent.PISTON_OUT);
            return true;
        } else if (!isPowered && isExtended()) {
            if (!this.doMove(false)) {
                return false;
            }

            this.getLevel().addLevelSoundEvent(this.getPosition(), SoundEvent.PISTON_IN);
            return true;
        }

        return false;
    }

    private boolean isPowered() {
        BlockFace face = getBlockFace();

        for (BlockFace side : BlockFace.values()) {
            if (side == face) {
                continue;
            }

            Block b = this.getSide(side);

            if (b.getId() == REDSTONE_WIRE && b.getMeta() > 0) {
                return true;
            }

            if (this.level.isSidePowered(b.getPosition(), side)) {
                return true;
            }
        }

        return false;
    }

    private boolean doMove(boolean extending) {
        BlockFace direction = getBlockFace();
        BlocksCalculator calculator = new BlocksCalculator(extending);

        boolean canMove = calculator.canMove();

        if (!canMove && extending) {
            return false;
        }

        List<Vector3i> attached = Collections.emptyList();

        BlockPistonEvent event = new BlockPistonEvent(this, direction, calculator.getBlocksToMove(), calculator.getBlocksToDestroy(), extending);
        this.level.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        if (canMove && (this.sticky || extending)) {
            List<Block> destroyBlocks = calculator.getBlocksToDestroy();
            for (int i = destroyBlocks.size() - 1; i >= 0; --i) {
                Block block = destroyBlocks.get(i);
                this.level.useBreakOn(block.getPosition(), null, null, false);
            }

            List<Block> newBlocks = calculator.getBlocksToMove();

            attached = newBlocks.stream().map(Block::getPosition).collect(Collectors.toList());

            BlockFace side = extending ? direction : direction.getOpposite();

            for (Block newBlock : newBlocks) {
                Vector3i oldPos = newBlock.getPosition();
                Vector3i newPos = side.getOffset(oldPos);

                BlockEntity blockEntity = this.level.getBlockEntity(oldPos);

                this.level.setBlock(newPos, Block.get(MOVING_BLOCK), true);

                CompoundTagBuilder nbt = CompoundTagBuilder.builder()
                        .intTag("pistonPosX", position.getX())
                        .intTag("pistonPosY", position.getY())
                        .intTag("pistonPosZ", position.getZ())
                        .tag(
                                CompoundTagBuilder.builder()
                                        .shortTag("val", (short) newBlock.getMeta())
                                        .stringTag("name", newBlock.getId().getName())
                                        .build("movingBlock")
                        );

                if (blockEntity != null && !(blockEntity instanceof MovingBlockEntity)) {
                    CompoundTagBuilder entityBuilder = CompoundTagBuilder.builder();
                    blockEntity.saveAdditionalData(entityBuilder);

                    nbt.tag(entityBuilder.build("movingEntity"));
                    blockEntity.close();
                }

                MovingBlock movingBlock = BlockEntityRegistry.get().newEntity(BlockEntityTypes.MOVING_BLOCK, this.getChunk(), this.getPosition());
                movingBlock.loadAdditionalData(nbt.buildRootTag());

                if (this.level.getBlockId(oldPos) != MOVING_BLOCK) {
                    this.level.setBlock(oldPos, Block.get(AIR));
                }
            }
        }

        if (extending) {
            this.level.setBlock(direction.getOffset(position), Block.get(PISTON_ARM_COLLISION, getMeta()));
        }

        PistonBlockEntity blockEntity = (PistonBlockEntity) this.level.getBlockEntity(this.position);
        blockEntity.move(extending, attached);
        return true;
    }

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

            BlockEntity be = block.level.getBlockEntity(block.getPosition());
            return be == null || be.isMovable();
        }

        return false;
    }

    public class BlocksCalculator {

        private final Vector3i pistonPos;
        private Vector3i armPos;
        private final Block blockToMove;
        private final BlockFace moveDirection;
        private final boolean extending;

        private final List<Block> toMove = new ArrayList<>();
        private final List<Block> toDestroy = new ArrayList<>();

        public BlocksCalculator(boolean extending) {
            this.pistonPos = position;
            this.extending = extending;

            BlockFace face = getBlockFace();
            if (!extending) {
                this.armPos = face.getOffset(pistonPos);
            }

            if (extending) {
                this.moveDirection = face;
                this.blockToMove = getSide(face);
            } else {
                this.moveDirection = face.getOpposite();
                if (sticky) {
                    this.blockToMove = getSide(face, 2);
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

            if (!this.addBlockLine(this.blockToMove, this.moveDirection)) {
                return false;
            }

            for (int i = 0; i < this.toMove.size(); ++i) {
                Block b = this.toMove.get(i);

                if (b.getId() == SLIME && !this.addBranchingBlocks(b)) {
                    return false;
                }
            }

            return true;
        }

        private boolean addBlockLine(Block origin, BlockFace from) {
            Block block = origin.clone();

            if (block.getId() == AIR) {
                return true;
            }

            if (!canPush(origin, this.moveDirection, false, extending)) {
                return true;
            }

            if (origin.getPosition().equals(this.pistonPos)) {
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

            while (block.getId() == SLIME) {
                block = origin.getSide(this.moveDirection.getOpposite(), count);

                if (block.getId() == AIR || !canPush(block, this.moveDirection, false, extending) || block.getPosition().equals(this.pistonPos)) {
                    break;
                }

                if (block.breaksWhenMoved() && block.sticksToPiston()) {
                    this.toDestroy.add(block);
                    break;
                }

                if (++count + this.toMove.size() > 12) {
                    return false;
                }

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

                        if (b.getId() == SLIME && !this.addBranchingBlocks(b)) {
                            return false;
                        }
                    }

                    return true;
                }

                if (nextBlock.getId() == AIR || nextBlock.getPosition().equals(armPos)) {
                    return true;
                }

                if (!canPush(nextBlock, this.moveDirection, true, extending) || nextBlock.getPosition().equals(this.pistonPos)) {
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
                if (face.getAxis() != this.moveDirection.getAxis() && !this.addBlockLine(block.getSide(face), face)) {
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
        return Item.get(PISTON);
    }

    @Override
    public BlockFace getBlockFace() {
        BlockFace face = BlockFace.fromIndex(this.getMeta());

        return face.getHorizontalIndex() >= 0 ? face.getOpposite() : face;
    }

    public boolean canWaterlogSource() {
        return true;
    }
}
