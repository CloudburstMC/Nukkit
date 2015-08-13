package cn.nukkit.block;

import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Crop extends Flowable {
    public Crop(int id) {
        super(id);
    }

    public Crop(int id, int meta) {
        super(id, meta);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz) {
        Block down = this.getSide(0);
        if (down.getId() == FARMLAND) {
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean onActivate(Item item) {
        //todo !!!
        return super.onActivate(item);
    }

    @Override
    public int onUpdate(int type) {
        //todo
        return super.onUpdate(type);
    }
}
