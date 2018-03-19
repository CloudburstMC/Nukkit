package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.Metadata;
import com.google.common.base.Preconditions;

public class Cake implements Metadata {
    public static final Cake NEW = new Cake(0);
    public static final Cake ALMOST_EATEN = new Cake(6);
    private final int level;

    private Cake(int level) {
        this.level = level;
    }

    public static Cake of(short data) {
        Preconditions.checkArgument(data >= 0 && data < 8, "data is not valid (wanted 0-7)");
        return new Cake(data);
    }

    public int getSlicesEaten() {
        return level;
    }

    public int getSlicesLeft() {
        return 7 - level;
    }
}
