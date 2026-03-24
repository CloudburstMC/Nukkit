package cn.nukkit.item.enchantment.mace;

public class EnchantmentMaceWindBurst extends EnchantmentMace {

    public EnchantmentMaceWindBurst() {
        super(EnchantmentMace.ID_WIND_BURST, "windburst", Rarity.RARE);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
