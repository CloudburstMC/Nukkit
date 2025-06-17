package cn.nukkit.block;

public class BlockBambooButton extends BlockButtonWooden {

    public BlockBambooButton() {
        this(0);
    }

    public BlockBambooButton(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Bamboo Button";
    }

    @Override
    public int getId() {
        return BAMBOO_BUTTON;
    }
}
