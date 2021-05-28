package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.network.protocol.AnimatePacket;

public class PlayerAnimationEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final AnimatePacket.Action animationType;
    private final float rowingTime;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public PlayerAnimationEvent(Player player, AnimatePacket animatePacket) {
        this.player = player;
        animationType = animatePacket.action;
        rowingTime = animatePacket.rowingTime;
    }

    public PlayerAnimationEvent(Player player) {
        this(player, AnimatePacket.Action.SWING_ARM);
    }

    public PlayerAnimationEvent(Player player, AnimatePacket.Action animation) {
        this.player = player;
        this.animationType = animation;
        rowingTime = 0;
    }

    public AnimatePacket.Action getAnimationType() {
        return this.animationType;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public float getRowingTime() {
        return rowingTime;
    }
}
