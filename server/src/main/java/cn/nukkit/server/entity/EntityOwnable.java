package cn.nukkit.server.entity;

import cn.nukkit.server.Player;

/**
 * Author: BeYkeRYkt
 * Nukkit Project
 */
public interface EntityOwnable {
    String getOwnerName();

    void setOwnerName(String playerName);

    Player getOwner();
}
