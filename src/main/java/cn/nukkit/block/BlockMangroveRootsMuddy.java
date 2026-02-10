package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockMangroveRootsMuddy extends BlockSolid {

    @Override
    public String getName() {
        return "Muddy Mangrove Roots";
    }

    @Override
    public int getId() {
        return MUDDY_MANGROVE_ROOTS;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.setPillarAxis(face.getAxis());
        return this.getLevel().setBlock(block, this, true, true);
    }

    public void setPillarAxis(BlockFace.Axis axis) {
        switch (axis) {
            case Y:
                this.setDamage(0);
                break;
            case X:
                this.setDamage(1);
                break;
            case Z:
                this.setDamage(2);
                break;
        }
    }

    public BlockFace.Axis getPillarAxis() {
        switch (this.getDamage() % 3) {
            case 2:
                return BlockFace.Axis.Z;
            case 1:
                return BlockFace.Axis.X;
            case 0:
            default:
                return BlockFace.Axis.Y;
        }
    }

    @Override
    public double getHardness() {
        return 0.7;
    }

    @Override
    public double getResistance() {
        return 0.7;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SPRUCE_BLOCK_COLOR;
    }
}
