package cn.nukkit.api.entity.component;

import cn.nukkit.api.entity.Entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public interface Targetable extends EntityComponent {

    /**
     * Gets the current target of this Creature
     *
     * @return Current target of this creature, or null if none exists
     */
    @Nonnull
    Optional<Entity> getTarget();

    /**
     * Instructs this Creature to set the specified Living as its
     * target.
     * <p>
     * Hostile creatures may attack their target, and friendly creatures may
     * follow their target.
     *
     * @param target New Living to target, or null to clear the target
     */
    void setTarget(@Nullable Entity target);
}
