package cn.nukkit.blockentity.impl;

import cn.nukkit.block.BlockIds;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.Skull;
import cn.nukkit.level.chunk.Chunk;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

/**
 * Created by Snake1999 on 2016/2/3.
 * Package cn.nukkit.blockentity in project Nukkit.
 */
public class SkullBlockEntity extends BaseBlockEntity implements Skull {
    private float rotation;
    private int skullType;
    private boolean mouthMoving;
    private int mouthTickCount;

    public SkullBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForByte("SkullType", this::setSkullType);
        tag.listenForFloat("Rotation", this::setRotation);
        tag.listenForInt("MouthTickCount", this::setMouthTickCount);
        tag.listenForBoolean("MouthMoving", this::setMouthMoving);
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        tag.byteTag("SkullType", (byte) this.getSkullType());
        tag.floatTag("Rotation", this.getRotation());
        tag.intTag("MouthTickCount", this.getMouthTickCount());
        tag.booleanTag("MouthMoving", this.isMouthMoving());
    }

    @Override
    public boolean isValid() {
        return getBlock().getId() == BlockIds.SKULL;
    }

    public float getRotation() {
        return this.rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public int getMouthTickCount() {
        return mouthTickCount;
    }

    public void setMouthTickCount(int mouthTickCount) {
        this.mouthTickCount = mouthTickCount;
    }

    public boolean isMouthMoving() {
        return mouthMoving;
    }

    public void setMouthMoving(boolean mouthMoving) {
        this.mouthMoving = mouthMoving;
    }

    public int getSkullType() {
        return skullType;
    }

    public void setSkullType(int skullType) {
        this.skullType = skullType;
    }

    @Override
    public boolean isSpawnable() {
        return true;
    }
}