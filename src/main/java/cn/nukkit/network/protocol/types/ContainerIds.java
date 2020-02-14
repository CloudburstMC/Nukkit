package cn.nukkit.network.protocol.types;

/**
 * @author CreeperFace
 */
public interface ContainerIds {

    int NONE = -1;
    int INVENTORY = 0;
    int FIRST = 1;
    int LAST = 100;
    int OFFHAND = 119;
    int ARMOR = 120;
    int CREATIVE = 121;
    int HOTBAR = 122;
    int FIXED_INVENTORY = 123;
    int UI = 124;
    int SPECIAL_INVENTORY = 0;
    int SPECIAL_OFFHAND = 0x77;
    int SPECIAL_ARMOR = 0x78;
    int SPECIAL_CREATIVE = 0x79;
    int SPECIAL_HOTBAR = 0x7a;
    int SPECIAL_FIXED_INVENTORY = 0x7b;
}
