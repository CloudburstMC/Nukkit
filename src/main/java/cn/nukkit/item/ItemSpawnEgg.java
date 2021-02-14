package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.CreatureSpawnEvent;
import cn.nukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.Random;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemSpawnEgg extends Item {
    
    public ItemSpawnEgg() {
        this(0, 1);
    }

    public ItemSpawnEgg(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEgg(Integer meta, int count) {
        super(SPAWN_EGG, meta, count, "Spawn EntityEgg");
    }

    protected ItemSpawnEgg(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (player.isAdventure()) {
            return false;
        }

        FullChunk chunk = level.getChunk((int) block.getX() >> 4, (int) block.getZ() >> 4);

        if (chunk == null) {
            return false;
        }

        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", block.getX() + 0.5))
                        .add(new DoubleTag("", target.getBoundingBox() == null ? block.getY() : target.getBoundingBox().getMaxY() + 0.0001f))
                        .add(new DoubleTag("", block.getZ() + 0.5)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", new Random().nextFloat() * 360))
                        .add(new FloatTag("", 0)));

        if (this.hasCustomName()) {
            nbt.putString("CustomName", this.getCustomName());
        }

        int networkId = getEntityNetworkId();
        CreatureSpawnEvent ev = new CreatureSpawnEvent(networkId, block, nbt, SpawnReason.SPAWN_EGG);
        level.getServer().getPluginManager().callEvent(ev);

        if (ev.isCancelled()) {
            return false;
        }

        Entity entity = Entity.createEntity(networkId, chunk, nbt);

        if (entity != null) {
            if (player.isSurvival()) {
                player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
            }
            entity.spawnToAll();
            return true;
        }

        return false;
    }

    @Since("1.3.2.0-PN")
    @PowerNukkitOnly
    public Item getLegacySpawnEgg() {
        return Item.get(SPAWN_EGG, getEntityNetworkId(), getCount(), getCompoundTag());
    }

    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    public int getEntityNetworkId() {
        return this.meta;
    }
}
