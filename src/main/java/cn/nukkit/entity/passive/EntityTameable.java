package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityOwnable;
import cn.nukkit.entity.EntityType;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

import static cn.nukkit.entity.data.EntityData.OWNER_EID;
import static cn.nukkit.entity.data.EntityFlag.SITTING;
import static cn.nukkit.entity.data.EntityFlag.TAMED;

/**
 * Author: BeYkeRYkt
 * Nukkit Project
 */
public abstract class EntityTameable extends Animal implements EntityOwnable {


    public EntityTameable(EntityType<?> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (!hasData(OWNER_EID)) {
            setLongData(OWNER_EID, -1);
        }

        long ownerId = -1;

        if (namedTag != null) {
            if (namedTag.contains("OwnerID")) {
                ownerId = namedTag.getLong("OwnerID");
            }

            if (ownerId != -1) {
                this.setOwnerId(ownerId);
                this.setTamed(true);
            }

            this.setSitting(namedTag.getBoolean("Sitting"));
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putLong("OwnerID", getOwnerId());

        namedTag.putBoolean("Sitting", isSitting());
    }

    @Override
    public long getOwnerId() {
        return getLongData(OWNER_EID);
    }

    @Override
    public void setOwnerId(long id) {
        setLongData(OWNER_EID, id);
    }

    @Override
    public String getName() {
        return getNameTag();
    }

    public boolean isTamed() {
        return getFlag(TAMED);
    }

    public void setTamed(boolean value) {
        setFlag(TAMED, value);
    }

    public boolean isSitting() {
        return getFlag(SITTING);
    }

    public void setSitting(boolean value) {
        setFlag(SITTING, value);
    }
}
