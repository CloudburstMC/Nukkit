package cn.nukkit;

import cn.nukkit.permission.ServerOperator;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract interface IPlayer extends ServerOperator {

    public abstract boolean isOnline();

    public abstract String getName();

    public abstract boolean isBanned();

    public abstract void setBanned(boolean banned);

    public abstract boolean isWhitelisted();

    public abstract void setWhitelisted(boolean value);

    public abstract Player getPlayer();

    public abstract double getFirstPlayered();

    public abstract double getLastPlayed();

    public Object hasPlayedBefore();

}
