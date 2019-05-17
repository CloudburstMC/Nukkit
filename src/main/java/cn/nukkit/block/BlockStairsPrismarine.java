package cn.nukkit.block;

/**
 * Created on 2019/05/16 by joserobjr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockStairsPrismarine extends BlockStairsWood {

    public BlockStairsPrismarine() {
        this(0);
    }

    public BlockStairsPrismarine(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PRISMARINE_STAIRS;
    }

    @Override
    public String getName() {
        return "Prismarine Stairs";
    }

}
