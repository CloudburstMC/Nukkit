package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.CreatureSpawnEvent;
import cn.nukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.registry.EntityRegistry;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemSpawnEgg extends Item {

    public ItemSpawnEgg(Identifier id) {
        super(id);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, Vector3f clickPos) {
        Chunk chunk = level.getLoadedChunk(block.getPosition());

        if (chunk == null) {
            return false;
        }

        Location location = Location.from(block.getPosition().toFloat().add(0.5, 0, 0.5),
                ThreadLocalRandom.current().nextFloat() * 360, 0, level);
        CreatureSpawnEvent ev = new CreatureSpawnEvent(EntityRegistry.get().getEntityType(this.getMeta()), // FIXME: 04/01/2020 Use string identifier in NBT
                location, SpawnReason.SPAWN_EGG);
        level.getServer().getPluginManager().callEvent(ev);

        if (ev.isCancelled()) {
            return false;
        }

        Entity entity = EntityRegistry.get().newEntity(ev.getEntityType(), ev.getLocation());
        if (this.hasCustomName()) {
            entity.setNameTag(this.getCustomName());
        }

        if (player.isSurvival()) {
            player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
        }
        entity.spawnToAll();
        return true;
    }
}
