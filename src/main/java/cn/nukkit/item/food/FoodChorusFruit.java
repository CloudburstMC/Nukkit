package cn.nukkit.item.food;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.Utils;

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
        int minX = player.getFloorX() - 8;
        int minY = player.getFloorY() - 8;
        int minZ = player.getFloorZ() - 8;

        int maxX = minX + 16;
        int maxY;
        int maxZ = minZ + 16;

        if (player.getLevel().getDimension() == Level.DIMENSION_NETHER) {
            maxY = Math.min(minY + 16, 125);

            if (minY > maxY) {
                minY = maxY;
            }
        } else {
            maxY = minY + 16;
        }

        int minBlockY = player.getLevel().getMinBlockY();

        Level level = player.getLevel();
        if (level == null) return false;

        for (int attempts = 0; attempts < 16; attempts++) {
            int x = Utils.rand(minX, maxX);
            int y = Utils.rand(minY, maxY);
            int z = Utils.rand(minZ, maxZ);

            if (y < minBlockY) continue;

            FullChunk chunk = level.getChunkIfLoaded(x >> 4, z >> 4);
            if (chunk == null) {
                continue;
            }

            while (y >= minBlockY && !level.getBlock(chunk, x, y + 1, z, true).isSolid()) {
                y--;
            }

            y++;

            Block blockUp = level.getBlock(chunk, x, y + 1, z, true);
            Block blockUp2 = level.getBlock(chunk, x, y + 2, z, true);

            if (blockUp.isSolid() || blockUp instanceof BlockLiquid || blockUp2.isSolid() || blockUp2 instanceof BlockLiquid) {
                continue;
            }

            level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_TELEPORT);
            if (player.teleport(new Vector3(x + 0.5, y + 1, z + 0.5), PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT)) {
                level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_TELEPORT);
            }
            break;
        }

        return true;
    }
}
