package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;

import javax.annotation.Nonnull;

/**
 * An entity which can be named by name tags.
 */
@PowerNukkitOnly
public interface EntityNameable {
    @PowerNukkitOnly("The Entity implementations are not PowerNukkit only")
    void setNameTag(String nameTag);

    @PowerNukkitOnly("The Entity implementations are not PowerNukkit only")
    String getNameTag();

    @PowerNukkitOnly("The Entity implementations are not PowerNukkit only")
    void setNameTagVisible(boolean visible);

    @PowerNukkitOnly("The Entity implementations are not PowerNukkit only")
    boolean isNameTagVisible();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    boolean isPersistent();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    void setPersistent(boolean persistent);

    @PowerNukkitOnly("The Entity implementations are not PowerNukkit only")
    default boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (item.getId() == Item.NAME_TAG) {
            if (!player.isSpectator()) {
                return playerApplyNameTag(player, item);
            }
        }
        return false;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default boolean playerApplyNameTag(@Nonnull Player player, @Nonnull Item item) {
        return playerApplyNameTag(player, item, true);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default boolean playerApplyNameTag(@Nonnull Player player, @Nonnull Item item, boolean consume) {
        if (item.hasCustomName()) {
            this.setNameTag(item.getCustomName());
            this.setNameTagVisible(true);
            
            if(consume && !player.isCreative()) {
                player.getInventory().removeItem(item);
            }
            // Set entity as persistent.
            return true;
        }
        return false;
    }

    @PowerNukkitOnly
    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", 
            reason = "New implementation needs a player instance, using this method may allow players to name unexpected entities",
            by = "PowerNukkit", replaceWith = "playerApplyNameTag(Player, Item)")
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
