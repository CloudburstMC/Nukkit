package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Sugarcane extends Item {

    public Sugarcane() {
        this(0, 1);
    }

    public Sugarcane(Integer meta) {
        this(meta, 1);
    }

    public Sugarcane(Integer meta, int count) {
        super(SUGARCANE, 0, count, "Sugar Cane");
        this.block = new cn.nukkit.block.Sugarcane();
    }
}
