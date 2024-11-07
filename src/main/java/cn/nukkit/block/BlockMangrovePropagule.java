package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;

public class BlockMangrovePropagule extends BlockSapling {

    public BlockMangrovePropagule() {
        this(0);
    }

    public BlockMangrovePropagule(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MANGROVE_PROPAGULE;
    }

    @Override
    public String getName() {
        return "Mangrove Propagule";
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.FLOW_INTO_BLOCK;
    }
}
