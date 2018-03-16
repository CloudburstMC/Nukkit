package cn.nukkit.api.entity.component;

import cn.nukkit.api.entity.Entity;

import javax.annotation.Nonnull;

public interface Throwable extends EntityComponent {
    @Nonnull
    Entity getThrower();

    void setThrower(@Nonnull Entity thrower);
}
