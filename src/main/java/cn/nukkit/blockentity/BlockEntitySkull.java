package cn.nukkit.blockentity;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Snake1999
 * @since 2016/2/3
 */
public class BlockEntitySkull extends BlockEntitySpawnable {
    public BlockEntitySkull(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
    
    private boolean mouthMoving;
    
    private int mouthTickCount;

    @Override
    protected void initBlockEntity() {
        if (!namedTag.contains("SkullType")) {
            namedTag.putByte("SkullType", 0);
        }
        if (!namedTag.contains("Rot")) {
            namedTag.putByte("Rot", 0);
        }

        if (namedTag.containsByte("MouthMoving")) {
            mouthMoving = namedTag.getBoolean("MouthMoving");
        }

        if (namedTag.containsInt("MouthTickCount")) {
            mouthTickCount = NukkitMath.clamp(namedTag.getInt("MouthTickCount"), 0, 60);
        }

        super.initBlockEntity();
    }

    @Override
    public boolean onUpdate() {
        if (isMouthMoving()) {
            mouthTickCount++;
            setDirty();
            return true;
        }
        return false;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setMouthMoving(boolean mouthMoving) {
        if (this.mouthMoving == mouthMoving) {
            return;
        }
        this.mouthMoving = mouthMoving;
        if (mouthMoving) {
            scheduleUpdate();
        }
        this.level.updateComparatorOutputLevelSelective(this, true);
        spawnToAll();
        if (chunk != null) {
            setDirty();
        }
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isObservable() {
        return false;
    }

    @Override
    public void setDirty() {
        chunk.setChanged();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isMouthMoving() {
        return mouthMoving;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getMouthTickCount() {
        return mouthTickCount;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setMouthTickCount(int mouthTickCount) {
        if (this.mouthTickCount == mouthTickCount) {
            return;
        }
        this.mouthTickCount = mouthTickCount;
        spawnToAll();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag
                .putBoolean("MouthMoving", this.mouthMoving)
                .putInt("MouthTickCount", mouthTickCount)
                .remove("Creator");
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock().getId() == Block.SKULL_BLOCK;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return new CompoundTag()
                .putString("id", BlockEntity.SKULL)
                .put("SkullType", this.namedTag.get("SkullType"))
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .put("Rot", this.namedTag.get("Rot"))
                .putBoolean("MouthMoving", this.mouthMoving)
                .putInt("MouthTickCount", mouthTickCount);
    }

}
