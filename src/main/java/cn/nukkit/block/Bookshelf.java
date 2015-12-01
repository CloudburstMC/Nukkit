package cn.nukkit.block;

import cn.nukkit.item.Tool;

/**
 * @author Nukkit Project Team
 */
public class Bookshelf extends Solid {

    public Bookshelf(int meta) {
        super(meta);
    }

    public Bookshelf() {
        this(0);
    }

    @Override
    public String getName() {
        return "Bookshelf";
    }

    @Override
    public int getId() {
        return Block.BOOKSHELF;
    }

    @Override
    public double getHardness() {
        return 1.5D;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_AXE;
    }

}
