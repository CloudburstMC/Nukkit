package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;

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

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return true;
    }
}
