package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.Faceable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CreeperFace
 */
public class BlockEntityPistonArm extends BlockEntitySpawnable {

    public static final float MOVE_STEP = Float.valueOf(0.5f);

    public float progress;
    public float lastProgress = 1.0F;
    public BlockFace facing;
    public boolean extending;
    public boolean sticky;
    public int state;
    public int newState = 1;
    public List<Vector3> attachedBlocks;
    public boolean powered;

    public BlockEntityPistonArm(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        if (namedTag.contains("Progress")) {
            this.progress = namedTag.getFloat("Progress");
        }

        if (namedTag.contains("LastProgress")) {
            this.lastProgress = (float) namedTag.getInt("LastProgress");
        }

        this.sticky = namedTag.getBoolean("Sticky");
        this.extending = namedTag.getBoolean("Extending");
        this.powered = namedTag.getBoolean("powered");


        if (namedTag.contains("facing")) {
            this.facing = BlockFace.fromIndex(namedTag.getInt("facing"));
        } else {
            Block b = this.getLevelBlock();

            if (b instanceof Faceable) {
                this.facing = ((Faceable) b).getBlockFace();
            }
        }

        attachedBlocks = new ArrayList<>();

        if (namedTag.contains("AttachedBlocks")) {
            ListTag blocks = namedTag.getList("AttachedBlocks", IntTag.class);
            if (blocks != null && blocks.size() > 0) {
                for (int i = 0; i < blocks.size(); i += 3) {
                    this.attachedBlocks.add(new Vector3(
                            ((IntTag) blocks.get(i)).data,
                            ((IntTag) blocks.get(i + 1)).data,
                            ((IntTag) blocks.get(i + 1)).data
                    ));
                }
            }
        } else {
            namedTag.putList(new ListTag<>("AttachedBlocks"));
        }

        super.initBlockEntity();
    }

    private void moveCollidedEntities() {
//        float lastProgress = this.getExtendedProgress(this.lastProgress);
//        double x = (double) (lastProgress * (float) this.facing.getXOffset());
//        double y = (double) (lastProgress * (float) this.facing.getYOffset());
//        double z = (double) (lastProgress * (float) this.facing.getZOffset());
//        AxisAlignedBB bb = new SimpleAxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
//        Entity[] entities = this.level.getCollidingEntities(bb);
//        if (entities.length != 0) {
//
//        }

        BlockFace pushDir = this.extending ? facing : facing.getOpposite();
        for (Vector3 pos : this.attachedBlocks) {
            BlockEntity blockEntity = this.level.getBlockEntity(pos.getSide(pushDir));

            if (blockEntity instanceof BlockEntityMovingBlock) {
                ((BlockEntityMovingBlock) blockEntity).moveCollidedEntities(this);
            }
        }
    }

    public void move(boolean extending, List<Vector3> attachedBlocks) {
        this.extending = extending;
        this.lastProgress = this.progress = extending ? 0 : 1;
        this.state = this.newState = extending ? 1 : 3;
        this.attachedBlocks = attachedBlocks;

        this.level.addChunkPacket(getChunkX(), getChunkZ(), getSpawnPacket());
        this.lastProgress = extending ? -MOVE_STEP : 1 + MOVE_STEP;
        this.moveCollidedEntities();
        this.scheduleUpdate();
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

        this.level.addChunkPacket(getChunkX(), getChunkZ(), getSpawnPacket());
        this.moveCollidedEntities();

        if (this.progress == this.lastProgress) {
            this.state = this.newState = extending ? 2 : 0;

            BlockFace pushDir = this.extending ? facing : facing.getOpposite();

            for (Vector3 pos : this.attachedBlocks) {
                BlockEntity movingBlock = this.level.getBlockEntity(pos.getSide(pushDir));

                if (movingBlock instanceof BlockEntityMovingBlock) {
                    movingBlock.close();
                    Block moved = ((BlockEntityMovingBlock) movingBlock).getMovingBlock();

                    this.level.setBlock(movingBlock, moved);

                    CompoundTag blockEntity = ((BlockEntityMovingBlock) movingBlock).getBlockEntity();

                    if (blockEntity != null) {
                        blockEntity.putInt("x", movingBlock.getFloorX());
                        blockEntity.putInt("y", movingBlock.getFloorY());
                        blockEntity.putInt("z", movingBlock.getFloorZ());
                        BlockEntity.createBlockEntity(blockEntity.getString("id"), this.level.getChunk(movingBlock.getChunkX(), movingBlock.getChunkZ()), blockEntity);
                    }
                }
            }

            if (!extending && this.level.getBlock(getSide(facing)).getId() == BlockID.PISTON_HEAD) {
                this.level.setBlock(getSide(facing), new BlockAir());
            }

            this.level.scheduleUpdate(this.getLevelBlock(), 1);
            this.attachedBlocks.clear();
            hasUpdate = false;
        }

        return super.onUpdate() || hasUpdate;
    }

    private float getExtendedProgress(float progress) {
        return this.extending ? progress - 1 : 1 - progress;
    }

    public boolean isBlockEntityValid() {
        return true;
    }

    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putByte("State", this.state);
        this.namedTag.putByte("NewState", this.newState);
        this.namedTag.putFloat("Progress", this.progress);
        this.namedTag.putFloat("LastProgress", this.lastProgress);
        this.namedTag.putBoolean("powered", this.powered);
        this.namedTag.putList(getAttachedBlocks());
        this.namedTag.putInt("facing", this.facing.getIndex());
    }

    public CompoundTag getSpawnCompound() {
        return new CompoundTag()
                .putString("id", BlockEntity.PISTON_ARM)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putFloat("Progress", this.progress)
                .putFloat("LastProgress", this.lastProgress)
                .putBoolean("isMovable", this.movable)
                .putList(getAttachedBlocks())
                .putList(new ListTag<>("BreakBlocks"))
                .putBoolean("Sticky", this.sticky)
                .putByte("State", this.state)
                .putByte("NewState", this.newState);
    }

    private ListTag<IntTag> getAttachedBlocks() {
        ListTag<IntTag> attachedBlocks = new ListTag<>("AttachedBlocks");
        for (Vector3 block : this.attachedBlocks) {
            attachedBlocks.add(new IntTag("", block.getFloorX()));
            attachedBlocks.add(new IntTag("", block.getFloorY()));
            attachedBlocks.add(new IntTag("", block.getFloorZ()));
        }

        return attachedBlocks;
    }
}