package com.nukkitx.api.entity.component;

import com.nukkitx.api.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public interface Leashable extends EntityComponent {

    void leash(@Nullable Player player);

    void unleash();

    @Nonnull
    Optional<Player> getLeasher();
}
