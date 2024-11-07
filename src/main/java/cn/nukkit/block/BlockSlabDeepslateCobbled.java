package cn.nukkit.block;

public class BlockSlabDeepslateCobbled extends BlockSlab {

    public BlockSlabDeepslateCobbled() {
        this(0);
    }
    
    public BlockSlabDeepslateCobbled(int meta) {
        super(meta, COBBLED_DEEPSLATE_DOUBLE_SLAB);
    }

    @Override
    public int getId() {
        return COBBLED_DEEPSLATE_SLAB;
    }

    @Override
    public String getSlabName() {
        return "Cobbled Deepslate";
    }
}
