package cn.nukkit.item.enchantment.damage;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityArthropod;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentType;
import cn.nukkit.potion.Effect;

import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EnchantmentDamage extends Enchantment {

    public enum TYPE {
        ALL,
        SMITE,
        ARTHROPODS
    }

    protected final TYPE damageType;

    protected EnchantmentDamage(int id, String name, int weight, TYPE type) {
        super(id, name, weight, EnchantmentType.WEAPON);
        this.damageType = type;
    }

    @Override
    public double getDamageBonus(Entity entity) {
        int level = this.level;
        switch (this.damageType) {
            case ARTHROPODS:
                if (entity instanceof EntityArthropod) {
                    return (double) level * 2.5;
                }
            case SMITE:
                if (entity instanceof EntitySmite) {
                    return (double) level * 2.5;
                }
            case ALL:
                return (double) level * 1.25;
        }

        return 0;
    }

    @Override
    public boolean isCompatibleWith(Enchantment enchantment) {
        return !(enchantment instanceof EnchantmentDamage);
    }

    @Override
    public boolean canEnchant(Item item) {
        return item.isAxe() || super.canEnchant(item);
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public void doPostAttack(Entity attacker, Entity entity) {
        if (attacker instanceof EntityLiving) {
            if (this.damageType == TYPE.ARTHROPODS && entity instanceof EntityArthropod) {
                int duration = 20 + ThreadLocalRandom.current().nextInt(10 * this.level);
                entity.addEffect(Effect.getEffect(Effect.SLOWNESS).setDuration(duration).setAmplifier(3));
            }
        }
    }

    @Override
    public String getName() {
        return "%enchantment.damage." + this.name;
    }
}
