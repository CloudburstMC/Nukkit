package cn.nukkit.entity.hostile;

import cn.nukkit.entity.Creature;
import cn.nukkit.entity.EntityType;
import cn.nukkit.item.Item;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.player.Player;

import static cn.nukkit.item.ItemIds.NAME_TAG;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Mob<T extends Mob<?>> extends Creature<T> {

    public Mob(EntityType<T> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3f clickedPos) {
        if (item.getId() == NAME_TAG) {
            if (item.hasCustomName()) {
                this.setNameTag(item.getCustomName());
                this.setNameTagVisible(true);
                player.getInventory().removeItem(item);
                return true;
            }
        }
        return false;
    }
}
