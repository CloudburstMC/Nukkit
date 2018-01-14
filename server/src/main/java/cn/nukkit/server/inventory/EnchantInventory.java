package cn.nukkit.server.inventory;

import cn.nukkit.server.Player;
import cn.nukkit.server.block.Block;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemBookEnchanted;
import cn.nukkit.server.item.enchantment.Enchantment;
import cn.nukkit.server.item.enchantment.EnchantmentEntry;
import cn.nukkit.server.item.enchantment.EnchantmentList;
import cn.nukkit.server.level.Location;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.level.Position;
import cn.nukkit.server.math.NukkitRandom;
import cn.nukkit.server.network.protocol.CraftingDataPacket;
import cn.nukkit.server.util.DyeColor;

import java.util.ArrayList;
import java.util.Arrays;
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
        super(null, InventoryType.ENCHANT_TABLE);
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
    public void onSlotChange(int index, Item before, boolean send) {
        super.onSlotChange(index, before, send);

        if (index == 0) {
            Item item = this.getItem(0);
            if (item.getId() == Item.AIR) {
                this.entries = null;
            } else if (before.getId() == Item.AIR && !item.hasEnchantments()) {
                //before enchant
                if (this.entries == null) {
                    int enchantAbility = item.getEnchantAbility();
                    this.entries = new EnchantmentEntry[3];
                    for (int i = 0; i < 3; i++) {
                        ArrayList<Enchantment> result = new ArrayList<>();

                        int level = this.levels[i];
                        int k = level;
                        k += ThreadLocalRandom.current().nextInt(0, Math.round(enchantAbility / 2f));
                        k += ThreadLocalRandom.current().nextInt(0, Math.round(enchantAbility / 2f));
                        k++;
                        float bonus = (ThreadLocalRandom.current().nextFloat() + ThreadLocalRandom.current().nextFloat() - 1) * 0.15f + 1;
                        int modifiedLevel = (int) (k * (1 + bonus) + 0.5f);

                        ArrayList<Enchantment> possible = new ArrayList<>();
                        for (Enchantment enchantment : Enchantment.getEnchantments()) {
                            if (enchantment.canEnchant(item)) {
                                for (int enchLevel = enchantment.getMinLevel(); enchLevel < enchantment.getMaxLevel(); enchLevel++) {
                                    if (modifiedLevel >= enchantment.getMinEnchantAbility(enchLevel) && modifiedLevel <= enchantment.getMaxEnchantAbility(enchLevel)) {
                                        enchantment.setLevel(enchLevel);
                                        possible.add(enchantment);
                                    }
                                }

                            }
                        }

                        int[] weights = new int[possible.size()];
                        int total = 0;

                        for (int j = 0; j < weights.length; j++) {
                            int weight = possible.get(j).getWeight();
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

                                for (Enchantment e : new ArrayList<>(possible)) {
                                    if (!e.isCompatibleWith(enchantment)) {
                                        possible.remove(e);
                                    }
                                }

                                weights = new int[possible.size()];
                                total = 0;

                                for (int j = 0; j < weights.length; j++) {
                                    int weight = possible.get(j).getWeight();
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

                        this.entries[i] = new EnchantmentEntry(result.stream().toArray(Enchantment[]::new), level, Enchantment.getRandomName());
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
        Item result = (before.getId() == Item.BOOK) ? new ItemBookEnchanted() : before;
        if (!before.hasEnchantments() && after.hasEnchantments() && after.getId() == result.getId() && this.levels != null && this.entries != null) {
            Enchantment[] enchantments = after.getEnchantments();
            for (int i = 0; i < 3; i++) {
                if (Arrays.equals(enchantments, this.entries[i].getEnchantments())) {
                    Item lapis = this.getItem(1);
                    int level = who.getExperienceLevel();
                    int exp = who.getExperience();
                    int cost = this.entries[i].getCost();
                    if (lapis.getId() == Item.DYE && lapis.getDamage() == DyeColor.BLUE.getDyeData() && lapis.getCount() > i && level >= cost) {
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
        int count = 0;
        Location loc = this.getHolder().getLocation();
        NukkitLevel level = loc.getLevel();

        for (int y = 0; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (z == 0 && x == 0) continue;
                    if (level.getBlock(loc.add(x, 0, z)).isTransparent()) {
                        if (level.getBlock(loc.add(0, 1, 0)).isTransparent()) {
                            //diagonal and straight
                            if (level.getBlock(loc.add(x << 1, y, z << 1)).getId() == Block.BOOKSHELF) {
                                count++;
                            }

                            if (x != 0 && z != 0) {
                                //one block diagonal and one straight
                                if (level.getBlock(loc.add(x << 1, y, z)).getId() == Block.BOOKSHELF) {
                                    ++count;
                                }

                                if (level.getBlock(loc.add(x, y, z << 1)).getId() == Block.BOOKSHELF) {
                                    ++count;
                                }
                            }
                        }
                    }
                }
            }
        }

        return count;
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

        //Server.broadcastPacket(this.getViewers(), pk); //TODO: fix this, causes crash in 1.2
    }

   /*@Override
    public void sendSlot(int index, Player... players) {

    }

    @Override
    public void sendContents(Player... players) {

    }

    @Override
    public boolean setItem(int index, ItemUse item, boolean send) {
        return super.setItem(index, item, false);
    }*/
}
