package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;

public class BlockAzalea extends BlockTransparent {

    public BlockAzalea() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Azalea";
    }

    @Override
    public int getId() {
        return AZALEA;
    }

    @Override
    public boolean canPlaceOn(Block floor, Position pos) {
        // Azaleas can be placed on grass blocks, dirt, coarse dirt, rooted dirt, podzol, moss blocks, farmland, mud, muddy mangrove roots and clay.
        switch (floor.getId()) {
            case GRASS:
            case DIRT:
            case ROOTED_DIRT:
            case PODZOL:
            case MOSS_BLOCK:
            case FARMLAND:
            case MUD:
            case CLAY_BLOCK:
                return true;
        }
        return false;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_NONE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return true;
    }

    @Override
    public boolean canBeClimbed() {
        return true;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    @Override
    public boolean breakWhenPushed() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }
}
