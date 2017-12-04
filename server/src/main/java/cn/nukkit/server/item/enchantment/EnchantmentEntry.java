package cn.nukkit.server.item.enchantment;

/**
 * @author Nukkit Project Team
 */
public class EnchantmentEntry {

    private final Enchantment[] enchantments;
    private final int cost;
    private final String randomName;

    public EnchantmentEntry(Enchantment[] enchantments, int cost, String randomName) {
        this.enchantments = enchantments;
        this.cost = cost;
        this.randomName = randomName;
    }

    public Enchantment[] getEnchantments() {
        return enchantments;
    }

    public int getCost() {
        return cost;
    }

    public String getRandomName() {
        return randomName;
    }

}
