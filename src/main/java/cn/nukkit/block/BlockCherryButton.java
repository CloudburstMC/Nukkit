package cn.nukkit.block;

public class BlockCherryButton extends BlockButtonWooden {

    public BlockCherryButton() {
        this(0);
    }

    public BlockCherryButton(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Cherry Button";
    }

    @Override
    public int getId() {
        return CHERRY_BUTTON;
    }
}
