package cn.nukkit.server.entity.component;

import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.entity.component.Throwable;
import com.google.common.base.Preconditions;

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
