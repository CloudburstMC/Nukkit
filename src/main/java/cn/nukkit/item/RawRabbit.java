package cn.nukkit.item;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package cn.nukkit.item in project nukkit.
 */
public class RawRabbit extends EdibleItem {
    public RawRabbit() {
        this(0, 1);
    }

    public RawRabbit(Integer meta) {
        this(meta, 1);
    }

    public RawRabbit(Integer meta, int count) {
        super(RAW_RABBIT, meta, count, "Raw Rabbit");
    }

}
