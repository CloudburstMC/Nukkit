package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * The base class of all mobs
 */
public abstract class BaseEntity extends EntityCreature implements EntityAgeable {

    public BaseEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public abstract int getKillExperience();

    @Override
    protected boolean applyNameTag(Player player, Item nameTag) {
        String name = nameTag.getCustomName();

        if (!name.isEmpty()) {
            this.namedTag.putString("CustomName", name);
            this.namedTag.putBoolean("CustomNameVisible", true);
            this.setNameTag(name);
            this.setNameTagVisible(true);
            return true; // onInteract: true = decrease count
        }

        return false;
    }

    @Override
    protected void checkGroundState(double movX, double movY, double movZ, double dx, double dy, double dz) {
        if (onGround && movX == 0 && movY == 0 && movZ == 0 && dx == 0 && dy == 0 && dz == 0) {
            return;
        }
        this.isCollidedVertically = movY != dy;
        this.isCollidedHorizontally = (movX != dx || movZ != dz);
        this.isCollided = (this.isCollidedHorizontally || this.isCollidedVertically);
        this.onGround = (movY != dy && movY < 0);
    }

    @Override
    protected void checkBlockCollision() {
        for (Block block : this.getCollisionBlocks()) {
            block.onEntityCollide(this);
        }

        // TODO: portals
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        if (!this.closed) {
            if (!this.isAlive()) {
                this.despawnFromAll();
                this.close();
                return false;
            }

            if (!this.isImmobile()) {
                if (this.age % 10 == 0) {
                    if (this.onGround) {
                        if (level.getBlock(chunk, getFloorX(), getFloorY() - 1, getFloorZ(), false).canPassThrough()) {
                            this.onGround = false;
                        }
                    }
                }

                if (!this.onGround || Math.abs(this.motionX) > 0.1 || Math.abs(this.motionY) > 0.1 || Math.abs(this.motionZ) > 0.1) {
                    this.motionY -= 0.08;
                    this.move(this.motionX, this.motionY, this.motionZ);
                }

                this.motionX *= 0.9;
                this.motionY *= 0.9;
                this.motionZ *= 0.9;
            }

            super.entityBaseTick(tickDiff); // updateMovement is called at the end of onUpdate after entityBaseTick is called
            return true;
        }
        return false;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public void setBaby(boolean baby) {
        //TODO
    }
}
