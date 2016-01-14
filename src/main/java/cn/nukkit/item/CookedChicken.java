package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CookedChicken extends EdibleItem {

    public CookedChicken() {
        this(0, 1);
    }

    public CookedChicken(Integer meta) {
        this(meta, 1);
    }

    public CookedChicken(Integer meta, int count) {
        super(COOKED_CHICKEN, meta, count, "Cooked Chicken");
    }
}
