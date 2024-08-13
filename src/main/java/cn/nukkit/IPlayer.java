package cn.nukkit;

import cn.nukkit.metadata.Metadatable;
import cn.nukkit.permission.ServerOperator;

import java.util.UUID;

/**
 * An interface to describe a player and get its information.
 * <p>
 * This player can be online or offline.
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @see cn.nukkit.Player
 * @see cn.nukkit.OfflinePlayer
 */
public interface IPlayer extends ServerOperator, Metadatable {

    /**
     * Returns if this player is online.
     *
     * @return 这个玩家是否在线。<br>If this player is online.
     */
    boolean isOnline();

    /**
     * Returns the name of this player.
     * <p>
     * 如果是在线的玩家，这个函数只会返回登录名字。如果要返回显示的名字，参见{@link cn.nukkit.Player#getDisplayName}<br>
     * Notice that this will only return its login name. If you need its display name, turn to
     * {@link cn.nukkit.Player#getDisplayName}
     *
     * @return 这个玩家的名称。<br>The name of this player.
     */
    String getName();
    
    UUID getUniqueId();

    /**
     * Returns if this player is banned.
     *
     * @return 这个玩家的名称。<br>The name of this player.
     * @see #setBanned
     */
    boolean isBanned();

    /**
     * Sets this player to be banned or to be pardoned.
     *
     * @param value 如果为{@code true}，封禁这个玩家。如果为{@code false}，解封这个玩家。<br>
     *              {@code true} for ban and {@code false} for pardon.
     * @see #isBanned
     */
    void setBanned(boolean value);

    /**
     * Returns if this player is pardoned by whitelist.
     *
     * @return 这个玩家是否已加入白名单。<br>If this player is pardoned by whitelist.
     * @see cn.nukkit.Server#isWhitelisted
     */
    boolean isWhitelisted();

    /**
     * Adds this player to the white list, or removes it from the whitelist.
     *
     * @param value 如果为{@code true}，把玩家加入白名单。如果为{@code false}，取消这个玩家的白名单。<br>
     *              {@code true} for add and {@code false} for remove.
     * @see #isWhitelisted
     * @see cn.nukkit.Server#addWhitelist
     * @see cn.nukkit.Server#removeWhitelist
     */
    void setWhitelisted(boolean value);

    /**
     * Returns a {@code Player} object for this interface.
     *
     * @return 这个接口的 {@code Player}对象。<br>a {@code Player} object for this interface.
     * @see cn.nukkit.Server#getPlayerExact
     */
    Player getPlayer();

    /**
     * Returns the server carrying this player.
     *
     * @return 玩家所在的服务器。<br>the server carrying this player.
     */
    Server getServer();

    /**
     * Returns the time this player first played in this server.
     *
     * @return Unix时间（以秒为单位。<br>Unix time in seconds.
     */
    Long getFirstPlayed();

    /**
     * Returns the time this player last joined in this server.
     *
     * @return Unix时间（以秒为单位。<br>Unix time in seconds.
     */
    Long getLastPlayed();

    /**
     * Returns if this player has played in this server before.
     * <p>
     * If you want to know if this player is the first time playing in this server, you can use:<br>
     * <pre>if (!player.hasPlayerBefore()) {...}</pre>
     *
     * @return 这个玩家以前是不是玩过游戏。<br>If this player has played in this server before.
     */
    boolean hasPlayedBefore();
}
