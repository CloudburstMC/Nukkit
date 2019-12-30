package cn.nukkit.item;


import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.FLOWING_WATER;
import static cn.nukkit.block.BlockIds.WATER;
import static cn.nukkit.item.ItemIds.POTION;

public class ItemGlassBottle extends Item {

    public ItemGlassBottle(Identifier id) {
        super(id);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (target.getId() == WATER || target.getId() == FLOWING_WATER) {
            Item potion = Item.get(POTION);

            if (this.getCount() == 1) {
                player.getInventory().setItemInHand(potion);
            } else if (this.getCount() > 1) {
                this.decrementCount();
                player.getInventory().setItemInHand(this);
                if (player.getInventory().canAddItem(potion)) {
                    player.getInventory().addItem(potion);
                } else {
                    player.getLevel().dropItem(player.add(0, 1.3, 0), potion, player.getDirectionVector().multiply(0.4));
                }
            }
        }
        return false;
    }
}
