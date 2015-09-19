package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface Info {

    byte CURRENT_PROTOCOL = 34;

    byte LOGIN_PACKET = (byte) 0x82;
    byte PLAY_STATUS_PACKET = (byte) 0x83;

    byte DISCONNECT_PACKET = (byte) 0x84;

    byte TEXT_PACKET = (byte) 0x85;
    byte SET_TIME_PACKET = (byte) 0x86;

    byte START_GAME_PACKET = (byte) 0x87;

    byte ADD_PLAYER_PACKET = (byte) 0x88;
    byte REMOVE_PLAYER_PACKET = (byte) 0x89;

    byte ADD_ENTITY_PACKET = (byte) 0x8a;
    byte REMOVE_ENTITY_PACKET = (byte) 0x8b;
    byte ADD_ITEM_ENTITY_PACKET = (byte) 0x8c;
    byte TAKE_ITEM_ENTITY_PACKET = (byte) 0x8d;

    byte MOVE_ENTITY_PACKET = (byte) 0x8e;
    byte MOVE_PLAYER_PACKET = (byte) 0x8f;

    byte REMOVE_BLOCK_PACKET = (byte) 0x90;
    byte UPDATE_BLOCK_PACKET = (byte) 0x91;

    byte ADD_PAINTING_PACKET = (byte) 0x92;

    byte EXPLODE_PACKET = (byte) 0x93;

    byte LEVEL_EVENT_PACKET = (byte) 0x94;
    byte TILE_EVENT_PACKET = (byte) 0x95;
    byte ENTITY_EVENT_PACKET = (byte) 0x96;
    byte MOB_EFFECT_PACKET = (byte) 0x97;

    byte PLAYER_EQUIPMENT_PACKET = (byte) 0x98;
    byte PLAYER_ARMOR_EQUIPMENT_PACKET = (byte) 0x99;
    byte INTERACT_PACKET = (byte) 0x9a;
    byte USE_ITEM_PACKET = (byte) 0x9b;
    byte PLAYER_ACTION_PACKET = (byte) 0x9c;
    byte HURT_ARMOR_PACKET = (byte) 0x9d;
    byte SET_ENTITY_DATA_PACKET = (byte) 0x9e;
    byte SET_ENTITY_MOTION_PACKET = (byte) 0x9f;
    byte SET_ENTITY_LINK_PACKET = (byte) 0xa0;
    byte SET_HEALTH_PACKET = (byte) 0xa1;
    byte SET_SPAWN_POSITION_PACKET = (byte) 0xa2;
    byte ANIMATE_PACKET = (byte) 0xa3;
    byte RESPAWN_PACKET = (byte) 0xa4;
    byte DROP_ITEM_PACKET = (byte) 0xa5;
    byte CONTAINER_OPEN_PACKET = (byte) 0xa6;
    byte CONTAINER_CLOSE_PACKET = (byte) 0xa7;
    byte CONTAINER_SET_SLOT_PACKET = (byte) 0xa8;
    byte CONTAINER_SET_DATA_PACKET = (byte) 0xa9;
    byte CONTAINER_SET_CONTENT_PACKET = (byte) 0xaa;
    //public static final byte CONTAINER_ACK_PACKET = (byte) 0xab;
    byte ADVENTURE_SETTINGS_PACKET = (byte) 0xac;
    byte TILE_ENTITY_DATA_PACKET = (byte) 0xad;
    //public static final byte PLAYER_INPUT_PACKET = (byte) 0xae;
    byte FULL_CHUNK_DATA_PACKET = (byte) 0xaf;
    byte SET_DIFFICULTY_PACKET = (byte) 0xb0;
    byte BATCH_PACKET = (byte) 0xb1;

}
