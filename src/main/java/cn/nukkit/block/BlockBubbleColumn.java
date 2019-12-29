package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.math.AxisAlignedBB;

public class BlockBubbleColumn extends BlockTransparentMeta {

    public BlockBubbleColumn() {
        this(0);
    }

    protected BlockBubbleColumn(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BUBBLE_COLUMN;
    }

    @Override
    public String getName() {
        return "Bubble Column";
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
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
    public boolean canBePlaced() {
        return false;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
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
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int onUpdate(int type) {
        Block down = this.down();
        if(!(down instanceof BlockBubbleColumn)) {
            if(this.getDamage() == 0 && !(down instanceof BlockMagma) || this.getDamage() == 1 && !(down instanceof BlockSoulSand)) {
                this.getLevel().setBlock(this, 1, get(AIR), true, false);
                this.getLevel().setBlock(this, get(WATER), true, true);
            }
        } else {
            Block up = this.up();
            if(up instanceof BlockWater || up.getLevelBlockAtLayer(1) instanceof BlockWater && !(up instanceof BlockBubbleColumn)) {
                this.getLevel().setBlock(up, 1, get(WATER), true, false);
                this.getLevel().setBlock(up, get(BUBBLE_COLUMN, this.getDamage()), true, true);
            }
        }
        return 0;
    }

}
