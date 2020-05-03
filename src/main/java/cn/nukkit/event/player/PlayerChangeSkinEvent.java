package cn.nukkit.event.player;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.player.Player;
import com.nukkitx.protocol.bedrock.data.SerializedSkin;

/**
 * author: KCodeYT
 * Nukkit Project
 */
public class PlayerChangeSkinEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final SerializedSkin skin;

    public PlayerChangeSkinEvent(Player player, SerializedSkin skin) {
        super(player);
        this.skin = skin;
    }

    public SerializedSkin getSkin() {
        return this.skin;
    }

}
