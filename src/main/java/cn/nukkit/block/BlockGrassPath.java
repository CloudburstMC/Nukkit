package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

/**
 * @author xtypr
 * @since 2015/11/22
 */

public class BlockGrassPath extends BlockGrass {

    public BlockGrassPath() {
    }

    @Override
    public int getId() {
        return GRASS_PATH;
    }

    @Override
    public String getName() {
        return "Grass Path";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.9375;
    }

    @Override
    public double getResistance() {
        return 3.25;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public int onUpdate(int type) {
        return 0;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        if (item.isHoe()) {
            item.useOn(this);
            this.getLevel().setBlock(this, get(FARMLAND), true);
            if(player != null){
                player.getLevel().addSound(player, Sound.USE_GRASS);
            }
            return true;
        }

        return false;
    }

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isSolid(BlockFace side) {
        return true;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will return true")
    @Override
    public boolean isTransparent() {
        return true;
    }
}
