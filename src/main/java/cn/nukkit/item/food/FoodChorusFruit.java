package cn.nukkit.item.food;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.player.Player;

/**
 * Created by Leonidius20 on 20.08.18.
 */
public class FoodChorusFruit extends FoodNormal {

    public FoodChorusFruit() {
        super(4, 2.4F);
        addRelative(Item.CHORUS_FRUIT);
    }

    @Override
    protected boolean onEatenBy(Player player) {
        super.onEatenBy(player);
        // Teleportation
        int minX = player.getFloorX() - 8;
        int minY = player.getFloorY() - 8;
        int minZ = player.getFloorZ() - 8;
        int maxX = minX + 16;
        int maxY = minY + 16;
        int maxZ = minZ + 16;

        Level level = player.getLevel();
        if (level == null) return false;

        for (int attempts = 0; attempts < 16; attempts++) {
            NukkitRandom random = new NukkitRandom(-1);
            int x = random.nextRange(minX, maxX);
            int y = random.nextRange(minY, maxY);
            int z = random.nextRange(minZ, maxZ);

            while (y >= 0 && !level.getBlock(new Vector3(x, y + 1, z)).isSolid()) {
                y--;
            }

            if (y < 0) continue;

            Block blockUp = level.getBlock(new Vector3(x, y + 1, z));
            Block blockUp2 = level.getBlock(new Vector3(x, y + 2, z));

            if (blockUp.isSolid() || blockUp instanceof BlockLiquid || blockUp2.isSolid() || blockUp2 instanceof BlockLiquid) {
                continue;
            }

            // Sounds are broadcasted at both source and destination
            level.addSound(player.asBlockVector3().asVector3(), Sound.MOB_ENDERMEN_PORTAL);
            player.teleport(new Vector3(x + 0.5, y + 1, z + 0.5));
            level.addSound(player.asBlockVector3().asVector3(), Sound.MOB_ENDERMEN_PORTAL);

            break;
        }

        return true;
    }

}