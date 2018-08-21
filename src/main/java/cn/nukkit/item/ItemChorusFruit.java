package cn.nukkit.item;

/**
 * Created by Leonidius20 on 20.08.18.
 */
public class ItemChorusFruit extends ItemEdible {

    public ItemChorusFruit() {
        this(0, 1);
    }

    public ItemChorusFruit(Integer meta) {
        this(meta, 1);
    }

    public ItemChorusFruit(Integer meta, int count) {
        super(CHORUS_FRUIT, meta, count, "Chorus Fruit");
    }

}