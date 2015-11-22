package cn.nukkit.item.enchantment;

/**
 * @author Nukkit Project Team
 */
public class EnchantmentList {

    private final EnchantmentEntry[] enchantments;

    public EnchantmentList(int size) {
        this.enchantments = new EnchantmentEntry[size];
    }

    /**
     * @param slot  The index of enchantment.
     * @param entry The given enchantment entry.
     */
    public void setSlot(int slot, EnchantmentEntry entry) {
        enchantments[slot] = entry;
    }

    public EnchantmentEntry getSlot(int slot) {
        return enchantments[slot];
    }

    public int getSize() {
        return enchantments.length;
    }

}
