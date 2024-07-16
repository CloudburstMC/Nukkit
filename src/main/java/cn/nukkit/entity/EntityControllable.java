package cn.nukkit.entity;

import cn.nukkit.Player;

public interface EntityControllable {

    void onPlayerInput(Player player, double strafe, double forward);

    default void onJump(Player player, int duration) {
    }
}
