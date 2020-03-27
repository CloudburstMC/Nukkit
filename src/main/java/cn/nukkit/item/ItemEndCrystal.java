package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBedrock;
import cn.nukkit.block.BlockObsidian;
import cn.nukkit.entity.EntityTypes;
import cn.nukkit.entity.misc.EnderCrystal;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.registry.EntityRegistry;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import java.util.concurrent.ThreadLocalRandom;

public class ItemEndCrystal extends Item {

    public ItemEndCrystal(Identifier id) {
        super(id);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, Vector3f clickPos) {
        if (!(target instanceof BlockBedrock) && !(target instanceof BlockObsidian)) return false;
        Chunk chunk = level.getLoadedChunk(block.getPosition());

        if (chunk == null) {
            return false;
        }

        Vector3f position = block.getPosition().toFloat().add(0.5, 0, 0.5);

        EnderCrystal enderCrystal = EntityRegistry.get().newEntity(EntityTypes.ENDER_CRYSTAL, Location.from(position, level));
        enderCrystal.setRotation(ThreadLocalRandom.current().nextFloat() * 360, 0);
        if (this.hasCustomName()) {
            enderCrystal.setNameTag(this.getCustomName());
        }

        if (player.isSurvival()) {
            Item item = player.getInventory().getItemInHand();
            item.setCount(item.getCount() - 1);
            player.getInventory().setItemInHand(item);
        }
        enderCrystal.spawnToAll();
        return true;
    }
}
