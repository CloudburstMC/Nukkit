package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Cake extends Item {

    public Cake() {
        this(0, 1);
    }

    public Cake(Integer meta) {
        this(meta, 1);
    }

    public Cake(Integer meta, int count) {
        super(CAKE, 0, count, "Cake");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, int face, double fx, double fy, double fz) {
        level.setBlock(block, Block.get(Block.CAKE_BLOCK), true);
        return true;
    }

}
