package cn.nukkit.block;

public class BlockSlabDeepslatePolished extends BlockSlabDeepslate {

    public BlockSlabDeepslatePolished() {
        this(0);
    }

    public BlockSlabDeepslatePolished(int meta) {
        super(meta, POLISHED_DEEPSLATE_SLAB);
    }

    @Override
    public int getId() {
        return POLISHED_DEEPSLATE_SLAB;
    }

    @Override
    public String getSlabName() {
        return "Polished Deepslate";
    }
}
