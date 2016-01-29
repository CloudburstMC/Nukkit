package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.BookEnchanted;
import cn.nukkit.item.Dye;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentEntry;
import cn.nukkit.item.enchantment.EnchantmentLevelTable;
import cn.nukkit.item.enchantment.EnchantmentList;
import cn.nukkit.level.Position;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.network.protocol.CraftingDataPacket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantInventory extends ContainerInventory {

    private final Random random = new Random();

    private int bookshelfAmount = 0;

    private int[] levels = null;
    private EnchantmentEntry[] entries = null;

    public EnchantInventory(Position position) {
        super(null, InventoryType.get(InventoryType.ENCHANT_TABLE));
        this.holder = new FakeBlockMenu(this, position);
    }

    @Override
    public FakeBlockMenu getHolder() {
        return (FakeBlockMenu) this.holder;
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);

        if (this.levels == null) {
            this.levels = new int[3];
            this.bookshelfAmount = this.countBookshelf();

            if (this.bookshelfAmount < 0) {
                this.bookshelfAmount = 0;
            }

            if (this.bookshelfAmount > 15) {
                this.bookshelfAmount = 15;
            }

            NukkitRandom random = new NukkitRandom();

            double base = (double) random.nextRange(1, 8) + (bookshelfAmount / 2d) + (double) random.nextRange(0, bookshelfAmount);
            this.levels[0] = (int) Math.max(base / 3, 1);
            this.levels[1] = (int) ((base * 2) / 3 + 1);
            this.levels[2] = (int) Math.max(base, bookshelfAmount * 2);
        }

    }

    @Override
    public void onSlotChange(int index, Item before) {
        super.onSlotChange(index, before);

        if (index == 0) {
            Item item = this.getItem(0);
            if (item.getId() == Item.AIR) {
                this.entries = null;
            } else if (before.getId() == Item.AIR && !item.hasEnchantments()) {
                //before enchant
                if (this.entries == null) {
                    int enchantAbility = Enchantment.getEnchantAbility(item);
                    this.entries = new EnchantmentEntry[3];
                    for (int i = 0; i < 3; i++) {
                        ArrayList<Enchantment> result = new ArrayList<>();

                        int level = this.levels[i];
                        int k = level + ThreadLocalRandom.current().nextInt(0, Math.round(Math.round(enchantAbility / 4f) * 2f)) + 1;
                        float bonus = (ThreadLocalRandom.current().nextFloat() + ThreadLocalRandom.current().nextFloat() - 1) * 0.15f + 1;
                        int modifiedLevel = (int) (k * (1 + bonus) + 0.5f);

                        ArrayList<Enchantment> possible = EnchantmentLevelTable.getPossibleEnchantments(item, modifiedLevel);
                        int[] weights = new int[possible.size()];
                        int total = 0;

                        for (int j = 0; j < weights.length; j++) {
                            int id = possible.get(j).getId();
                            int weight = Enchantment.getEnchantWeight(id);
                            weights[j] = weight;
                            total += weight;
                        }

                        int v = ThreadLocalRandom.current().nextInt(total + 1);

                        int sum = 0;
                        int key;
                        for (key = 0; key < weights.length; ++key) {
                            sum += weights[key];
                            if (sum >= v) {
                                key++;
                                break;
                            }
                        }
                        key--;

                        Enchantment enchantment = possible.get(key);
                        result.add(enchantment);
                        possible.remove(key);

                        //Extra enchantment
                        while (!possible.isEmpty()) {
                            modifiedLevel = Math.round(modifiedLevel / 2f);
                            v = ThreadLocalRandom.current().nextInt(0, 51);
                            if (v <= (modifiedLevel + 1)) {

                                removeConflictEnchantment(enchantment, possible);

                                weights = new int[possible.size()];
                                total = 0;

                                for (int j = 0; j < weights.length; j++) {
                                    int id = possible.get(j).getId();
                                    int weight = Enchantment.getEnchantWeight(id);
                                    weights[j] = weight;
                                    total += weight;
                                }

                                v = ThreadLocalRandom.current().nextInt(total + 1);
                                sum = 0;
                                for (key = 0; key < weights.length; ++key) {
                                    sum += weights[key];
                                    if (sum >= v) {
                                        key++;
                                        break;
                                    }
                                }
                                key--;

                                enchantment = possible.get(key);
                                result.add(enchantment);
                                possible.remove(key);
                            } else {
                                break;
                            }
                        }

                        this.entries[i] = new EnchantmentEntry(result.stream().toArray(Enchantment[]::new), level, Enchantment.generateName());
                    }
                }

                this.sendEnchantmentList();
            }
        }
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);

        for (int i = 0; i < 2; ++i) {
            this.getHolder().getLevel().dropItem(this.getHolder().add(0.5, 0.5, 0.5), this.getItem(i));
            this.clear(i);
        }

        if (this.getViewers().size() == 0) {
            this.levels = null;
            this.entries = null;
            this.bookshelfAmount = 0;
        }
    }

    public void onEnchant(Player who, Item before, Item after) {
        Item result = (before.getId() == Item.BOOK) ? new BookEnchanted() : before;
        if (!before.hasEnchantments() && after.hasEnchantments() && after.getId() == result.getId() && this
                .levels != null && this.entries != null) {
            System.out.println("here");
            Enchantment[] enchantments = after.getEnchantments();
            for (int i = 0; i < 3; i++) {
                if (Arrays.equals(enchantments, this.entries[i].getEnchantments())) {
                    Item lapis = this.getItem(1);
                    int level = who.getExperienceLevel();
                    int exp = who.getExperience();
                    int cost = this.entries[i].getCost();
                    if (lapis.getId() == Item.DYE && lapis.getDamage() == Dye.BLUE && lapis.getCount() > i && level >= cost) {
                        result.addEnchantment(enchantments);
                        this.setItem(0, result);
                        lapis.setCount(lapis.getCount() - i - 1);
                        this.setItem(1, lapis);
                        who.setExperience(exp, level - cost);
                        break;
                    }
                }
            }
        }
    }

    public int countBookshelf() {
        return 15;
        //todo calculate bookshelf
    }

    public void sendEnchantmentList() {
        CraftingDataPacket pk = new CraftingDataPacket();
        if (this.entries != null && this.levels != null) {
            EnchantmentList list = new EnchantmentList(this.entries.length);
            for (int i = 0; i < list.getSize(); i++) {
                list.setSlot(i, this.entries[i]);
            }
            pk.addEnchantList(list);
        }

        Server.broadcastPacket(this.getViewers(), pk);
    }

    private void removeConflictEnchantment(Enchantment enchantment, List<Enchantment> enchantments) {
        for (Enchantment e : new ArrayList<>(enchantments)) {
            int id = e.getId();
            if (id == enchantment.getId()) {
                enchantments.remove(e);
                continue;
            }

            if (id >= 0 && id <= 4 && enchantment.getId() >= 0 && enchantment.getId() <= 4) {
                //Protection
                enchantments.remove(e);
                continue;
            }

            if (id >= 9 && id <= 14 && enchantment.getId() >= 9 && enchantment.getId() <= 14) {
                //Weapon
                enchantments.remove(e);
                continue;
            }

            if ((id == Enchantment.TYPE_MINING_SILK_TOUCH && enchantment.getId() == Enchantment.TYPE_MINING_FORTUNE) || (id == Enchantment.TYPE_MINING_FORTUNE && enchantment.getId() == Enchantment.TYPE_MINING_SILK_TOUCH)) {
                enchantments.remove(e);
                continue;
            }
        }
    }
}
