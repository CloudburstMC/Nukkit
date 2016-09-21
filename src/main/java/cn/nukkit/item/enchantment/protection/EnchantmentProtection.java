package cn.nukkit.item.enchantment.protection;

import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentType;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EnchantmentProtection extends Enchantment {

    public enum TYPE {
        ALL,
        FIRE,
        FALL,
        EXPLOSION,
        PROJECTILE
    }

    protected final TYPE protectionType;

    protected EnchantmentProtection(int id, String name, int weight, EnchantmentProtection.TYPE type) {
        super(id, name, weight, EnchantmentType.ARMOR);
        this.protectionType = type;
        if (protectionType == TYPE.FALL) {
            this.type = EnchantmentType.ARMOR_FEET;
        }
    }

    @Override
    public int getDamageProtection(EntityDamageEvent event) {
        int level = this.level;
        int cause = event.getCause();

        if (level <= 0 || cause == EntityDamageEvent.CAUSE_VOID || cause == EntityDamageEvent.CAUSE_CUSTOM || cause == EntityDamageEvent.CAUSE_MAGIC) {
            return 0;
        }
        /*double f = (6d + level * level) / 3d;
        switch (event.getCause()) {
            case EntityDamageEvent.CAUSE_PROJECTILE:
            case EntityDamageEvent.CAUSE_ENTITY_EXPLOSION:
            case EntityDamageEvent.CAUSE_BLOCK_EXPLOSION:
                return (int) Math.floor(f * 1.5);
            case EntityDamageEvent.CAUSE_FALL:
                return (int) Math.floor(f * 2.5);
            case EntityDamageEvent.CAUSE_FIRE:
            case EntityDamageEvent.CAUSE_FIRE_TICK:
                return (int) Math.floor(f * 1.25);
            default:
                if (this.protectionType == TYPE.ALL) {
                    return (int) Math.floor(f * 0.75);
                } else {
                    return 0;
                }
        }*/

        boolean canProtect = false;

        switch (this.getId()) {
            case Enchantment.ID_PROTECTION_ALL:
                canProtect = true;
                break;
            case Enchantment.ID_PROTECTION_FIRE:
                if (cause == EntityDamageEvent.CAUSE_FIRE || cause == EntityDamageEvent.CAUSE_FIRE_TICK || cause == EntityDamageEvent.CAUSE_LAVA)
                    canProtect = true;
                break;
            case Enchantment.ID_PROTECTION_FALL:
                if (cause == EntityDamageEvent.CAUSE_FALL) canProtect = true;
                break;
            case Enchantment.ID_PROTECTION_EXPLOSION:
                if (cause == EntityDamageEvent.CAUSE_ENTITY_EXPLOSION || cause == EntityDamageEvent.CAUSE_BLOCK_EXPLOSION)
                    canProtect = true;
                break;
            case Enchantment.ID_PROTECTION_PROJECTILE:
                if (cause == EntityDamageEvent.CAUSE_PROJECTILE) canProtect = true;
                break;
        }

        if (canProtect) {
            return (int) (level * getTypeModifier());
        }

        return 0;
    }

    @Override
    public boolean isCompatibleWith(Enchantment enchantment) {
        if (enchantment instanceof EnchantmentProtection) {
            if (((EnchantmentProtection) enchantment).protectionType == this.protectionType) {
                return false;
            }
            return ((EnchantmentProtection) enchantment).protectionType == TYPE.FALL || this.protectionType == TYPE.FALL;
        }
        return super.isCompatibleWith(enchantment);
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public String getName() {
        return "%enchantment.protect." + this.name;
    }

    public double getTypeModifier() {
        return 0;
    }

    @Override
    public boolean isMajor() {
        return true;
    }
}
