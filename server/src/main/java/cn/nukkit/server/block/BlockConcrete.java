package cn.nukkit.server.block;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemTool;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockConcrete extends BlockSolid {

    public BlockConcrete() {
        this(0);
    }

    public BlockConcrete(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CONCRETE;
    }

    @Override
    public double getResistance() {
        return 9;
    }

    @Override
    public double getHardness() {
        return 1.8;
    }

    @Override
    public String getName() {
        return "Concrete";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        return item.getTier() >= ItemTool.TIER_WOODEN ? new Item[]{toItem()} : new Item[0];
    }
}
