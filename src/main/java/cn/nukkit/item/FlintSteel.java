package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class FlintSteel extends Tool {

    public FlintSteel() {
        this(0, 1);
    }

    public FlintSteel(Integer meta) {
        this(meta, 1);
    }

    public FlintSteel(Integer meta, int count) {
        super(FLINT_STEEL, meta, count, "Flint and Steel");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, double face, double fx, double fy, double fz) {
        //todo
        return super.onActivate(level, player, block, target, face, fx, fy, fz);
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_FLINT_STEEL;
    }
}
