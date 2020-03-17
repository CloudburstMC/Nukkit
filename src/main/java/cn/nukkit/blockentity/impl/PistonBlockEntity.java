package cn.nukkit.blockentity.impl;

import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.Piston;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
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

    private final List<Vector3i> attachedBlocks = new ArrayList<>();
    private final List<Vector3i> breakBlocks = new ArrayList<>();
    public BlockFace facing;
    public boolean extending = false;
    public boolean powered = false;
    private float progress = 1.0F;
    private float lastProgress = 1.0F;
    private byte state = 1;
    private byte newState = 1;
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

        List<IntTag> breakBlocks = tag.getList("BreakBlocks", IntTag.class);

        for (int i = 0; i < breakBlocks.size(); i += 3) {
            this.breakBlocks.add(Vector3i.from(
                    breakBlocks.get(i).getPrimitiveValue(),
                    breakBlocks.get(i + 1).getPrimitiveValue(),
                    breakBlocks.get(i + 2).getPrimitiveValue()
            ));
        }
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);
    }

    private void pushEntities() {
        float lastProgress = this.getExtendedProgress(this.lastProgress);
        float x = lastProgress * this.facing.getXOffset();
        float y = lastProgress * this.facing.getYOffset();
        float z = lastProgress * this.facing.getZOffset();
        AxisAlignedBB bb = new SimpleAxisAlignedBB(x, y, z, x + 1f, y + 1f, z + 1f);
        Set<Entity> entities = this.getLevel().getCollidingEntities(bb);
        if (!entities.isEmpty()) {

        }

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
}