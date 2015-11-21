package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Bone extends Item {

    public Bone() {
        this(0, 1);
    }

    public Bone(Integer meta) {
        this(meta, 1);
    }

    public Bone(Integer meta, int count) {
        super(BONE, meta, count, "Bone");
    }
}
