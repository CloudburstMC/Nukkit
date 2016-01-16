package cn.nukkit.item;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package cn.nukkit.item in project nukkit.
 */
public class FishingRod extends Item {

    public FishingRod() {
        this(0, 1);
    }

    public FishingRod(Integer meta) {
        this(meta, 1);
    }

    public FishingRod(Integer meta, int count) {
        super(FISHING_ROD, meta, count, "Fishing Rod");
    }
}
