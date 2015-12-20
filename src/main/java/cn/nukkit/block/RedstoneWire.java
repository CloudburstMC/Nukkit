package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.redstone.Redstone;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class RedstoneWire extends Flowable{

    public RedstoneWire() {
        this(0);
    }

    public RedstoneWire(int meta) {
        super(meta);
        this.redEnergyLevel = meta;
    }

    @Override
    public String getName() {
        return "Redstone Wire";
    }

    @Override
    public int getId() {
        return Block.REDSTONE_WIRE;
    }

    @Override
    public void setRedEnergyLevel(int energyLevel) {
        this.redEnergyLevel = energyLevel;
        this.meta = energyLevel;
    }

    @Override
    public int onUpdate(int type) {
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        this.setRedEnergyLevel(this.getSideRedEnergy() - 1);
        block.getLevel().setBlock(block, this, true, true);
        Redstone.updateActive(this);
        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        int level = this.getRedEnergyLevel();
        this.getLevel().setBlock(this, new Air(), true, false);
        Redstone.updateDeactive(this, level);
        return true;
    }

}
