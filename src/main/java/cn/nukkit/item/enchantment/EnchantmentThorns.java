package cn.nukkit.item.enchantment;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHumanType;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArmor;
import cn.nukkit.item.ItemElytra;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentThorns extends Enchantment {
    protected EnchantmentThorns() {
        super(ID_THORNS, "thorns", Rarity.VERY_RARE, EnchantmentType.ARMOR);
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
        return 3;
    }

    @Override
    public void doPostAttack(Entity attacker, Entity entity) {
        if (!(entity instanceof EntityHumanType)) {
            return;
        }

        EntityHumanType human = (EntityHumanType) entity;

        int thornsLevel = 0;

        for (Item armor : human.getInventory().getArmorContents()) {
            Enchantment thorns = armor.getEnchantment(Enchantment.ID_THORNS);
            if (thorns != null) {
                thornsLevel = Math.max(thorns.getLevel(), thornsLevel);
            }
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();

        if (shouldHit(random, thornsLevel)) {
            attacker.attack(new EntityDamageByEntityEvent(entity, attacker, EntityDamageEvent.DamageCause.ENTITY_ATTACK, getDamage(random, level), 0f));
        }
    }

    @Override
    public boolean canEnchant(@Nonnull Item item) {
        return !(item instanceof ItemElytra) && super.canEnchant(item);
    }

    @Override
    public boolean isItemAcceptable(Item item) {
        if (item instanceof ItemArmor) {
            return !(item instanceof ItemElytra);
        }
        return super.isItemAcceptable(item);
    }

    private static boolean shouldHit(ThreadLocalRandom random, int level) {
        return level > 0 && random.nextFloat() < 0.15 * level;
    }

    private static int getDamage(ThreadLocalRandom random, int level) {
        return level > 10 ? level - 10 : random.nextInt(1, 5);
    }
}
