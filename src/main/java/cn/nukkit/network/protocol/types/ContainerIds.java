package cn.nukkit.network.protocol.types;

/**
 * @author CreeperFace
 */
public interface ContainerIds {

    byte NONE = -1;
    byte INVENTORY = 0;
    byte FIRST = 1;
    byte ANVIL = 2;
    byte ENCHANTING_TABLE = 3;
    byte BEACON = 4;
    byte LAST = 100;
    byte OFFHAND = 119;
    byte ARMOR = 120;
    byte CREATIVE = 121;
    byte HOTBAR = 122;
    byte FIXED_INVENTORY = 123;
    byte UI = 124;
    byte SPECIAL_INVENTORY = 0;
    byte SPECIAL_OFFHAND = 0x77;
    byte SPECIAL_ARMOR = 0x78;
    byte SPECIAL_CREATIVE = 0x79;
    byte SPECIAL_HOTBAR = 0x7a;
    byte SPECIAL_FIXED_INVENTORY = 0x7b;
}
