package cn.nukkit.server.entity.component;

import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.entity.component.Targetable;

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
