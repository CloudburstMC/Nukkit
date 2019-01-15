package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
abstract public class ItemArmor extends Item {

    public static final int TIER_LEATHER = 1;
    public static final int TIER_IRON = 2;
    public static final int TIER_CHAIN = 3;
    public static final int TIER_GOLD = 4;
    public static final int TIER_DIAMOND = 5;
    public static final int TIER_OTHER = 6;

    public ItemArmor(int id) {
        super(id);
    }

    public ItemArmor(int id, Integer meta) {
        super(id, meta);
    }

    public ItemArmor(int id, Integer meta, int count) {
        super(id, meta, count);
    }

    public ItemArmor(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean isArmor() {
        return true;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        boolean equip = false;
        if (this.isHelmet() && player.getInventory().getHelmet().isNull()) {
            if (player.getInventory().setHelmet(this)) {
                player.getInventory().clear(player.getInventory().getHeldItemIndex());
                equip = true;
            }
        } else if (this.isChestplate() && player.getInventory().getChestplate().isNull()) {
            if (player.getInventory().setChestplate(this)) {
                player.getInventory().clear(player.getInventory().getHeldItemIndex());
                equip = true;
            }
        } else if (this.isLeggings() && player.getInventory().getLeggings().isNull()) {
            if (player.getInventory().setLeggings(this)) {
                player.getInventory().clear(player.getInventory().getHeldItemIndex());
                equip = true;
            }
        } else if (this.isBoots() && player.getInventory().getBoots().isNull()) {
            if (player.getInventory().setBoots(this)) {
                player.getInventory().clear(player.getInventory().getHeldItemIndex());
                equip = true;
            }
        }
        if (equip) {
            switch (this.getTier()) {
                case TIER_CHAIN:
                    player.getLevel().addSound(player, Sound.ARMOR_EQUIP_CHAIN);
                    break;
                case TIER_DIAMOND:
                    player.getLevel().addSound(player, Sound.ARMOR_EQUIP_DIAMOND);
                    break;
                case TIER_OTHER:
                    player.getLevel().addSound(player, Sound.ARMOR_EQUIP_GENERIC);
                    break;
                case TIER_GOLD:
                    player.getLevel().addSound(player, Sound.ARMOR_EQUIP_GOLD);
                    break;
                case TIER_IRON:
                    player.getLevel().addSound(player, Sound.ARMOR_EQUIP_IRON);
                    break;
                case TIER_LEATHER:
                    player.getLevel().addSound(player, Sound.ARMOR_EQUIP_LEATHER);
                    break;
            }
        }

        return this.getCount() == 0;
    }

    @Override
    public int getEnchantAbility() {
        switch (this.getTier()) {
            case TIER_CHAIN:
                return 12;
            case TIER_LEATHER:
                return 15;
            case TIER_DIAMOND:
                return 10;
            case TIER_GOLD:
                return 25;
            case TIER_IRON:
                return 9;
        }

        return 0;
    }
}
