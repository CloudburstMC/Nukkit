package cn.nukkit.block;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class BlockUnknown extends BlockMeta {

    private final int id;

    public BlockUnknown(int id) {
        this(id, 0);
    }

    public BlockUnknown(int id, Integer meta) {
        super(meta);
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public double getHardness() {
        return 0.1;
    }

    @Override
    public String getName() {
        return "Unknown";
    }
}
