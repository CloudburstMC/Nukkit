package cn.nukkit.item.enchantment.sideeffect;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PowerNukkitOnly
@Since("FUTURE")
public interface SideEffect extends Cloneable {
    SideEffect[] EMPTY_ARRAY = new SideEffect[0];

    @PowerNukkitOnly
    @Since("FUTURE")
    default void doPreHealthChange(@Nonnull Entity entity, @Nonnull EntityDamageEvent source, @Nullable Entity attacker) {
        // Does nothing
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    default void doPostAttack(@Nonnull Entity entity, @Nonnull EntityDamageEvent source, @Nullable Entity attacker) {
        // Does nothing
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    @Nonnull
    SideEffect clone();
}
