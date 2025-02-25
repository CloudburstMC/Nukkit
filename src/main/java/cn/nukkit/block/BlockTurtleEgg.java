package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;

public class BlockTurtleEgg extends BlockTransparentMeta {

    public BlockTurtleEgg() {
        this(0);
    }

    public BlockTurtleEgg(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Turtle Egg";
    }

    @Override
    public int getId() {
        return TURTLE_EGG;
    }

    @Override
    public double getResistance() {
        return 0.5;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public double getMinX() {
        return this.x + 0.2;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.2;
    }

    @Override
    public double getMaxX() {
        return this.x + 0.8;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.45;
    }

    @Override
    public double getMaxZ() {
        return this.z + 0.8;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!canStayOnFullSolid(this.down())) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (target instanceof BlockTurtleEgg) {
            if (target.getDamage() < 3) {
                this.setDamage(target.getDamage() + 1);
                return this.getLevel().setBlock(target, this, true, true);
            }
            return false;
        }
        if (!canStayOnFullSolid(this.down())) {
            return false;
        }
        this.getLevel().setBlock(this, this, true, true);
        return true;
    }

    @Override
    public boolean breakWhenPushed() {
        return true;
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }
}
