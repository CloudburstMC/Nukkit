package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;

public class ItemHoneyBottle extends ItemEdible {
    
    public ItemHoneyBottle() {
        this(0, 1);
    }
    
    public ItemHoneyBottle(Integer meta) {
        this(meta, 1);
    }
    
    public ItemHoneyBottle(Integer meta, int count) {
        super(HONEY_BOTTLE, meta, count, "Honey Bottle");
    }
    
    @Override
    public int getMaxStackSize() {
        return 16;
    }
    
    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return true;
    }
}
