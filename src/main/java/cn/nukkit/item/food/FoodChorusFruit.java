package cn.nukkit.item.food;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.player.Player;
import com.nukkitx.math.vector.Vector3f;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Leonidius20 on 20.08.18.
 */
public class FoodChorusFruit extends FoodNormal {

    public FoodChorusFruit() {
        super(4, 2.4F);
        addRelative(ItemIds.CHORUS_FRUIT);
    }

    @Override
    protected boolean onEatenBy(Player player) {
        super.onEatenBy(player);
        // Teleportation
        int minX = player.getPosition().getFloorX() - 8;
        int minY = player.getPosition().getFloorY() - 8;
        int minZ = player.getPosition().getFloorZ() - 8;
        int maxX = minX + 16;
        int maxY = minY + 16;
        int maxZ = minZ + 16;

        Level level = player.getLevel();
        if (level == null) return false;

        for (int attempts = 0; attempts < 128; attempts++) {
            int x = ThreadLocalRandom.current().nextInt(minX, maxX);
            int y = ThreadLocalRandom.current().nextInt(minY, maxY);
            int z = ThreadLocalRandom.current().nextInt(minZ, maxZ);

            if (y < 0) continue;

            while (y >= 0 && !level.getBlock(x, y + 1, z).isSolid()) {
                y--;
            }
            y++; // Back up to non solid

            Block blockUp = level.getBlock(x, y + 1, z);
            Block blockUp2 = level.getBlock(x, y + 2, z);

            if (blockUp.isSolid() || blockUp instanceof BlockLiquid ||
                    blockUp2.isSolid() || blockUp2 instanceof BlockLiquid) {
                continue;
            }

            // Sounds are broadcast at both source and destination
            level.addSound(player.getPosition(), Sound.MOB_ENDERMEN_PORTAL);
            player.teleport(Vector3f.from(x + 0.5, y + 1, z + 0.5), PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT);
            level.addSound(player.getPosition(), Sound.MOB_ENDERMEN_PORTAL);

            break;
        }

        return true;
    }

}