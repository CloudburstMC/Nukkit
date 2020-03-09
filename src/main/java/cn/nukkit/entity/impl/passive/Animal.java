package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityAgeable;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.EntityCreature;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.Location;
import cn.nukkit.player.Player;
import com.nukkitx.math.vector.Vector3f;

import static cn.nukkit.item.ItemIds.NAME_TAG;
import static com.nukkitx.protocol.bedrock.data.EntityFlag.BABY;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Animal extends EntityCreature implements EntityAgeable {
    public Animal(EntityType<?> type, Location location) {
        super(type, location);
    }

    @Override
    public boolean isBaby() {
        return this.data.getFlag(BABY);
    }

    public boolean isBreedingItem(Item item) {
        return item.getId() == ItemIds.WHEAT; //default
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
