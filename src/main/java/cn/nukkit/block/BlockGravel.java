package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

import java.util.Random;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockGravel extends BlockFallable {

    public BlockGravel(Identifier id) {
        super(id);
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (new Random().nextInt(9) == 0) {
            return new Item[]{
                    Item.get(ItemIds.FLINT)
            };
        } else {
            return new Item[]{
                    toItem()
            };
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

}
