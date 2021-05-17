package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityMinecartAbstract;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.OptionalBoolean;

import javax.annotation.Nonnull;

/**
 * @author CreeperFace (Nukkit Project), larryTheCoder (Minecart and Riding Project)
 * @since 2015/11/22 
 */
public class BlockRailDetector extends BlockRail {

    public BlockRailDetector() {
        this(0);
        canBePowered = true;
    }

    public BlockRailDetector(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DETECTOR_RAIL;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return ACTIVABLE_PROPERTIES;
    }

    @Override
    public String getName() {
        return "Detector Rail";
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
                getFloorX() + 0.125D,
                getFloorY(),
                getFloorZ() + 0.125D,
                getFloorX() + 0.875D,
                getFloorY() + 0.525D,
                getFloorZ() + 0.875D))) {
            if (entity instanceof EntityMinecartAbstract) {
                isPowered = true;
                break;
            }
        }

        if (isPowered && !wasPowered) {
            setActive(true);
            level.scheduleUpdate(this, this, 0);
            level.scheduleUpdate(this, this.down(), 0);
        }

        if (!isPowered && wasPowered) {
            setActive(false);
            level.scheduleUpdate(this, this, 0);
            level.scheduleUpdate(this, this.down(), 0);
        }

        level.updateComparatorOutputLevel(this);
    }

    @Override
    public boolean isActive() {
        return getBooleanValue(ACTIVE);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public OptionalBoolean isRailActive() {
        return OptionalBoolean.of(getBooleanValue(ACTIVE));
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setRailActive(boolean active) {
        setBooleanValue(ACTIVE, active);
    }
}
