package cn.nukkit.item.food;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * @author Leonidius20
 * @since 20.08.18
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

        NukkitRandom random = new NukkitRandom();
        for (int attempts = 0; attempts < 128; attempts++) {
            int x = random.nextRange(minX, maxX);
            int y = random.nextRange(minY, maxY);
            int z = random.nextRange(minZ, maxZ);

            if (y < 0) continue;

            while (y >= 0 && !level.getBlock(new Vector3(x, y + 1, z)).isSolid()) {
                y--;
            }
            y++; // Back up to non solid

            Block blockUp = level.getBlock(new Vector3(x, y + 1, z));
            Block blockUp2 = level.getBlock(new Vector3(x, y + 2, z));

            if (blockUp.isSolid() || blockUp instanceof BlockLiquid ||
                    blockUp2.isSolid() || blockUp2 instanceof BlockLiquid) {
                continue;
            }

            // Sounds are broadcast at both source and destination
            level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_TELEPORT);
            player.teleport(new Vector3(x + 0.5, y + 1, z + 0.5), PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT);
            level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_TELEPORT);

            break;
        }

        return true;
    }

}
