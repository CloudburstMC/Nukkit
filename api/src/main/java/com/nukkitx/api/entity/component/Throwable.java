package com.nukkitx.api.entity.component;

import com.nukkitx.api.entity.Entity;

import javax.annotation.Nonnull;

public interface Throwable extends EntityComponent {
    @Nonnull
    Entity getThrower();

    void setThrower(@Nonnull Entity thrower);
}
