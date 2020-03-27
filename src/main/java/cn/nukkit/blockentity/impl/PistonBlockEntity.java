package cn.nukkit.blockentity.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.Piston;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockEntityRegistry;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.nbt.tag.IntTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author CreeperFace
 */
public class PistonBlockEntity extends BaseBlockEntity implements Piston {

    public static final float MOVE_STEP = Float.valueOf(0.5f);

    private List<Vector3i> attachedBlocks = new ArrayList<>();
    public BlockFace facing;
    public boolean extending = false;
    public boolean powered = false;
    private float progress = 1.0F;
    private float lastProgress = 1.0F;
    private int state = 1;
    private int newState = 1;
    private boolean sticky = false;

    public PistonBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        this.progress = tag.getFloat("Progress", 1.0f);
        this.lastProgress = tag.getFloat("LastProgress", 1.0f);
        this.state = tag.getByte("State", (byte) 1);
        this.newState = tag.getByte("NewState", (byte) 1);
        this.sticky = tag.getBoolean("Sticky");

        List<IntTag> attachedBlocks = tag.getList("AttachedBlocks", IntTag.class);

        for (int i = 0; i < attachedBlocks.size(); i += 3) {
            this.attachedBlocks.add(Vector3i.from(
                    attachedBlocks.get(i).getPrimitiveValue(),
                    attachedBlocks.get(i + 1).getPrimitiveValue(),
                    attachedBlocks.get(i + 2).getPrimitiveValue()
            ));
        }
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);
    }

    private float getExtendedProgress(float progress) {
        return this.extending ? progress - 1.0F : 1.0F - progress;
    }

    public boolean isValid() {
        return true;
    }

    public float getProgress() {
        return progress;
    }

    public float getLastProgress() {
        return lastProgress;
    }

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    public int getState() {
        return state;
    }

    public int getNewState() {
        return newState;
    }

    @Override
    public boolean isPowered() {
        return powered;
    }

    @Override
    public void setPowered(boolean powered) {
        if (this.powered != powered) {
            this.powered = powered;
            this.setDirty();
        }
    }

    private void moveCollidedEntities() {
        BlockFace pushDir = this.extending ? facing : facing.getOpposite();
        for (Vector3i pos : this.attachedBlocks) {
            BlockEntity blockEntity = this.getLevel().getBlockEntity(pushDir.getOffset(pos));

            if (blockEntity instanceof MovingBlockEntity) {
                ((MovingBlockEntity) blockEntity).moveCollidedEntities(this, pushDir);
            }
        }

        Vector3i pos = getPosition();
        AxisAlignedBB bb = new SimpleAxisAlignedBB(0, 0, 0, 1, 1, 1).getOffsetBoundingBox(
                pos.getX() + (pushDir.getXOffset() * progress),
                pos.getY() + (pushDir.getYOffset() * progress),
                pos.getZ() + (pushDir.getZOffset() * progress)
        );

        Set<Entity> entities = this.getLevel().getCollidingEntities(bb);

        for (Entity entity : entities) {
            moveEntity(entity, pushDir);
        }
    }

    void moveEntity(Entity entity, BlockFace moveDirection) {
        if (!entity.canBePushed()) {
            return;
        }

        //TODO: event

        if (entity instanceof Player) {
            return;
        }

        entity.onPushByPiston(this);

        if (!entity.isClosed()) {
            float diff = Math.abs(this.progress - this.lastProgress);

            ((BaseEntity) entity).move(
                    diff * moveDirection.getXOffset(),
                    diff * moveDirection.getYOffset(),
                    diff * moveDirection.getZOffset()
            );
        }
    }

    @Override
    public boolean onUpdate() {
        boolean hasUpdate = true;

        if (this.extending) {
            this.progress = Math.min(1, this.progress + MOVE_STEP);
            this.lastProgress = Math.min(1, this.lastProgress + MOVE_STEP);
        } else {
            this.progress = Math.max(0, this.progress - MOVE_STEP);
            this.lastProgress = Math.max(0, this.lastProgress - MOVE_STEP);
        }

        this.moveCollidedEntities();

        if (this.progress == this.lastProgress) {
            this.state = this.newState = extending ? 2 : 0;

            BlockFace pushDir = this.extending ? facing : facing.getOpposite();

            for (Vector3i pos : this.attachedBlocks) {
                BlockEntity movingBlock = this.getLevel().getBlockEntity(pushDir.getOffset(pos));

                if (movingBlock instanceof MovingBlockEntity) {
                    movingBlock.close();
                    Block moved = ((MovingBlockEntity) movingBlock).getMovingBlock();

                    CompoundTag nbt = ((MovingBlockEntity) movingBlock).getBlockEntity();

                    if (nbt != null) {
                        BlockEntityType<?> type = BlockEntityRegistry.get().getBlockEntityType(nbt.getString("id"));
                        BlockEntity blockEntity = BlockEntityRegistry.get().newEntity(type, ((MovingBlockEntity) movingBlock).getChunk(), movingBlock.getPosition());
                        blockEntity.loadAdditionalData(nbt);
                    }

                    this.getLevel().setBlock(pos, moved);
                }
            }

            if (!extending && this.getLevel().getBlockId(facing.getOffset(getPosition())) == BlockIds.PISTON_ARM_COLLISION) {
                this.getLevel().setBlock(facing.getOffset(getPosition()), BlockIds.AIR);
                this.movable = true;
            }

            this.getLevel().scheduleUpdate(this.getBlock(), 1);
            this.attachedBlocks.clear();
            hasUpdate = false;
        }

        this.spawnToAll();
        return super.onUpdate() || hasUpdate;
    }

    public void move(boolean extending, List<Vector3i> attachedBlocks) {
        this.extending = extending;
        this.lastProgress = this.progress = extending ? 0 : 1;
        this.state = this.newState = extending ? 1 : 3;
        this.attachedBlocks = attachedBlocks;
        this.movable = false;

        this.spawnToAll();
        this.lastProgress = extending ? -MOVE_STEP : 1 + MOVE_STEP;
        this.moveCollidedEntities();
        this.scheduleUpdate();
    }

    @Override
    public boolean isSpawnable() {
        return true;
    }
}