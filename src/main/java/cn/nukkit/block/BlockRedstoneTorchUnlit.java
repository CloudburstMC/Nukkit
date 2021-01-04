package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.RedstoneComponent;

/**
 * @author CreeperFace
 * @since 10.4.2017
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent and uses methods from it.", since = "1.4.0.0-PN")
public class BlockRedstoneTorchUnlit extends BlockTorch implements RedstoneComponent {

    public BlockRedstoneTorchUnlit() {
        this(0);
    }

    public BlockRedstoneTorchUnlit(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Unlit Redstone Torch";
    }

    @Override
    public int getId() {
        return UNLIT_REDSTONE_TORCH;
    }

    @Override
    public int getLightLevel() {
        return 0;
    }

    @Override
    public int getWeakPower(BlockFace side) {
        return 0;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return 0;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.REDSTONE_TORCH));
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
        if (!this.isPoweredFromSide()) {
            this.level.setBlock(getLocation(), Block.get(BlockID.REDSTONE_TORCH, getDamage()), false, true);

            updateAllAroundRedstone(getBlockFace().getOpposite());
            return true;
        }

        return false;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
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
}
