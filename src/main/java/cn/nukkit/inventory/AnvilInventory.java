package cn.nukkit.inventory;

import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.player.Player;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class AnvilInventory extends ContainerInventory {

    public static final int TARGET = 0;
    public static final int SACRIFICE = 1;
    public static final int RESULT = 2;

    public AnvilInventory(Position position) {
        super(null, InventoryType.ANVIL);
        this.holder = new FakeBlockMenu(this, position);
    }

    @Override
    public FakeBlockMenu getHolder() {
        return (FakeBlockMenu) this.holder;
    }

    public boolean onRename(Player player, Item resultItem) {
        Item local = getItem(TARGET);
        Item second = getItem(SACRIFICE);

        if (!resultItem.equals(local, true, false) || resultItem.getCount() != local.getCount()) {
            //Item does not match target item. Everything must match except the tags.
            return false;
        }

        if (local.equals(resultItem)) {
            //just item transaction
            return true;
        }

        if (local.getId() != 0 && second.getId() == 0) { //only rename
            local.setCustomName(resultItem.getCustomName());
            setItem(RESULT, local);
            player.getInventory().addItem(local);
            clearAll();
            player.getInventory().sendContents(player);
            sendContents(player);

            player.getLevel().addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_RANDOM_ANVIL_USE);
            return true;
        } else if (local.getId() != 0 && second.getId() != 0) { //enchants combining
            if (!local.equals(second, true, false)) {
                return false;
            }

            if (local.getId() != 0 && second.getId() != 0) {
                Item result = local.clone();
                int enchants = 0;

                ArrayList<Enchantment> enchantments = new ArrayList<>(Arrays.asList(second.getEnchantments()));

                ArrayList<Enchantment> baseEnchants = new ArrayList<>();

                for (Enchantment ench : local.getEnchantments()) {
                    if (ench.isMajor()) {
                        baseEnchants.add(ench);
                    }
                }

                for (Enchantment enchantment : enchantments) {
                    if (enchantment.getLevel() < 0 || enchantment.getId() < 0) {
                        continue;
                    }

                    if (enchantment.isMajor()) {
                        boolean same = false;
                        boolean another = false;

                        for (Enchantment baseEnchant : baseEnchants) {
                            if (baseEnchant.getId() == enchantment.getId())
                                same = true;
                            else {
                                another = true;
                            }
                        }

                        if (!same && another) {
                            continue;
                        }
                    }

                    Enchantment localEnchantment = local.getEnchantment(enchantment.getId());

                    if (localEnchantment != null) {
                        int level = Math.max(localEnchantment.getLevel(), enchantment.getLevel());

                        if (localEnchantment.getLevel() == enchantment.getLevel())
                            level++;

                        enchantment.setLevel(level);
                        result.addEnchantment(enchantment);
                        continue;
                    }

                    result.addEnchantment(enchantment);
                    enchants++;
                }

                result.setCustomName(resultItem.getCustomName());

                player.getInventory().addItem(result);
                player.getInventory().sendContents(player);
                clearAll();
                sendContents(player);

                player.getLevel().addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_RANDOM_ANVIL_USE);
                return true;
            }
        }

        return false;
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);
        who.craftingType = Player.CRAFTING_SMALL;
        who.resetCraftingGridType();

        for (int i = 0; i < 2; ++i) {
            this.getHolder().getLevel().dropItem(this.getHolder().add(0.5, 0.5, 0.5), this.getItem(i));
            this.clear(i);
        }
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.CRAFTING_ANVIL;
    }

    /*@Override
    public boolean setItem(int index, Item item, boolean send) {
        return super.setItem(index, item, false);
    }

    @Override
    public void sendSlot(int index, Player... players) {

    }

    @Override
    public void sendContents(Player... player) {

    }*/
}
