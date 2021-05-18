package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.WorldSoundEventPacket;
import cn.nukkit.world.Sound;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
abstract public class ItemArmor extends Item implements ItemDurable {

    public static final int TIER_LEATHER = 1;
    public static final int TIER_IRON = 2;
    public static final int TIER_CHAIN = 3;
    public static final int TIER_GOLD = 4;
    public static final int TIER_DIAMOND = 5;
    public static final int TIER_NETHERITE = 6;
    public static final int TIER_OTHER = 7;

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
        Item oldSlotItem = Item.get(AIR);
        if (this.isHelmet()) {
            oldSlotItem = player.getInventory().getHelmet();
            if (player.getInventory().setHelmet(this)) {
                equip = true;
            }
        } else if (this.isChestplate()) {
            oldSlotItem = player.getInventory().getChestplate();
            if (player.getInventory().setChestplate(this)) {
                equip = true;
            }
        } else if (this.isLeggings()) {
            oldSlotItem = player.getInventory().getLeggings();
            if (player.getInventory().setLeggings(this)) {
                equip = true;
            }
        } else if (this.isBoots()) {
            oldSlotItem = player.getInventory().getBoots();
            if (player.getInventory().setBoots(this)) {
                equip = true;
            }
        }
        if (equip) {
            player.getInventory().setItem(player.getInventory().getHeldItemIndex(), oldSlotItem);
            switch (this.getTier()) {
                case TIER_CHAIN:
                    player.getWorld().addLevelSoundEvent(player, WorldSoundEventPacket.SOUND_ARMOR_EQUIP_CHAIN);
                    break;
                case TIER_DIAMOND:
                    player.getWorld().addLevelSoundEvent(player, WorldSoundEventPacket.SOUND_ARMOR_EQUIP_DIAMOND);
                    break;
                case TIER_GOLD:
                    player.getWorld().addLevelSoundEvent(player, WorldSoundEventPacket.SOUND_ARMOR_EQUIP_GOLD);
                    break;
                case TIER_IRON:
                    player.getWorld().addLevelSoundEvent(player, WorldSoundEventPacket.SOUND_ARMOR_EQUIP_IRON);
                    break;
                case TIER_LEATHER:
                    player.getWorld().addLevelSoundEvent(player, WorldSoundEventPacket.SOUND_ARMOR_EQUIP_LEATHER);
                    break;
                case TIER_NETHERITE:
                    player.getWorld().addSound(player, Sound.ARMOR_EQUIP_NETHERITE);
                case TIER_OTHER:
                default:
                    player.getWorld().addLevelSoundEvent(player, WorldSoundEventPacket.SOUND_ARMOR_EQUIP_GENERIC);
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

    @Override
    public boolean isUnbreakable() {
        Tag tag = this.getNamedTagEntry("Unbreakable");
        return tag instanceof ByteTag && ((ByteTag) tag).data > 0;
    }
}
