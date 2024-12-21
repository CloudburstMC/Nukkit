package cn.nukkit.item.enchantment.damage;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityArthropod;
import cn.nukkit.event.entity.EntityPotionEffectEvent;
import cn.nukkit.potion.Effect;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class EnchantmentDamageArthropods extends EnchantmentDamage {

    public EnchantmentDamageArthropods() {
        super(ID_DAMAGE_ARTHROPODS, "arthropods", Rarity.UNCOMMON, TYPE.SMITE);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 5 + ((level - 1) << 3);
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 20;
    }

    @Override
    public double getDamageBonus(Entity entity) {
        if (entity instanceof EntityArthropod) {
            return getLevel() * 2.5;
        }

        return 0;
    }

    @Override
    public void doPostAttack(Entity attacker, Entity entity) {
        if (entity instanceof EntityArthropod) {
            entity.addEffect(
                    Effect.getEffect(Effect.SLOWNESS).setDuration(20 + ThreadLocalRandom.current().nextInt(10 * this.level)).setAmplifier(3),
                    EntityPotionEffectEvent.Cause.ATTACK);
        }
    }
}
