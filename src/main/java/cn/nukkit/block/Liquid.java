package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Liquid extends Trasparent {
    private Vector3 temporalVector = null;

    public Liquid(int id) {
        super(id);
    }

    public Liquid(int id, int meta) {
        super(id, meta);
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public boolean isBreakable(Item item) {
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

    //todo !
}
