package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

public class BlockFrogspawn extends BlockFlowable {

    public BlockFrogspawn() {
        this(0);
    }

    public BlockFrogspawn(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return FROG_SPAWN;
    }

    @Override
    public String getName() {
        return "Frogspawn";
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public int getDropExp() {
        return 1;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        Block up;
        if (!(block instanceof BlockWater) || !((up = block.up()) instanceof BlockAir)) {
            return false;
        }
        return this.getLevel().setBlock(up, this, true, true);
    }
}
