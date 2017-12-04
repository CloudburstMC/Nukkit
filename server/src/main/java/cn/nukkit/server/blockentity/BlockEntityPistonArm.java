package cn.nukkit.server.blockentity;

import cn.nukkit.server.entity.Entity;
import cn.nukkit.server.level.format.FullChunk;
import cn.nukkit.server.math.AxisAlignedBB;
import cn.nukkit.server.math.BlockFace;
import cn.nukkit.server.math.Vector3;
import cn.nukkit.server.nbt.tag.CompoundTag;
import cn.nukkit.server.nbt.tag.IntTag;
import cn.nukkit.server.nbt.tag.ListTag;

/**
 * @author CreeperFace
 */
public class BlockEntityPistonArm extends BlockEntity {

    public float progress = 1.0F;
    public float lastProgress = 1.0F;
    public BlockFace facing;
    public boolean extending = false;
    public boolean sticky = false;
    public byte state = 1;
    public byte newState = 1;
    public Vector3 attachedBlock = null;
    public boolean isMovable = true;
    public boolean powered = false;

    public BlockEntityPistonArm(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        if (nbt.contains("Progress")) {
            this.progress = nbt.getFloat("Progress");
        }

        if (nbt.contains("LastProgress")) {
            this.lastProgress = (float) nbt.getInt("LastProgress");
        }

        if (nbt.contains("Sticky")) {
            this.sticky = nbt.getBoolean("Sticky");
        }

        if (nbt.contains("Extending")) {
            this.extending = nbt.getBoolean("Extending");
        }

        if (nbt.contains("powered")) {
            this.powered = nbt.getBoolean("powered");
        }

        if (nbt.contains("AttachedBlocks")) {
            ListTag blocks = nbt.getList("AttachedBlocks", IntTag.class);
            if (blocks != null && blocks.size() > 0) {
                this.attachedBlock = new Vector3((double) ((IntTag) blocks.get(0)).getData().intValue(), (double) ((IntTag) blocks.get(1)).getData().intValue(), (double) ((IntTag) blocks.get(2)).getData().intValue());
            }
        } else {
            nbt.putList(new ListTag("AttachedBlocks"));
        }

    }

    private void pushEntities() {
        float lastProgress = this.getExtendedProgress(this.lastProgress);
        double x = (double) (lastProgress * (float) this.facing.getXOffset());
        double y = (double) (lastProgress * (float) this.facing.getYOffset());
        double z = (double) (lastProgress * (float) this.facing.getZOffset());
        AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
        Entity[] entities = this.level.getCollidingEntities(bb);
        if (entities.length != 0) {
            ;
        }

    }

    private float getExtendedProgress(float progress) {
        return this.extending ? progress - 1.0F : 1.0F - progress;
    }

    public boolean isBlockEntityValid() {
        return true;
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
        return (new CompoundTag()).putString("id", "PistonArm").putInt("x", (int) this.x).putInt("y", (int) this.y).putInt("z", (int) this.z);
    }
}