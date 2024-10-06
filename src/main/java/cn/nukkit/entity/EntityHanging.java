package cn.nukkit.entity;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public abstract class EntityHanging extends Entity {

    protected int direction;

    public EntityHanging(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        this.updateMode = 3;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(1);
        super.initEntity();
        this.setHealth(1);

        if (this.namedTag.contains("Direction")) {
            this.direction = this.namedTag.getByte("Direction");
        } else if (this.namedTag.contains("Dir")) {
            int d = this.namedTag.getByte("Dir");
            if (d == 2) {
                this.direction = 0;
            } else if (d == 0) {
                this.direction = 2;
            }
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putByte("Direction", this.getDirection().getHorizontalIndex());
        this.namedTag.putInt("TileX", (int) this.x);
        this.namedTag.putInt("TileY", (int) this.y);
        this.namedTag.putInt("TileZ", (int) this.z);
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

        if (this.updateMode % 2 == 1) {
            this.updateMode = 3;
        }

        int tickDiff = currentTick - this.lastUpdate;
        if (tickDiff <= 0 && !this.justCreated) {
            return false;
        }

        this.minimalEntityTick(currentTick, tickDiff);

        if (!this.isAlive()) {
            this.close();
        }

        if (!this.isAlive()) {
            this.close();
            return false;
        }

        if (!this.isSurfaceValid()) {
            this.dropItem();
            this.close();
            return false;
        }

        if (this.lastYaw != this.yaw || this.lastX != this.x || this.lastY != this.y || this.lastZ != this.z) {
            this.despawnFromAll();

            this.direction = (int) (this.yaw / 90);

            this.lastYaw = this.yaw;
            this.lastX = this.x;
            this.lastY = this.y;
            this.lastZ = this.z;

            this.spawnToAll();
            return false;
        }

        return false;
    }

    protected void dropItem() {

    }

    protected boolean isSurfaceValid() {
        return true;
    }
}
