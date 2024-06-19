package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockCobweb extends BlockFlowable {

    public BlockCobweb() {
        this(0);
    }

    public BlockCobweb(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Cobweb";
    }

    @Override
    public int getId() {
        return COBWEB;
    }

    @Override
    public double getHardness() {
        return 4;
    }

    @Override
    public double getResistance() {
        return 20;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SWORD;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.resetFallDistance();
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[]{
                    this.toItem()
            };
        } else if (item.isSword()) {
            return new Item[]{
                    Item.get(Item.STRING)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CLOTH_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }

    @Override
    public boolean breakWhenPushed() {
        return true;
    }
}
