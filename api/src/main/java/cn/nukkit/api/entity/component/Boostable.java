package cn.nukkit.api.entity.component;

import cn.nukkit.api.Player;

public interface Boostable extends EntityComponent {

    boolean isBoosting();

    void boost(Player player);
}
