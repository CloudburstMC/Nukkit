package cn.nukkit.block;

/**
 * Created by Pub4Game on 03.01.2015.
 */
public class BlockMushroomRed extends BlockMushroom {

    public BlockMushroomRed(int id, int meta) {
        super(id, meta);
    }

    @Override
    public String getName() {
        return "Red Mushroom";
    }

    @Override
    protected int getType() {
        return 1;
    }
}
