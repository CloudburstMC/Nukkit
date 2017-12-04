package cn.nukkit.server.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemBone extends Item {

    public ItemBone() {
        this(0, 1);
    }

    public ItemBone(Integer meta) {
        this(meta, 1);
    }

    public ItemBone(Integer meta, int count) {
        super(BONE, meta, count, "Bone");
    }
}
