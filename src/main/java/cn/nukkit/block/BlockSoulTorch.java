package cn.nukkit.block;

public class BlockSoulTorch extends BlockTorch {
    public BlockSoulTorch() {
        this(0);
    }

    public BlockSoulTorch(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Soul Torch";
    }

    @Override
    public int getId() {
        return SOUL_TORCH;
    }

    @Override
    public int getLightLevel() {
        return 10;
    }
    
}
