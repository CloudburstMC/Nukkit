package cn.nukkit.entity;

import cn.nukkit.Player;

/**
 * Author: BeYkeRYkt
 * Nukkit Project
 */
public interface EntityOwnable {
    public String getOwnerName();

    public void setOwnerName(String playerName);

    public Player getOwner();
}
