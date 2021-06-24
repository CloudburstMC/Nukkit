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
@Since("FUTURE")
public class SideEffectCombust implements SideEffect {
    private int duration;

    @PowerNukkitOnly
    @Since("FUTURE")
    public SideEffectCombust(int duration) {
        this.duration = duration;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Override
    public void doPreHealthChange(@Nonnull Entity entity, @Nonnull EntityDamageEvent event, @Nullable Entity attacker) {
        EntityCombustByEntityEvent ev = new EntityCombustByEntityEvent(attacker, entity, duration);
        Server.getInstance().getPluginManager().callEvent(ev);

        if (!ev.isCancelled()) {
            entity.setOnFire(ev.getDuration());
        }
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    public int getDuration() {
        return duration;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @SneakyThrows
    @Override
    @Nonnull
    public SideEffect clone() {
        return (SideEffect) super.clone();
    }
}
