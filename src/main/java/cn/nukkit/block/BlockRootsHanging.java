package cn.nukkit.block;

public class BlockRootsHanging extends BlockRoots {

    public BlockRootsHanging() {
        this(0);
    }

    public BlockRootsHanging(int meta) {
        super(0); // hanging roots have no variants
    }

    @Override
    protected boolean isSupportValid() {
        Block up = this.up();
        return up.isSolid() && !up.isTransparent();
    }

    @Override
    public String getName() {
        return "Hanging Roots";
    }

    @Override
    public int getId() {
        return HANGING_ROOTS;
    }
}
