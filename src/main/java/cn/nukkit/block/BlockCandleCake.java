package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

public class BlockCandleCake extends BlockCake {

    public BlockCandleCake() {
        this(0);
    }

    public BlockCandleCake(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Candle Cake";
    }

    @Override
    public int getId() {
        return CANDLE_CAKE;
    }

    @Override
    public boolean canBeActivated() {
        return false; // TODO
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        // TODO
        return false;
    }
}
