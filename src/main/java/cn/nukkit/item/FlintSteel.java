package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.Fire;
import cn.nukkit.block.Solid;
import cn.nukkit.level.Level;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class FlintSteel extends Tool {

    public FlintSteel() {
        this(0, 1);
    }

    public FlintSteel(Integer meta) {
        this(meta, 1);
    }

    public FlintSteel(Integer meta, int count) {
        super(FLINT_STEEL, meta, count, "Flint and Steel");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, int face, double fx, double fy, double fz) {
        if ((player.gamemode & 0x01) == 0 && this.useOn(block) && this.getDamage() >= this.getMaxDurability()) {
            player.getInventory().setItemInHand(new Item(Item.AIR, 0, 0));
        }

        if (block.getId() == AIR && (target instanceof Solid)) {
            level.setBlock(block, new Fire(), true);

            return true;
        }

        return false;
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_FLINT_STEEL;
    }
}
