package com.nukkitx.server.entity.component;

import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.entity.component.Targetable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class TargetableComponent implements Targetable {
    private Entity target;

    @Nonnull
    @Override
    public Optional<Entity> getTarget() {
        return Optional.ofNullable(target);
    }

    @Override
    public void setTarget(@Nullable Entity target) {
        this.target = target;
    }
}
