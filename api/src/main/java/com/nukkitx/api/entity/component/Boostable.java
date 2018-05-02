package com.nukkitx.api.entity.component;

import com.nukkitx.api.Player;

public interface Boostable extends EntityComponent {

    boolean isBoosting();

    void boost(Player player);
}
