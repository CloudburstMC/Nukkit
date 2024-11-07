package cn.nukkit.block;

public class BlockPackedMud extends BlockSolid {

    public BlockPackedMud() {
    }

    @Override
    public String getName() {
        return "Packed Mud";
    }

    @Override
    public int getId() {
        return PACKED_MUD;
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 3;
    }
}
