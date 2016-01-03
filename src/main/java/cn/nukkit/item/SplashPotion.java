package cn.nukkit.item;

/**
 * Created on 2015/12/27 by xtypr.
 * Package cn.nukkit.item in project Nukkit .
 */
public class SplashPotion extends Item {

    public SplashPotion(int meta) {
        this(meta, 1);
    }

    public SplashPotion(int meta, int count) {
        super(SPLASH_POTION, meta, count, "Splash Potion");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }
}
