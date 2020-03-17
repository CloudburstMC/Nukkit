package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.FARMLAND;

/**
 * Created on 2015/11/22 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockGrassPath extends BlockGrass {

    public BlockGrassPath(Identifier id) {
        super(id);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public float getMaxY() {
        return this.getY() + 0.9375f;
    }

    @Override
    public float getResistance() {
        return 3.25f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }

    @Override
    public int onUpdate(int type) {
        return 0;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.isHoe()) {
            item.useOn(this);
            this.getLevel().setBlock(this.getPosition(), Block.get(FARMLAND), true);
            return true;
        }

        return false;
    }
}
