package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.impl.vehicle.EntityAbstractMinecart;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.DETECTOR_RAIL;

/**
 * Created on 2015/11/22 by CreeperFace.
 * Contributed by: larryTheCoder on 2017/7/8.
 * <p>
 * Nukkit Project,
 * Minecart and Riding Project,
 * Package cn.nukkit.block in project Nukkit.
 */
public class BlockRailDetector extends BlockRail {

    public BlockRailDetector(Identifier id) {
        super(id);
        canBePowered = true;
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    @Override
    public int getWeakPower(BlockFace side) {
        return isActive() ? 15 : 0;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return isActive() ? 0 : (side == BlockFace.UP ? 15 : 0);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            updateState();
            return type;
        }
        return super.onUpdate(type);
    }

    @Override
    public void onEntityCollide(Entity entity) {
        updateState();
    }

    protected void updateState() {
        boolean wasPowered = isActive();
        boolean isPowered = false;

        for (Entity entity : level.getNearbyEntities(new SimpleAxisAlignedBB(
                getX() + 0.125f,
                getY(),
                getZ() + 0.125f,
                getX() + 0.875f,
                getY() + 0.525f,
                getZ() + 0.875f))) {
            if (entity instanceof EntityAbstractMinecart) {
                isPowered = true;
            }
        }

        if (isPowered && !wasPowered) {
            setActive(true);
            level.scheduleUpdate(this, this.getPosition(), 0);
            level.scheduleUpdate(this, this.getPosition().down(), 0);
        }

        if (!isPowered && wasPowered) {
            setActive(false);
            level.scheduleUpdate(this, this.getPosition(), 0);
            level.scheduleUpdate(this, this.getPosition().down(), 0);
        }

        level.updateComparatorOutputLevel(this.getPosition());
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                Item.get(DETECTOR_RAIL, 0, 1)
        };
    }
}
