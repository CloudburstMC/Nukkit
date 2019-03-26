package cn.nukkit.block;

/**
 * @author Nukkit Project Team
 */
public class BlockMushroomBrown extends BlockMushroom {

    public BlockMushroomBrown() {
        super();
    }

    public BlockMushroomBrown(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Brown Mushroom";
    }

    @Override
    public int getId() {
        return BROWN_MUSHROOM;
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
