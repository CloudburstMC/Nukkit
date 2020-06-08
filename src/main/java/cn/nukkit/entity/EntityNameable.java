package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;

/**
 * An entity which can be named by name tags.
 */
public interface EntityNameable {
    void setNameTag(String nameTag);
    String getNameTag();

    void setNameTagVisible(boolean visible);
    boolean isNameTagVisible();

    default boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (item.getId() == Item.NAME_TAG) {
            if (!player.isSpectator()) {
                return applyNameTag(item);
            }
        }
        return false;
    }

    default boolean applyNameTag(Item item) {
        if(item.hasCustomName()) {
            this.setNameTag(item.getCustomName());
            this.setNameTagVisible(true);
            return true;
        }
        else {
            return false;
        }
    }
}
