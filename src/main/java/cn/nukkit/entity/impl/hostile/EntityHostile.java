package cn.nukkit.entity.impl.hostile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.EntityCreature;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.player.Player;
import com.nukkitx.math.vector.Vector3f;

import static cn.nukkit.item.ItemIds.NAME_TAG;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityHostile extends EntityCreature {

    public EntityHostile(EntityType<?> type, Location location) {
        super(type, location);
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
