package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.utils.Identifier;

public class BlockLight extends BlockTransparent {

    public BlockLight(Identifier identifier) {
        super(identifier);
    }

    @Override
    public void setMeta(int meta) {
        super.setMeta(meta & 0xF);
    }

    @Override
    public int getLightLevel() {
        return getMeta() & 0xF;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @Override
    public boolean canWaterlogSource() {
        return true;
    }

    @Override
    public boolean canWaterlogFlowing() {
        return true;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public float getHardness() {
        return 0;
    }

    @Override
    public float getResistance() {
        return 0;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }
}
