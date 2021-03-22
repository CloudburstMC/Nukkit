package cn.nukkit.item.enchantment.trident;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.enchantment.Enchantment;

public class EnchantmentTridentImpaling extends EnchantmentTrident {
    public EnchantmentTridentImpaling() {
        super(Enchantment.ID_TRIDENT_IMPALING, "tridentImpaling", Rarity.RARE);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 8 * level - 7;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }
    
    @Override
    public double getDamageBonus(Entity entity) {
        if (entity.isTouchingWater() || (entity.getLevel().isRaining() && entity.getLevel().canBlockSeeSky(entity))) {
            return 2.5 * getLevel();
        }
        
        return 0;
    }
}
