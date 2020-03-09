package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Identifier;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockConcrete extends BlockSolid {

    public BlockConcrete(Identifier id) {
        super(id);
    }

    @Override
    public float getResistance() {
        return 9;
    }

    @Override
    public float getHardness() {
        return 1.8f;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        return item.getTier() >= ItemTool.TIER_WOODEN ? new Item[]{toItem()} : new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.getByWoolData(getMeta()).getColor();
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByWoolData(getMeta());
    }
}
