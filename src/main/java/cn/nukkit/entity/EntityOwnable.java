package cn.nukkit.entity;

import cn.nukkit.player.Player;

/**
 * Author: BeYkeRYkt
 * Nukkit Project
 */
public interface EntityOwnable {
    String getOwnerName();

    void setOwnerName(String playerName);

    Player getOwner();
}
