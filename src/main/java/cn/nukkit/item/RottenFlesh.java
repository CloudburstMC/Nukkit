package cn.nukkit.item;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package cn.nukkit.item in project nukkit.
 */
public class RottenFlesh extends EdibleItem {

    public RottenFlesh() {
        this(0, 1);
    }

    public RottenFlesh(Integer meta) {
        this(meta, 1);
    }

    public RottenFlesh(Integer meta, int count) {
        super(ROTTEN_FLESH, meta, count, "Rotten Flesh");
    }

}
