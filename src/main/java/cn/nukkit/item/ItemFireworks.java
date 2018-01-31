package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.projectile.FireworkRocket;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.*;

import java.util.Random;

/**
 * author: NycuRO
 * NukkitX Project
 */
public class ItemFireworks extends Item {

    public static final String TAG_FIREWORKS = "Fireworks";
    public static final String TAG_EXPLOSIONS = "Explosions";
    public static final String TAG_FLIGHT = "Flight";

    public float spread = 5.0f;

    public ItemFireworks(Integer meta, int count) {
        super(FIREWORKS, meta, count);
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (this.getNamedTag().getCompound(TAG_FIREWORKS) != null) {
            Random random = new Random();
            int yaw = random.nextInt(360);
            float pitch = -1 * (float) (90 + (random.nextFloat() * this.spread - this.spread / 2));
            CompoundTag nbt = new CompoundTag()
                    .putList(new ListTag<>("Pos")
                            .add(new DoubleTag("", 0.5))
                            .add(new DoubleTag("", 0))
                            .add(new DoubleTag("", 0.5)))
                    .putList(new ListTag<DoubleTag>("Motion")
                            .add(new DoubleTag("", 0))
                            .add(new DoubleTag("", 0))
                            .add(new DoubleTag("", 0)))
                    .putList(new ListTag<FloatTag>("Rotation")
                            .add(new FloatTag("", yaw))
                            .add(new FloatTag("", pitch)))
                    .putCompound(TAG_FIREWORKS, NBTIO.putItemHelper(this));
            level = player.getLevel();
            FireworkRocket fireworkRocket = new FireworkRocket(level.getChunk(player.getFloorX(), player.getFloorZ()), nbt, player);
            level.addEntity(fireworkRocket);
            if (player.isSurvival()) {
                --this.count;
            }
            fireworkRocket.spawnToAll();
        }
        return true;
    }
}
