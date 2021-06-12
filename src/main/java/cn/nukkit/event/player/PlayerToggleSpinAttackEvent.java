package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * @author GoodLucky777
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class PlayerToggleSpinAttackEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static HandlerList getHandlers() {
        return handlers;
    }

    private final boolean isSpinAttacking;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public PlayerToggleSpinAttackEvent(Player player, boolean isSpinAttacking) {
        this.player = player;
        this.isSpinAttacking = isSpinAttacking;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isSpinAttacking() {
        return this.isSpinAttacking;
    }
}
