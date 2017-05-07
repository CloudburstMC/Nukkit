package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;

/**
 * @author CreeperFace
 */
public class BlockEntityPistonArm extends BlockEntitySpawnable {

    public float progress = 1f;
    public float lastProgress = 1f;
    public BlockFace facing;
    public boolean extending = false;
    public boolean sticky = false;

    public byte state = 1;
    public byte newState = 1;

    public Vector3 attachedBlock = null;

    public boolean isMovable = true;

    public BlockEntityPistonArm(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        if (nbt.contains("Progress")) {
            this.progress = nbt.getFloat("Progress");
        }

        if (nbt.contains("LastProgress")) {
            this.lastProgress = nbt.getInt("LastProgress");
        }

        if (nbt.contains("Sticky")) {
            this.sticky = nbt.getBoolean("Sticky");
        }

        if (nbt.contains("Extending")) {
            this.extending = nbt.getBoolean("Extending");
        }

        if (nbt.contains("AttachedBlocks")) {
            ListTag<IntTag> blocks = nbt.getList("AttachedBlocks", IntTag.class);
            if (blocks != null && blocks.size() > 0) {
                this.attachedBlock = new Vector3(blocks.get(0).getData(), blocks.get(1).getData(), blocks.get(2).getData());
            }
        } else {
            nbt.putList(new ListTag<IntTag>("AttachedBlocks"));
        }
    }

    /*@Override
    public boolean onUpdate() {
        this.lastProgress = this.progress;

        if (this.lastProgress >= 1) {
            if(this.newState == 2) {
                this.spawnToAll();
                this.state = 2;
                return true;
            }

            this.newState = 2;
            this.spawnToAll();

            this.pushEntities();
        } else {
            this.progress += 0.5;

            if (this.progress >= 1) {
                this.progress = 1;
            }

            this.pushEntities();
            this.spawnToAll();
        }

        return this.state != 2;
    }*/

    private void pushEntities() {
        float lastProgress = this.getExtendedProgress(this.lastProgress);
        double x = lastProgress * (float) this.facing.getXOffset();
        double y = lastProgress * (float) this.facing.getYOffset();
        double z = lastProgress * (float) this.facing.getZOffset();
        AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
        Entity[] entities = this.level.getCollidingEntities(bb);

        if (entities.length != 0) {
            //TODO:
        }
    }

    private float getExtendedProgress(float progress) {
        return this.extending ? progress - 1.0F : 1.0F - progress;
    }

    @Override
    public boolean isBlockEntityValid() {
        return true;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putBoolean("isMovable", this.isMovable);
        this.namedTag.putByte("State", this.state);
        this.namedTag.putByte("NewState", this.newState);
        this.namedTag.putFloat("Progress", this.progress);
        this.namedTag.putFloat("LastProgress", this.lastProgress);
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);

        //System.out.println("spawn  X: "+this.x+"  Y: "+this.y+"  Z: "+this.z);
    }

    @Override
    public CompoundTag getSpawnCompound() {
        /*return new CompoundTag()
                .putString("id", BlockEntity.PISTON_ARM)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putFloat("Progress", this.progress)
                .putFloat("LastProgress", this.lastProgress)
                .putBoolean("isMovable", this.isMovable)
                .putList(new ListTag<>("AttachedBlocks"))
                .putList(new ListTag<>("BreakBlocks"))
                .putBoolean("Sticky", this.sticky)
                .putByte("State", this.state)
                .putByte("NewState", this.newState);*/

        return new CompoundTag()
                .putString("id", BlockEntity.PISTON_ARM)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);
    }
}