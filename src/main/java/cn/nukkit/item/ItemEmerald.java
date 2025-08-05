package cn.nukkit.item;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class ItemEmerald extends Item implements ItemTrimMaterial {

    public ItemEmerald() {
        this(0, 1);
    }

    public ItemEmerald(Integer meta) {
        this(meta, 1);
    }

    public ItemEmerald(Integer meta, int count) {
        super(EMERALD, meta, count, "Emerald");
    }

    @Override
    public ItemTrimMaterial.Type getMaterial() {
        return ItemTrimMaterial.Type.EMERALD;
    }
}
