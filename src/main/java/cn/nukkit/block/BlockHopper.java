package cn.nukkit.block;

/**
 * @author CreeperFace
 */
public class BlockHopper extends BlockSolid {

    public BlockHopper() {
        this(0);
    }

    public BlockHopper(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return HOPPER_BLOCK;
    }

    @Override
    public String getName() {
        return "Hopper";
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        /*BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (blockEntity instanceof BlockEntityHopper) { TODO: hopper
            return ContainerInventory.calculateRedstone(((BlockEntityHopper) blockEntity).getInventory());
        }*/

        return super.getComparatorInputOverride();
    }
}
