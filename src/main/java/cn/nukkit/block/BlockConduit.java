package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

public class BlockConduit extends BlockSolidMeta {

    public BlockConduit() {
        this(0);
    }

    public BlockConduit(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Conduit";
    }

    @Override
    public int getId() {
        return CONDUIT;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.FLOW_INTO_BLOCK;
    }

    @Override
    public boolean alwaysDropsOnExplosion() {
        return true;
    }
}
