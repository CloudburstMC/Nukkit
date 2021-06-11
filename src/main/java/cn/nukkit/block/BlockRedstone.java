package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.RedstoneComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/*
 * @author Pub4Game
 * @since 2015/12/11
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent and uses methods from it.", since = "1.4.0.0-PN")
public class BlockRedstone extends BlockSolidMeta implements RedstoneComponent {

    public BlockRedstone() {
        this(0);
    }

    public BlockRedstone(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return REDSTONE_BLOCK;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return CommonBlockProperties.EMPTY_PROPERTIES;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Redstone Block";
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.REDSTONE_BLOCK_COLOR;
    }

    @Override
    @PowerNukkitOnly
    @PowerNukkitDifference(info = "Update around redstone", since = "1.4.0.0-PN")
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (super.place(item, block, target, face, fx, fy, fz, player)) {
            updateAroundRedstone();

            return true;
        }
        return false;
    }

    @Override
    @PowerNukkitOnly
    @PowerNukkitDifference(info = "Update around redstone", since = "1.4.0.0-PN")
    public boolean onBreak(Item item) {
        if (!super.onBreak(item)) {
            return false;
        }

        updateAroundRedstone();
        return true;
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    @Override
    public int getWeakPower(BlockFace face) {
        return 15;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
