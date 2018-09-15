package cn.nukkit.item;

/**
 * Created by Leonidius20 on 18.08.18.
 */
public class ItemGhastTear extends Item {

    public ItemGhastTear() {
        this(0, 1);
    }

    public ItemGhastTear(Integer meta) {
        this(meta, 1);
    }

    public ItemGhastTear(Integer meta, int count) {
        super(GHAST_TEAR, meta, count, "Ghast Tear");
    }

}
