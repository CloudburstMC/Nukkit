package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

public abstract class BlockAmethystBud extends BlockTransparentMeta implements Faceable {

    public BlockAmethystBud() {
        this(0);
    }

    public BlockAmethystBud(int meta) {
        super(meta);
    }

    protected abstract String getSizeName();

    @Override
    public String getName() {
        return this.getSizeName() + " Amethyst Bud";
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (Block.canConnectToFullSolid(this.getSide(face.getOpposite()))) {
            this.setDamage(face.getIndex());
            return this.getLevel().setBlock(this, this, true, true);
        }
        return false;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(this.getDamage());
    }

    public void setBlockFace(BlockFace face) {
        this.setDamage(face.getIndex());
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        switch (this.getBlockFace()) {
            case UP:
                return boundingBox(this.getCrystalOffset(), 0, this.getCrystalOffset(), 16 - this.getCrystalOffset(),
                        this.getCrystalHeight(), 16 - this.getCrystalOffset());
            case DOWN:
                return boundingBox(this.getCrystalOffset(), 16 - this.getCrystalHeight(), this.getCrystalOffset(),
                        16 - this.getCrystalOffset(), 16, 16 - this.getCrystalOffset());
            case NORTH:
                return boundingBox(this.getCrystalOffset(), this.getCrystalOffset(), 16 - this.getCrystalHeight(),
                        16 - this.getCrystalOffset(), 16 - this.getCrystalOffset(), 16);
            case SOUTH:
                return boundingBox(this.getCrystalOffset(), this.getCrystalOffset(), 0,
                        16 - this.getCrystalOffset(), 16 - this.getCrystalOffset(), this.getCrystalHeight());
            case EAST:
                return boundingBox(0, this.getCrystalOffset(), this.getCrystalOffset(),
                        this.getCrystalHeight(), 16 - this.getCrystalOffset(), 16 - this.getCrystalOffset());
            case WEST:
                return boundingBox(16 - this.getCrystalHeight(), this.getCrystalOffset(), this.getCrystalHeight(),
                        16, 16 - this.getCrystalOffset(), 16 - this.getCrystalOffset());
        }
        return super.recalculateBoundingBox();
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 1.5;
    }

    @Override
    public boolean breakWhenPushed() {
        return true;
    }

    protected abstract int getCrystalHeight();
    protected abstract int getCrystalOffset();

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId()), 0, 1);
    }

    protected static AxisAlignedBB boundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return new SimpleAxisAlignedBB(minX / 16.0D, minY / 16.0D, minZ / 16.0D, maxX / 16.0D, maxY / 16.0D, maxZ / 16.0D);
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PURPLE_BLOCK_COLOR;
    }
}
