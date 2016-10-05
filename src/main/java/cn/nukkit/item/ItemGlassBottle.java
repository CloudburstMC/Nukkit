package cn.nukkit.item;


import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

public class ItemGlassBottle extends Item {

    public ItemGlassBottle() {
        this(0, 1);
    }

    public ItemGlassBottle(Integer meta) {
        this(meta, 1);
    }

    public ItemGlassBottle(Integer meta, int count) {
        super(GLASS_BOTTLE, meta, count, "Glass Bottle");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, int face, double fx, double fy, double fz) {
        if (target.getId() == WATER || target.getId() == STILL_WATER) {
            Item potion = new ItemPotion();

            if (this.count == 1) {
                player.getInventory().setItemInHand(potion);
            } else if (this.count > 1) {
                this.count--;
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
