package cn.nukkit.item.enchantment.sideeffect;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityCombustByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PowerNukkitOnly
@Since("1.5.1.0-PN")
public class SideEffectCombust implements SideEffect {
    private int duration;

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public SideEffectCombust(int duration) {
        this.duration = duration;
    }

    @Since("1.5.1.0-PN")
    @PowerNukkitOnly
    @Override
    public void doPreHealthChange(@Nonnull Entity entity, @Nonnull EntityDamageEvent event, @Nullable Entity attacker) {
        EntityCombustByEntityEvent ev = new EntityCombustByEntityEvent(attacker, entity, duration);
        Server.getInstance().getPluginManager().callEvent(ev);

        if (!ev.isCancelled()) {
            entity.setOnFire(ev.getDuration());
        }
    }

    @Since("1.5.1.0-PN")
    @PowerNukkitOnly
    public int getDuration() {
        return duration;
    }

    @Since("1.5.1.0-PN")
    @PowerNukkitOnly
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Since("1.5.1.0-PN")
    @PowerNukkitOnly
    @SneakyThrows
    @Override
    @Nonnull
    public SideEffect clone() {
        return (SideEffect) super.clone();
    }
}
