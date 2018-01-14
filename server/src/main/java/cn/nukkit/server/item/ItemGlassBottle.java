package cn.nukkit.server.item;


import cn.nukkit.server.Player;
import cn.nukkit.server.block.Block;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.math.BlockFace;

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
    public boolean onActivate(NukkitLevel level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (target.getId() == WATER || target.getId() == STILL_WATER) {
            Item potion = new ItemPotion();

            if (this.count == 1) {
                player.getInventory().setItemInHand(potion);
            } else if (this.count > 1) {
                this.count--;
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
