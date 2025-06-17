package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockSuspiciousSand extends BlockFallableMeta {

    public BlockSuspiciousSand() {
        this(0);
    }

    public BlockSuspiciousSand(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SUSPICIOUS_SAND;
    }

    @Override
    public double getHardness() {
        return 0.25;
    }

    @Override
    public double getResistance() {
        return 1.25;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public String getName() {
        return "Suspicious Sand";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{Item.get(AIR)};
    }
}
