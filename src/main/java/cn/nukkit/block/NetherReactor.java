package cn.nukkit.block;

/**
 * Created on 2015/11/22 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class NetherReactor extends Solid {
    public NetherReactor() {
        this(0);
    }

    public NetherReactor(int meta) {
        super(NETHER_REACTOR, meta);
    }

    @Override
    public String getName(){
        return "Nether Reactor";
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

}
