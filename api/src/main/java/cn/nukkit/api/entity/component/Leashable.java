package cn.nukkit.api.entity.component;

import cn.nukkit.api.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public interface Leashable extends EntityComponent {

    void leash(@Nullable Player player);

    void unleash();

    @Nonnull
    Optional<Player> getLeasher();
}
