package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.TerracottaColor;

/**
 * @author xtypr
 * @since 2015/11/24
 */
public class BlockTerracotta extends BlockSolidMeta {
    public BlockTerracotta() {
        this(0);
    }

    public BlockTerracotta(int meta) {
        super(0);
    }

    public BlockTerracotta(TerracottaColor dyeColor) {
        this(dyeColor.getTerracottaData());
    }

    @Override
    public int getId() {
        return TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Terracotta";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 1.25;
    }

    @Override
    public double getResistance() {
        return 7;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public BlockColor getColor() {
        return TerracottaColor.getByTerracottaData(getDamage()).getColor();
    }

    public TerracottaColor getDyeColor() {
        return TerracottaColor.getByTerracottaData(getDamage());
    }
}
