package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

public class BlockNetherWartBlock extends BlockSolid {

    public BlockNetherWartBlock(Identifier id) {
        super(id);
    }

    @Override
    public float getResistance() {
        return 5;
    }

    @Override
    public float getHardness() {
        return 1;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                toItem()
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }
}
