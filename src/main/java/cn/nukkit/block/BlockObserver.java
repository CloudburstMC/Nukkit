package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static cn.nukkit.blockproperty.CommonBlockProperties.FACING_DIRECTION;
import static cn.nukkit.blockproperty.CommonBlockProperties.POWERED;

/**
 * @author Leonidius20, joserobjr
 * @since 18.08.18
 */
public class BlockObserver extends BlockSolidMeta implements Faceable {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(FACING_DIRECTION, POWERED);

    public BlockObserver() {
        this(0);
    }

    public BlockObserver(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Observer";
    }

    @Override
    public int getId() {
        return OBSERVER;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (player != null) {
            if (Math.abs(player.getFloorX() - this.x) <= 1 && Math.abs(player.getFloorZ() - this.z) <= 1) {
                double y = player.y + player.getEyeHeight();
                if (y - this.y > 2) {
                    setBlockFace(BlockFace.DOWN);
                } else if (this.y - y > 0) {
                    setBlockFace(BlockFace.UP);
                } else {
                    setBlockFace(player.getHorizontalFacing());
                }
            } else {
                setBlockFace(player.getHorizontalFacing());
            }
        }
        
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implemented")
    @Override
    public boolean isPowerSource() {
        return true;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implemented")
    @Override
    public int getStrongPower(BlockFace side) {
        return isPowered() && side == getBlockFace()? 15 : 0;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implemented")
    @Override
    public int getWeakPower(BlockFace face) {
        return getStrongPower(face);
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implemented")
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
            PluginManager pluginManager = level.getServer().getPluginManager();
            pluginManager.callEvent(ev);
            if (ev.isCancelled()) {
                return 0;
            }

            pluginManager.callEvent(new BlockRedstoneEvent(this, 15, 0));
            setPowered(false);
            level.setBlock(this, this);
            getSide(getBlockFace().getOpposite()).onUpdate(Level.BLOCK_UPDATE_REDSTONE);
            return Level.BLOCK_UPDATE_SCHEDULED;
        } else if (type == Level.BLOCK_UPDATE_MOVED) {
            onNeighborChange(getBlockFace());
            return type;
        }
        return 0;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void onNeighborChange(@Nonnull BlockFace side) {
        Server server = level.getServer();
        BlockFace blockFace = getBlockFace();
        if (!server.isRedstoneEnabled() || isPowered() || side != blockFace) {
            return;
        }
        RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
        server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }

        server.getPluginManager().callEvent(new BlockRedstoneEvent(this, 0, 15));
        setPowered(true);
        if (level.setBlock(this, this)) {
            level.scheduleUpdate(this, 3);
            getSide(blockFace.getOpposite()).onUpdate(Level.BLOCK_UPDATE_REDSTONE);
        }
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public double getHardness() {
        return 3.5;
    }

    @Override
    public double getResistance() {
        return 17.5;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isPowered() {
        return getBooleanValue(POWERED);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setPowered(boolean powered) {
        setBooleanValue(POWERED, powered);
    }
    
    @Override
    public BlockFace getBlockFace() {
        return getPropertyValue(FACING_DIRECTION);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(FACING_DIRECTION, face);
    }
}
