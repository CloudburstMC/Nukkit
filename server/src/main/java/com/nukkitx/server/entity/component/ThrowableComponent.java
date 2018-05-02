package com.nukkitx.server.entity.component;

import com.google.common.base.Preconditions;
import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.entity.component.Throwable;

import javax.annotation.Nonnull;

public class ThrowableComponent implements Throwable {
    private Entity thrower;

    @Nonnull
    @Override
    public Entity getThrower() {
        return thrower;
    }

    @Override
    public void setThrower(@Nonnull Entity thrower) {
        this.thrower = Preconditions.checkNotNull(thrower, "thrower");
    }
}
