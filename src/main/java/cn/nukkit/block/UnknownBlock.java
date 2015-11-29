package cn.nukkit.block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class UnknownBlock extends Block {

    private int id;

    public UnknownBlock(int id) {
        this(id, 0);
    }

    public UnknownBlock(int id, Integer meta) {
        super(meta);
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return "Unknown";
    }
}
