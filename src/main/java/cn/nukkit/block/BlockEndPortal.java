package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.utils.BlockColor;

public class BlockEndPortal extends BlockFlowable {

    public BlockEndPortal() {
        this(0);
    }

    public BlockEndPortal(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "End Portal Block";
    }

    @Override
    public int getId() {
        return END_PORTAL;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public double getResistance() {
        return 18000000;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.AIR));
    }
}
