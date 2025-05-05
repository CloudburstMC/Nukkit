package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockMossCarpet extends BlockTransparent {

    public BlockMossCarpet() {
    }

    @Override
    public int getId() {
        return MOSS_CARPET;
    }

    @Override
    public String getName() {
        return "Moss Carpet";
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    public double getHardness() {
        return 0.1;
    }

    @Override
    public double getResistance() {
        return 0.5;
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.0625;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = this.down();
        if (down.getId() != Item.AIR) {
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!this.down().isSolid()) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GREEN_BLOCK_COLOR;
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }

    @Override
    public boolean breakWhenPushed() {
        return true;
    }
}
