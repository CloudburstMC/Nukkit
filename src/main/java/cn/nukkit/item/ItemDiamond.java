package cn.nukkit.item;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class ItemDiamond extends Item implements ItemTrimMaterial {

    public ItemDiamond() {
        this(0, 1);
    }

    public ItemDiamond(Integer meta) {
        this(meta, 1);
    }

    public ItemDiamond(Integer meta, int count) {
        super(DIAMOND, 0, count, "Diamond");
    }

    @Override
    public ItemTrimMaterial.Type getMaterial() {
        return Type.DIAMOND;
    }
}
