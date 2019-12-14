package cn.nukkit.block;

/**
 * Created on 2015/12/8 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockPumpkinLit extends BlockPumpkin {
    public BlockPumpkinLit(int id, int meta) {
        super(id, meta);
    }

    @Override
    public String getName() {
        return "Jack o'Lantern";
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

}
