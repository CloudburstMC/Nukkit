package cn.nukkit.block;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class RedstoneDust extends Flowable{

    public RedstoneDust() {
        this(0);
    }

    public RedstoneDust(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Redstone Dust";
    }

    @Override
    public int getId() {
        return Block.REDSTONE_DUST;
    }

}
