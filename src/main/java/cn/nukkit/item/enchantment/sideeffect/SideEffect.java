package cn.nukkit.item.enchantment.sideeffect;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PowerNukkitOnly
@Since("1.5.1.0-PN")
public interface SideEffect extends Cloneable {
    SideEffect[] EMPTY_ARRAY = new SideEffect[0];

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    default void doPreHealthChange(@Nonnull Entity entity, @Nonnull EntityDamageEvent source, @Nullable Entity attacker) {
        // Does nothing
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    default void doPostAttack(@Nonnull Entity entity, @Nonnull EntityDamageEvent source, @Nullable Entity attacker) {
        // Does nothing
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Nonnull
    SideEffect clone();
}
