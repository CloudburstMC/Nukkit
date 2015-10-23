package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Painting extends Item {

    public Painting() {
        this(0, 1);
    }

    public Painting(Integer meta) {
        this(meta, 1);
    }

    public Painting(Integer meta, int count) {
        super(PAINTING, 0, count, "Painting");
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
}
