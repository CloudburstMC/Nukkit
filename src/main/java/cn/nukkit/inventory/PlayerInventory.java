package cn.nukkit.inventory;

import cn.nukkit.Player;

import java.util.Collection;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PlayerInventory extends BaseInventory {

    public void sendArmorContents(Player player) {
        this.sendArmorContents(new Player[]{player});
    }

    public void sendArmorContents(Player[] players) {

    }

    public void sendArmorContents(Collection<Player> players) {
        this.sendArmorContents(players.stream().toArray(Player[]::new));
    }
}
