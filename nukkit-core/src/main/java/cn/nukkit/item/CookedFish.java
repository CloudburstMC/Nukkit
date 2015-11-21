package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CookedFish extends Item {

    public CookedFish() {
        this(0, 1);
    }

    public CookedFish(Integer meta) {
        this(meta, 1);
    }

    public CookedFish(Integer meta, int count) {
        super(COOKED_FISH, meta, count, "Cooked Fish");
        if (this.meta == 1) {
            this.name = "Cooked Salmon";
        }
    }
}
