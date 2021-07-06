package cn.nukkit.item.enchantment;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.enchantment.sideeffect.SideEffect;
import cn.nukkit.item.enchantment.sideeffect.SideEffectCombust;

import javax.annotation.Nonnull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentFireAspect extends Enchantment {
    protected EnchantmentFireAspect() {
        super(ID_FIRE_ASPECT, "fire", Rarity.RARE, EnchantmentType.SWORD);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 10 + (level - 1) * 20;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return super.getMinEnchantAbility(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @PowerNukkitDifference(since = "1.5.1.0-PN", info = "The entity combustion code was moved to SideEffectCombust, obtained by getAttackSideEffects(Entity, Entity)")
    @Override
    public void doPostAttack(Entity attacker, Entity entity) {
        super.doPostAttack(attacker, entity);
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Nonnull
    @Override
    public SideEffect[] getAttackSideEffects(@Nonnull Entity attacker, @Nonnull Entity entity) {
        return new SideEffect[]{
                new SideEffectCombust(Math.max(entity.fireTicks / 20, getLevel() * 4))
        };
    }
}
