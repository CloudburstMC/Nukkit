package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityTarget;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityThrownTrident;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockTarget extends BlockTransparent implements BlockEntityHolder<BlockEntityTarget> {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockTarget() {
        // Does nothing
    }

    @Override
    public int getId() {
        return TARGET;
    }

    @Override
    public String getName() {
        return "Target";
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public Class<? extends BlockEntityTarget> getBlockEntityClass() {
        return BlockEntityTarget.class;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.TARGET;
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    @Override
    public int getWeakPower(BlockFace face) {
        BlockEntityTarget target = getBlockEntity();
        return target == null? 0 : target.getActivePower();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean activatePower(int power) {
        return activatePower(power, 4 * 2);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean activatePower(int power, int ticks) {
        Level level = getLevel();
        if (power <= 0 || ticks <= 0) {
            BlockEntityTarget target = getBlockEntity();
            if (target != null) {
                int currentPower = target.getActivePower();
                target.close();
                if (currentPower != 0 && level.getServer().isRedstoneEnabled()) {
                    level.updateAroundRedstone(this, null);
                }
                return true;
            }
            return false;
        }

        BlockEntityTarget target = getOrCreateBlockEntity();
        int previous = target.getActivePower();
        level.cancelSheduledUpdate(this, this);
        level.scheduleUpdate(this, ticks);
        target.setActivePower(power);
        if (previous != power) {
            level.updateAroundRedstone(this, null);
        }
        return true;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean deactivatePower() {
        return activatePower(0, 0);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            deactivatePower();
            return type;
        }
        return 0;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean onProjectileHit(@Nonnull Entity projectile) {
        int ticks = 8;
        if (projectile instanceof EntityArrow || projectile instanceof EntityThrownTrident) {
            ticks = 20;
        }

        Position relative = subtract(projectile);
        double discard = Math.min(Math.min(relative.x, relative.y), relative.z);
        double[] coords = new double[2];
        int i = 0;
        for (double coord: new double[]{relative.x, relative.y, relative.z}) {
            if (coord != discard) {
                coords[i++] = coord;
            }
        }

        for (i = 0; i < 2 ; i++) {
            if (coords[i] == 0.5) {
                coords[i] = 1;
            } else if (coords[i] <= 0 || coords[i] >= 1) {
                coords[i] = 0;
            } else if (coords[i] < 0.5) {
                coords[i] *= 2;
            } else {
                coords[i] = (coords[i] / (-0.5)) + 2;
            }
        }
        
        double scale = (coords[0] + coords[1]) / 2;
        activatePower(NukkitMath.ceilDouble(15 * scale), ticks);
        return true;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_HOE;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 0.5;
    }

    @Override
    public int getBurnAbility() {
        return 15;
    }

    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public BlockColor getColor() {
        // TODO Was guessed
        return BlockColor.WHITE_BLOCK_COLOR;
    }
}
