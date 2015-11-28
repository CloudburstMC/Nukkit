package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class Sponge extends Solid {


    public Sponge() {
        this(0);
    }

    public Sponge(int meta) {
        super(SPONGE, meta);
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public String getName() {
        return "Sponge";
    }
}
