package cn.nukkit.block;

/**
 * Created by Snake1999 on 2016/1/11.
 * Package cn.nukkit.block in project nukkit
 */
public abstract class PressurePlate extends Transparent {

    protected PressurePlate() {
        this(0);
    }

    protected PressurePlate(int meta) {
        super(meta);
    }

    //todo redstone here?
}
