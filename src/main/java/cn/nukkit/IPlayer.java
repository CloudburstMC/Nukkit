package cn.nukkit;

import cn.nukkit.metadata.Metadatable;
import cn.nukkit.permission.ServerOperator;

/**
 * 用来描述一个玩家和获得这个玩家相应信息的接口。<br>
 * An interface to describe a player and get its information.
 * <p>
 * <p>这个玩家可以在线，也可以是不在线。<br>
 * This player can be online or offline.</p>
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @see cn.nukkit.Player
 * @see cn.nukkit.OfflinePlayer
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public interface IPlayer extends ServerOperator, Metadatable {

    /**
     * 返回这个玩家是否在线。<br>
     * Returns if this player is online.
     *
     * @return 这个玩家是否在线。<br>If this player is online.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean isOnline();

    /**
     * 返回这个玩家的名称。<br>
     * Returns the name of this player.
     * <p>
     * <p>如果是在线的玩家，这个函数只会返回登录名字。如果要返回显示的名字，参见{@link cn.nukkit.Player#getDisplayName}<br>
     * Notice that this will only return its login name. If you need its display name, turn to
     * {@link cn.nukkit.Player#getDisplayName}</p>
     *
     * @return 这个玩家的名称。<br>The name of this player.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    String getName();

    /**
     * 返回这个玩家是否被封禁(ban)。<br>
     * Returns if this player is banned.
     *
     * @return 这个玩家的名称。<br>The name of this player.
     * @see #setBanned
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean isBanned();

    /**
     * 设置这个玩家是否被封禁(ban)。<br>
     * Sets this player to be banned or to be pardoned.
     *
     * @param value 如果为{@code true}，封禁这个玩家。如果为{@code false}，解封这个玩家。<br>
     *              {@code true} for ban and {@code false} for pardon.
     * @see #isBanned
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void setBanned(boolean value);

    /**
     * 返回这个玩家是否已加入白名单。<br>
     * Returns if this player is pardoned by whitelist.
     *
     * @return 这个玩家是否已加入白名单。<br>If this player is pardoned by whitelist.
     * @see cn.nukkit.Server#isWhitelisted
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean isWhitelisted();

    /**
     * 把这个玩家加入白名单，或者取消这个玩家的白名单。<br>
     * Adds this player to the white list, or removes it from the whitelist.
     *
     * @param value 如果为{@code true}，把玩家加入白名单。如果为{@code false}，取消这个玩家的白名单。<br>
     *              {@code true} for add and {@code false} for remove.
     * @see #isWhitelisted
     * @see cn.nukkit.Server#addWhitelist
     * @see cn.nukkit.Server#removeWhitelist
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void setWhitelisted(boolean value);

    /**
     * 得到这个接口的{@code Player}对象。<br>
     * Returns a {@code Player} object for this interface.
     *
     * @return 这个接口的 {@code Player}对象。<br>a {@code Player} object for this interface.
     * @see cn.nukkit.Server#getPlayerExact
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    Player getPlayer();

    /**
     * 返回玩家所在的服务器。<br>
     * Returns the server carrying this player.
     *
     * @return 玩家所在的服务器。<br>the server carrying this player.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    Server getServer();

    /**
     * 得到这个玩家第一次游戏的时间。<br>
     * Returns the time this player first played in this server.
     *
     * @return 这个玩家第一次游戏的时间。<br>The time this player first played in this server.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    Long getFirstPlayed();

    /**
     * 得到这个玩家上次加入游戏的时间。<br>
     * Returns the time this player last joined in this server.
     *
     * @return 这个玩家上次游戏的时间。<br>The time this player last joined in this server.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    Long getLastPlayed();

    /**
     * 返回这个玩家以前是否来过服务器。<br>
     * Returns if this player has played in this server before.
     * <p>
     * <p>如果想得到这个玩家是不是第一次玩，可以使用：<br>
     * If you want to know if this player is the first time playing in this server, you can use:<br>
     * <pre>if(!player.hasPlayerBefore()) {...}</pre></p>
     *
     * @return 这个玩家以前是不是玩过游戏。<br>If this player has played in this server before.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean hasPlayedBefore();

}
