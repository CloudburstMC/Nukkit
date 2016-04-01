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
                level.setBlock(block, new BlockFire(), true);
                int targetX = target.getFloorX();
                int targetY = target.getFloorY();
                int targetZ = target.getFloorZ();
                int x_max = targetX;
                int x_min = targetX;
                int x;
                for (x = targetX + 1; level.getBlock(new Vector3(x, targetY, targetZ)).getId() == OBSIDIAN; x++) {
                    x_max++;
                }
                for (x = targetX - 1; level.getBlock(new Vector3(x, targetY, targetZ)).getId() == OBSIDIAN; x--) {
                    x_min--;
                }
                int count_x = x_max - x_min + 1;
                int z_max = targetZ;
                int z_min = targetZ;
                int z;
                for (z = targetZ + 1; level.getBlock(new Vector3(targetX, targetY, z)).getId() == OBSIDIAN; z++) {
                    z_max++;
                }
                for (z = targetZ - 1; level.getBlock(new Vector3(targetX, targetY, z)).getId() == OBSIDIAN; z--) {
                    z_min--;
                }
                int count_z = z_max - z_min + 1;
                int z_max_y = targetY;
                int z_min_y = targetY;
                int y;
                for (y = targetY; level.getBlock(new Vector3(targetX, y, z_max)).getId() == OBSIDIAN; y++) {
                    z_max_y++;
                }
                for (y = targetY; level.getBlock(new Vector3(targetX, y, z_min)).getId() == OBSIDIAN; y++) {
                    z_min_y++;
                }
                int y_max = Math.min(z_max_y, z_min_y) - 1;
                int count_y = y_max - targetY + 2;
                if ((count_x >= 4 && count_x <= 23 || count_z >= 4 && count_z <= 23) && count_y >= 5 && count_y <= 23) {
                    int count_up = 0;
                    for (int up_z = z_min; level.getBlock(new Vector3(targetX, y_max, up_z)).getId() == OBSIDIAN && up_z <= z_max; up_z++) {
                        count_up++;
                    }
                    if (count_up == count_z) {
                        for (int block_z = z_min + 1; block_z < z_max; block_z++) {
                            for (int block_y = targetY + 1; block_y < y_max; block_y++) {
                                level.setBlock(new Vector3(targetX, block_y, block_z), new BlockNetherPortal());
                            }
                        }
                    }
                }
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
            if ((player.gamemode & 0x01) == 0 && this.useOn(block)) {
                if (this.getDamage() >= this.getMaxDurability()) {
                    player.getInventory().setItemInHand(new Item(Item.AIR, 0, 0));
                } else {
                    this.meta++;
                    player.getInventory().setItemInHand(this);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_FLINT_STEEL;
    }
}
