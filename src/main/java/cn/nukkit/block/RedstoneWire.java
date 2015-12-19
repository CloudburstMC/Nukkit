package cn.nukkit.block;

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

}
