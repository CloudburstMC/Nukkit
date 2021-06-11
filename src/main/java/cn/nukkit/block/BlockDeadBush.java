package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemStick;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * @author xtypr
 * @since 2015/12/2
 */
public class BlockDeadBush extends BlockFlowable {
    public BlockDeadBush() {
        this(0);
    }

    public BlockDeadBush(int meta) {
        // Dead bushes can't have meta. Also stops the server from throwing an exception with the block palette.
        super(0);
    }

    @Override
    public String getName() {
        return "Dead Bush";
    }

    @Override
    public int getId() {
        return DEAD_BUSH;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return CommonBlockProperties.EMPTY_PROPERTIES;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }
    
    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed support logic")
    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        if (isSupportValid()) {
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }
        return false;
    }
    
    private boolean isSupportValid() {
        switch (down().getId()) {
            case SAND:
            case TERRACOTTA:
            case STAINED_TERRACOTTA:
            case DIRT:
            case PODZOL:
                return true;
            default:
                return false;
        }
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed support logic")
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isSupportValid()) {
                this.getLevel().useBreakOn(this);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[]{
                    toItem()
            };
        } else {
            return new Item[]{
                    new ItemStick(0, new Random().nextInt(3))
            };
        }
    }

    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }
}
