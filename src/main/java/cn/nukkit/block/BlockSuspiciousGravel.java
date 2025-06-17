package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockSuspiciousGravel extends BlockFallableMeta {

    public BlockSuspiciousGravel() {
        this(0);
    }

    public BlockSuspiciousGravel(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SUSPICIOUS_GRAVEL;
    }

    @Override
    public double getHardness() {
        return 0.25;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public String getName() {
        return "Suspicious Gravel";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_BLOCK_COLOR;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{Item.get(AIR)};
    }
}
