package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

/**
 * @author CreeperFace
 */
public class BlockPistonHead extends BlockTransparentMeta {

    public BlockPistonHead() {
        this(0);
    }

    public BlockPistonHead(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PISTON_HEAD;
    }

    @Override
    public String getName() {
        return "Piston Head";
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public boolean onBreak(Item item) {
        this.level.setBlock(this, new BlockAir(), true, true);
        Block piston = getSide(getFacing().getOpposite());

        if (piston instanceof BlockPistonBase && ((BlockPistonBase) piston).getFacing() == this.getFacing()) {
            piston.onBreak(item);
        }
        return true;
    }

    public BlockFace getFacing() {
        return BlockFace.fromIndex(this.getDamage()).getOpposite();
    }

    @Override
    public boolean canBePushed() {
        return false;
    }
}
