package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

public class BlockCarvedPumpkin extends BlockPumpkin {

    @Override
    public int getId() {
        return CARVED_PUMPKIN;
    }
    
    @Override
    public String getName() {
        return "Caved Pumpkin";
    }
    
    @Override
    public boolean canBeActivated() {
        return false;
    }
    
    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        return false;
    }
}
