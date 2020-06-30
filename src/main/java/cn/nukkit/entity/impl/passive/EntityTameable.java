package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityOwnable;
import cn.nukkit.entity.EntityType;
import cn.nukkit.level.Location;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

import static com.nukkitx.protocol.bedrock.data.entity.EntityData.OWNER_EID;
import static com.nukkitx.protocol.bedrock.data.entity.EntityFlag.SITTING;
import static com.nukkitx.protocol.bedrock.data.entity.EntityFlag.TAMED;

/**
 * Author: BeYkeRYkt
 * Nukkit Project
 */
public abstract class EntityTameable extends Animal implements EntityOwnable {


    public EntityTameable(EntityType<?> type, Location location) {
        super(type, location);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setOwner(null);
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForLong("OwnerID", v -> {
            this.setOwnerId(v);
            this.setTamed(true);
        });
        tag.listenForBoolean("Sitting", this::setSitting);
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        tag.longTag("OwnerID", this.getOwnerId());
        tag.booleanTag("Sitting", this.isSitting());
    }

    @Override
    public long getOwnerId() {
        return this.data.getLong(OWNER_EID);
    }

    @Override
    public void setOwnerId(long id) {
        this.data.setLong(OWNER_EID, id);
    }

    @Override
    public String getName() {
        return getNameTag();
    }

    public boolean isTamed() {
        return this.data.getFlag(TAMED);
    }

    public void setTamed(boolean value) {
        this.data.setFlag(TAMED, value);
    }

    public boolean isSitting() {
        return this.data.getFlag(SITTING);
    }

    public void setSitting(boolean value) {
        this.data.setFlag(SITTING, value);
    }
}
