package cn.nukkit.block;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockDandelion extends BlockFlower {
    public BlockDandelion(int id, int meta) {
        super(id, meta);
    }

    @Override
    public String getName() {
        return "Dandelion";
    }

    @Override
    protected Block getUncommonFlower() {
        return get(POPPY);
    }
}
