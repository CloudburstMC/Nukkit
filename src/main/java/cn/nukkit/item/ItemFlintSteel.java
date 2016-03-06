package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFire;
import cn.nukkit.block.BlockNetherPortal;
import cn.nukkit.block.BlockSolid;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemFlintSteel extends ItemTool {

    public ItemFlintSteel() {
        this(0, 1);
    }

    public ItemFlintSteel(Integer meta) {
        this(meta, 1);
    }

    public ItemFlintSteel(Integer meta, int count) {
        super(FLINT_STEEL, meta, count, "Flint and Steel");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, int face, double fx, double fy, double fz) {
        if (block.getId() == AIR && (target instanceof BlockSolid)) {
            if (target.getId() == OBSIDIAN) {
                //todo construct the nether portal
                level.setBlock(block, new BlockNetherPortal(), true);
            } else {
                BlockFire fire = new BlockFire();
                fire.x = block.x;
                fire.y = block.y;
                fire.z = block.z;
                fire.level = level;

                if (fire.isBlockTopFacingSurfaceSolid(fire.getSide(Vector3.SIDE_DOWN)) || fire.canNeighborBurn()) {
                    level.setBlock(fire, fire, true);
                    level.scheduleUpdate(fire, fire.tickRate() + level.rand.nextInt(10));
                    return true;
                }
            }
            return true;
        }
        if ((player.gamemode & 0x01) == 0 && this.useOn(block)) {
            if (this.getDamage() >= this.getMaxDurability()) {
                player.getInventory().setItemInHand(new Item(Item.AIR, 0, 0));
            } else {
                this.meta++;
                player.getInventory().setItemInHand(this);
            }
        }
        return false;
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_FLINT_STEEL;
    }
}
