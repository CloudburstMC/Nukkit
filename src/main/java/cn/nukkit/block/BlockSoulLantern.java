package cn.nukkit.block;

public class BlockSoulLantern extends BlockLantern {

    public BlockSoulLantern() {
        this(0);
    }

    public BlockSoulLantern(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SOUL_LANTERN;
    }

    @Override
    public String getName() {
        return "Soul Lantern";
    }

    @Override
    public int getLightLevel() {
        return 10;
    }

}
