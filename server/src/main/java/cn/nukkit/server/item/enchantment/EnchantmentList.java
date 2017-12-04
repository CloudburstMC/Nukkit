package cn.nukkit.server.item.enchantment;

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
    public EnchantmentList setSlot(int slot, EnchantmentEntry entry) {
        enchantments[slot] = entry;
        return this;
    }

    public EnchantmentEntry getSlot(int slot) {
        return enchantments[slot];
    }

    public int getSize() {
        return enchantments.length;
    }

}
