package cn.nukkit.item;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package cn.nukkit.item in project nukkit.
 */
public class CookedRabbit extends EdibleItem {

    public CookedRabbit() {
        this(0, 1);
    }

    public CookedRabbit(Integer meta) {
        this(meta, 1);
    }

    public CookedRabbit(Integer meta, int count) {
        super(COOKED_RABBIT, meta, count, "Cooked Rabbit");
    }
}
