package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Armor;
import cn.nukkit.item.Dye;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.ContainerSetDataPacket;

import java.util.HashSet;
import java.util.Random;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantInventory extends ContainerInventory {

    private final Random random = new Random();

    private final int[] enchLevelCosts = new int[3];
    private final int[] enchId = new int[3];
    private final int[] enchLevel = new int[3];

    private int xpSeed;

    public EnchantInventory(Position position) {
        super(null, InventoryType.get(InventoryType.ENCHANT_TABLE));
        this.holder = new FakeBlockMenu(this, position);
    }

    @Override
    public FakeBlockMenu getHolder() {
        return (FakeBlockMenu) this.holder;
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);

        for (int i = 0; i < 2; ++i) {
            this.getHolder().getLevel().dropItem(this.getHolder().add(0.5, 0.5, 0.5), this.getItem(i));
            this.clear(i);
        }
    }

    @Override
    public void onSlotChange(int index, Item before) {
        super.onSlotChange(index, before);

        this.checkItems();
    }

    public void checkItems() {
        Item tool = this.getItem(0);
        Item lapis = this.getItem(1);

        if (((tool.isTool() && !tool.hasEnchantments()) || tool.isArmor() || tool.getId() == Item.BOOK) && lapis.getId() == Item.DYE && lapis.getDamage() == Dye.BLUE && lapis.getCount() > 0) {
            this.calculateNewEnchantsAndLevels();
        }
    }

    public void calculateNewEnchantsAndLevels() {
        int bookshelfCount = 15; //TODO: check for bookshelfs

        for (int i = 0; i < enchLevelCosts.length; i++) {
            this.enchLevelCosts[i] = calculateLevelCost(i, bookshelfCount);
            this.enchLevel[i] = 0;
            this.enchId[i] = 0;
        }

        this.update();
    }

    private int calculateLevelCost(int stage, int countBookshelf) {
        int modifier = calculateModifier(this.getItem(0));
        if (modifier <= 0) return 0;

        int rand = random.nextInt(8) + random.nextInt(countBookshelf + 1);
        rand += 1;
        rand += countBookshelf / 2;

        int result;
        if (stage == 0) {
            result = Math.max(rand / 3, 1);
        } else if (stage == 1) {
            result = rand * 2 / 3 + 1;
        } else {
            result = Math.max(rand, countBookshelf * 2);
        }

        if (result < stage + 1)
            return 0;
        else
            return result;
    }

    private static int calculateModifier(Item item) {

        switch (item.getId()) {
            case Item.BOOK:
            case Item.BOW:
            case Item.FISHING_ROD:
                return 1;
        }

        if (item.isArmor()) {
            switch (item.getTier()) {
                case Armor.TIER_CHAIN:
                    return 12;
                case Armor.TIER_IRON:
                    return 9;
                case Armor.TIER_DIAMOND:
                    return 10;
                case Armor.TIER_LEATHER:
                    return 15;
                case Armor.TIER_GOLD:
                    return 25;
            }
        }

        if (item.isTool()) {
            switch (item.getTier()) {
                case Tool.TIER_WOODEN:
                    return 15;
                case Tool.TIER_STONE:
                    return 5;
                case Tool.TIER_DIAMOND:
                    return 10;
                case Tool.TIER_IRON:
                    return 14;
                case Tool.TIER_GOLD:
                    return 22;
            }
        }

        return 0;
    }

    public void onEnchant(int clicked) {
        if (clicked < 0 || clicked > 2) {
            return;
        }

        HashSet<Integer> enchantments = this.getAvalaibleEnchantments();

        for (int i = 0; i < enchLevelCosts.length; i++) {
            int amount = (i == 0 ? 1 : i) + (random.nextInt(3) == 0 ? 1 : 0);

            HashSet<Integer> rndEnch = new HashSet<>();

            for (i = amount; i >= 0; ) {
                int rnd = random.nextInt(enchantments.size());

                if (rndEnch.contains(rnd)) {
                    continue;
                }

                rndEnch.add(rnd);
                i--;
            }
        }
    }

    public HashSet<Integer> getAvalaibleEnchantments() {
        Item item = this.getItem(0);

        int specific = 0;
        int slot = 0;

        if (item.isTool()) {
            slot = Enchantment.SLOT_TOOL;

            if (item.isSword()) {
                specific = Enchantment.SLOT_SWORD;
            } else if (item.isPickaxe()) {
                specific = Enchantment.SLOT_PICKAXE;
            } else if (item.isAxe()) {
                specific = Enchantment.SLOT_AXE;
            } else if (item.isShovel()) {
                specific = Enchantment.SLOT_SHOVEL;
            } else if (item.getId() == Item.BOW) {
                specific = Enchantment.SLOT_BOW;
            }
        } else if (item.isArmor()) {
            slot = Enchantment.SLOT_ARMOR;

            if (item.isHelmet()) {
                specific = Enchantment.SLOT_HEAD;
            } else if (item.isChestplate()) {
                specific = Enchantment.SLOT_TORSO;
            } else if (item.isLeggings()) {
                specific = Enchantment.SLOT_LEGS;
            } else if (item.isBoots()) {
                specific = Enchantment.SLOT_FEET;
            }
        }

        HashSet<Integer> enchantments = new HashSet<>();

        for (int i = 0; i < 25; i++) {
            Enchantment ench = Enchantment.getEnchantment(i);

            if (ench.getSlot() == slot || ench.getSlot() == specific || ench.getSlot() == Enchantment.SLOT_ALL) {
                enchantments.add(i);
            }
        }

        return enchantments;
    }

    public void update() {
        ContainerSetDataPacket pk = new ContainerSetDataPacket();

        for (Player player : this.getViewers()) {
            int windowId = player.getWindowId(this);
            if (windowId > 0) {

                //this may be wrong
                for (int i = 0; i < enchLevelCosts.length; i++) {
                    pk.windowid = (byte) windowId;

                    pk.property = 0;
                    pk.value = enchId[i];
                    player.dataPacket(pk);

                    pk.property = 1;
                    pk.value = enchLevel[i];
                    player.dataPacket(pk);

                    pk.property = 2;
                    pk.value = enchLevelCosts[i];
                    player.dataPacket(pk);
                }
            }
        }
    }
}
