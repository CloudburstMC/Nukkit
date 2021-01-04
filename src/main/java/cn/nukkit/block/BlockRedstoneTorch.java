package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.RedstoneComponent;

import javax.annotation.Nonnull;

/**
 * @author Angelic47 (Nukkit Project)
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent and uses methods from it.", since = "1.4.0.0-PN")
public class BlockRedstoneTorch extends BlockTorch implements RedstoneComponent {

    public BlockRedstoneTorch() {
        this(0);
    }

    public BlockRedstoneTorch(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Redstone Torch";
    }

    @Override
    public int getId() {
        return REDSTONE_TORCH;
    }

    @Override
    public int getLightLevel() {
        return 7;
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }

        if (this.level.getServer().isRedstoneEnabled()) {
            if (!checkState()) {
                updateAllAroundRedstone(getBlockFace().getOpposite());
            }

            checkState();
        }

        return true;
    }

    @Override
    public int getWeakPower(BlockFace side) {
        return getBlockFace() != side ? 15 : 0;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return side == BlockFace.DOWN ? this.getWeakPower(side) : 0;
    }

    @Override
    public boolean onBreak(Item item) {
        if (!super.onBreak(item)) {
            return false;
        }

        if (this.level.getServer().isRedstoneEnabled()) {
            updateAllAroundRedstone(getBlockFace().getOpposite());
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (super.onUpdate(type) == 0) {
            if (!this.level.getServer().isRedstoneEnabled()) {
                return 0;
            }

            if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
                this.level.scheduleUpdate(this, tickRate());
            } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
                RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
                getLevel().getServer().getPluginManager().callEvent(ev);

                if (ev.isCancelled()) {
                    return 0;
                }

                if (checkState()) {
                    return 1;
                }
            }
        }

        return 0;
    }

    private boolean checkState() {
        if (isPoweredFromSide()) {
            this.level.setBlock(getLocation(), Block.get(BlockID.UNLIT_REDSTONE_TORCH, getDamage()), false, true);

            updateAllAroundRedstone(getBlockFace().getOpposite());
            return true;
        }

        return false;
    }

    @PowerNukkitDifference(info = "Check if the side block is piston and if piston is getting power.",
            since = "1.4.0.0-PN")
    protected boolean isPoweredFromSide() {
        BlockFace face = getBlockFace().getOpposite();
        if (this.getSide(face) instanceof BlockPistonBase && this.getSide(face).isGettingPower()) {
            return true;
        }

        return this.level.isSidePowered(this.getLocation().getSide(face), face);
    }

    @Override
    public int tickRate() {
        return 2;
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }
}
