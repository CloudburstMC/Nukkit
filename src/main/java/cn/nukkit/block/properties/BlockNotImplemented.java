package cn.nukkit.block.properties;

import cn.nukkit.block.BlockMeta;

public class BlockNotImplemented extends BlockMeta {

    private final int id;

    public BlockNotImplemented(int id) {
        this(id, 0);
    }

    public BlockNotImplemented(int id, int meta) {
        super(meta);
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public double getHardness() {
        return 0.1;
    }

    @Override
    public String getName() {
        return "Not Implemented";
    }
}
