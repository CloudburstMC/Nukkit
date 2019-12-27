package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;

/**
 * @author CreeperFace
 */
public class BlockEntityPistonArm extends BlockEntitySpawnable {

    public float progress;
    public float lastProgress;
    public BlockFace facing;
    public boolean extending;
    public boolean sticky;
    public byte state;
    public byte newState;
    public Vector3 attachedBlock;
    public boolean isMovable;
    public boolean powered;

    public BlockEntityPistonArm(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        this.isMovable = true;
        
        if (namedTag.contains("Progress")) {
            this.progress = namedTag.getFloat("Progress");
        }

        if (namedTag.contains("LastProgress")) {
            this.lastProgress = (float) namedTag.getInt("LastProgress");
        }

        if (namedTag.contains("Sticky")) {
            this.sticky = namedTag.getBoolean("Sticky");
        }

        if (namedTag.contains("Extending")) {
            this.extending = namedTag.getBoolean("Extending");
        }

        if (namedTag.contains("powered")) {
            this.powered = namedTag.getBoolean("powered");
        }

        if (namedTag.contains("AttachedBlocks")) {
            ListTag blocks = namedTag.getList("AttachedBlocks", IntTag.class);
            if (blocks != null && blocks.size() > 0) {
                this.attachedBlock = new Vector3((double) ((IntTag) blocks.get(0)).getData(), (double) ((IntTag) blocks.get(1)).getData(), (double) ((IntTag) blocks.get(2)).getData());
            }
        } else {
            namedTag.putList(new ListTag("AttachedBlocks"));
        }

        super.initBlockEntity();
    }

    private void pushEntities() {
        float lastProgress = this.getExtendedProgress(this.lastProgress);
        double x = (double) (lastProgress * (float) this.facing.getXOffset());
        double y = (double) (lastProgress * (float) this.facing.getYOffset());
        double z = (double) (lastProgress * (float) this.facing.getZOffset());
        AxisAlignedBB bb = new SimpleAxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
        Entity[] entities = this.level.getCollidingEntities(bb);
        if (entities.length != 0) {

        }

    }

    private float getExtendedProgress(float progress) {
        return this.extending ? progress - 1.0F : 1.0F - progress;
    }

    public boolean isBlockEntityValid() {
        int blockId = getBlock().getId();
        return blockId == Block.PISTON || blockId == Block.STICKY_PISTON;
    }

    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putBoolean("isMovable", this.isMovable);
        this.namedTag.putByte("State", this.state);
        this.namedTag.putByte("NewState", this.newState);
        this.namedTag.putFloat("Progress", this.progress);
        this.namedTag.putFloat("LastProgress", this.lastProgress);
        this.namedTag.putBoolean("powered", this.powered);
    }

    public CompoundTag getSpawnCompound() {
        return new CompoundTag()
                .putString("id", BlockEntity.PISTON_ARM)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putFloat("Progress", this.progress)
                .putFloat("LastProgress", this.lastProgress)
                .putBoolean("Sticky", this.sticky)
                .putByte("State", this.state)
                .putByte("NewState", this.newState);
    }
}
