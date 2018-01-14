package cn.nukkit.server.entity.component;

import cn.nukkit.api.Player;
import cn.nukkit.api.entity.component.Tameable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class TameableComponent implements Tameable {
    private Player owner;
    private boolean tamed;

    @Override
    public boolean isTamed() {
        return tamed || owner != null;
    }

    @Override
    public void setTamed(boolean tamed) {
        this.tamed = tamed;
    }

    @Nonnull
    @Override
    public Optional<Player> getOwner() {
        return Optional.ofNullable(owner);
    }

    @Override
    public void setOwner(@Nullable Player owner) {
        this.owner = owner;
    }
}
