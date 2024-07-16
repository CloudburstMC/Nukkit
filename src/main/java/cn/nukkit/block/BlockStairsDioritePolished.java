package cn.nukkit.block;

public class BlockStairsDioritePolished extends BlockStairsDiorite {

    public BlockStairsDioritePolished() {
        this(0);
    }

    public BlockStairsDioritePolished(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Polished Diorite Stairs";
    }

    @Override
    public int getId() {
        return POLISHED_DIORITE_STAIRS;
    }
}
