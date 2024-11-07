package cn.nukkit.block;

public class BlockDoubleSlabDeepslateCobbled extends BlockDoubleSlabBase {
    
    public BlockDoubleSlabDeepslateCobbled() {
        this(0);
    }

    public BlockDoubleSlabDeepslateCobbled(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return COBBLED_DEEPSLATE_DOUBLE_SLAB;
    }

    @Override
    public String getSlabName() {
        return "Cobbled Deepslate";
    }

    @Override
    public int getSingleSlabId() {
        return COBBLED_DEEPSLATE_SLAB;
    }

    @Override
    public int getItemDamage() {
        return 0;
    }
}
