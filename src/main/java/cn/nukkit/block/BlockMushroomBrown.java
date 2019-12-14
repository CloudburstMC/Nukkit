package cn.nukkit.block;

/**
 * @author Nukkit Project Team
 */
public class BlockMushroomBrown extends BlockMushroom {

    public BlockMushroomBrown(int id, int meta) {
        super(id, meta);
    }

    @Override
    public String getName() {
        return "Brown Mushroom";
    }

    @Override
    public int getLightLevel() {
        return 1;
    }

    @Override
    protected int getType() {
        return 0;
    }
}
