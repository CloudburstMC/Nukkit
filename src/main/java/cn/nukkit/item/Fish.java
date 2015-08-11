package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Fish extends Item {

    public Fish() {
        this(0, 1);
    }

    public Fish(int meta) {
        this(meta, 1);
    }

    public Fish(int meta, int count) {
        super(RAW_FISH, meta, count, "Raw Fish");
        if (this.meta == 1) {
            this.name = "Raw Salmon";
        } else if (this.meta == 2) {
            this.name = "Clownfish";
        } else if (this.meta == 3) {
            this.name = "Pufferfish";
        }
    }
}
