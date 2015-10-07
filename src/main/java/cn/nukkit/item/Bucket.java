package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Bucket extends Item {

    public Bucket() {
        this(0, 1);
    }

    public Bucket(Integer meta) {
        this(meta, 1);
    }

    public Bucket(Integer meta, int count) {
        super(BUCKET, meta, count, "Bucket");
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
    public boolean onActivate(Level level, Player player, Block block, Block target, double face, double fx, double fy, double fz) {
        //todo
        return super.onActivate(level, player, block, target, face, fx, fy, fz);
    }
}
