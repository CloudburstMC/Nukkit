package cn.nukkit.item;

/**
 * Created by Leonidius20 on 18.08.18.
 */
public class ItemMelonGlistering extends Item {

    public ItemMelonGlistering() {
        this(0, 1);
    }

    public ItemMelonGlistering(Integer meta) {
        this(meta, 1);
    }

    public ItemMelonGlistering(Integer meta, int count) {
        super(GLISTERING_MELON, 0, count, "Glistering Melon");
    }
}
