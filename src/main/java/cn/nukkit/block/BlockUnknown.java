package cn.nukkit.block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockUnknown extends Block {

    public BlockUnknown(int id, int meta) {
        super(id, meta);
    }

    @Override
    public String getName() {
        return "Unknown";
    }
}
