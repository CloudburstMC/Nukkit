package cn.nukkit.server.item.randomitem;

import java.util.Map;

/**
 * Created by Snake1999 on 2016/1/15.
 * Package cn.nukkit.server.item.randomitem in project nukkit.
 */
public class Selector {

    private Selector parent;

    public Selector(Selector parent) {
        this.setParent(parent);
    }

    public Selector setParent(Selector parent) {
        this.parent = parent;
        return parent;
    }

    public Selector getParent() {
        return parent;
    }

    public Object select() {
        return this;
    }

    public static Selector selectRandom(Map<Selector, Float> selectorChanceMap) {
        final float[] totalChance = {0};
        selectorChanceMap.values().forEach(f -> totalChance[0] += f);
        float resultChance = (float) (Math.random() * totalChance[0]);
        final float[] flag = {0};
        final boolean[] found = {false};
        final Selector[] temp = {null};
        selectorChanceMap.forEach((o, f) -> {
            flag[0] += f;
            if (flag[0] > resultChance && !found[0]) {
                temp[0] = o;
                found[0] = true;
            }
        });
        return temp[0];
    }
}
