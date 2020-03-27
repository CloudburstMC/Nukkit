package cn.nukkit.entity.impl;

import cn.nukkit.entity.EntityType;
import cn.nukkit.level.Location;
import cn.nukkit.math.BlockFace;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class HangingEntity extends BaseEntity {
    protected byte direction;

    public HangingEntity(EntityType<?> type, Location location) {
        super(type, location);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.setMaxHealth(1);
        this.setHealth(1);
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForByte("Direction", v -> this.direction = v);
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        tag.byteTag("Direction", this.direction);
        tag.intTag("TileX", (int) this.getX());
        tag.intTag("TileY", (int) this.getY());
        tag.intTag("TileZ", (int) this.getZ());
    }

    @Override
    public BlockFace getDirection() {
        return BlockFace.fromIndex(this.direction);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        if (!this.isAlive()) {

            this.despawnFromAll();
            if (!this.isPlayer) {
                this.close();
            }

            return true;
        }

        if (this.lastYaw != this.getY() || !this.position.equals(this.lastPosition)) {
            this.despawnFromAll();

            this.direction = (byte) (this.getYaw() / 90);

            this.lastYaw = this.getYaw();
            this.lastPosition = this.position;

            this.spawnToAll();

            return true;
        }

        return false;
    }

    protected boolean isSurfaceValid() {
        return true;
    }

}
