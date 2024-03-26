package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.utils.BlockColor;

public class BlockTrapdoorAcacia extends BlockTrapdoor {

    public BlockTrapdoorAcacia() {
        this(0);
    }

    public BlockTrapdoorAcacia(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Acacia Trapdoor";
    }

    @Override
    public int getId() {
        return ACACIA_TRAPDOOR;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }
}
