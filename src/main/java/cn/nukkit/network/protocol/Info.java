package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX & iNevet
 * Nukkit Project
 */
public interface Info {

    /**
     * Actual Minecraft: PE protocol version
     */
    byte CURRENT_PROTOCOL = 34;
    byte LOGIN_PACKET = (byte) 0x8f;
    byte PLAY_STATUS_PACKET = (byte) 0x90;
    byte DISCONNECT_PACKET = (byte) 0x91;
    byte BATCH_PACKET = (byte) 0x92;
    byte TEXT_PACKET = (byte) 0x93;
    byte SET_TIME_PACKET = (byte) 0x94;
    byte START_GAME_PACKET = (byte) 0x95;
    byte ADD_PLAYER_PACKET = (byte) 0x96;
    byte REMOVE_PLAYER_PACKET = (byte) 0x97;
    byte ADD_ENTITY_PACKET = (byte) 0x98;
    byte REMOVE_ENTITY_PACKET = (byte) 0x99;
    byte ADD_ITEM_ENTITY_PACKET = (byte) 0x9a;
    byte TAKE_ITEM_ENTITY_PACKET = (byte) 0x9b;
    byte MOVE_ENTITY_PACKET = (byte) 0x9c;
    byte MOVE_PLAYER_PACKET = (byte) 0x9d;
    byte REMOVE_BLOCK_PACKET = (byte) 0x9e;
    byte UPDATE_BLOCK_PACKET = (byte) 0x9f;
    byte ADD_PAINTING_PACKET = (byte) 0xa0;
    byte EXPLODE_PACKET = (byte) 0xa1;
    byte LEVEL_EVENT_PACKET = (byte) 0xa2;
    byte TILE_EVENT_PACKET = (byte) 0xa3;
    byte ENTITY_EVENT_PACKET = (byte) 0xa4;
    byte MOB_EFFECT_PACKET = (byte) 0xa5;
    byte UPDATE_ATTRIBUTES_PACKET = (byte) 0xa6;
    byte MOB_EQUIPMENT_PACKET = (byte) 0xa7;
    byte MOB_ARMOR_EQUIPMENT_PACKET = (byte) 0xa8;
    byte INTERACT_PACKET = (byte) 0xa9;
    byte USE_ITEM_PACKET = (byte) 0xaa;
    byte PLAYER_ACTION_PACKET = (byte) 0xab;
    byte HURT_ARMOR_PACKET = (byte) 0xac;
    byte SET_ENTITY_DATA_PACKET = (byte) 0xad;
    byte SET_ENTITY_MOTION_PACKET = (byte) 0xae;
    byte SET_ENTITY_LINK_PACKET = (byte) 0xaf;
    byte SET_HEALTH_PACKET = (byte) 0xb0;
    byte SET_SPAWN_POSITION_PACKET = (byte) 0xb1;
    byte ANIMATE_PACKET = (byte) 0xb2;
    byte RESPAWN_PACKET = (byte) 0xb3;
    byte DROP_ITEM_PACKET = (byte) 0xb4;
    byte CONTAINER_OPEN_PACKET = (byte) 0xb5;
    byte CONTAINER_CLOSE_PACKET = (byte) 0xb6;
    byte CONTAINER_SET_SLOT_PACKET = (byte) 0xb7;
    byte CONTAINER_SET_DATA_PACKET = (byte) 0xb8;
    byte CONTAINER_SET_CONTENT_PACKET = (byte) 0xb9;
    byte CRAFTING_DATA_PACKET = (byte) 0xba;
    byte CRAFTING_EVENT_PACKET = (byte) 0xbb;
    byte ADVENTURE_SETTINGS_PACKET = (byte) 0xbc;
    byte TILE_ENTITY_DATA_PACKET = (byte) 0xbd;
    //public static final byte PLAYER_INPUT_PACKET = (byte) 0xbe;
    byte FULL_CHUNK_DATA_PACKET = (byte) 0xbf;
    byte SET_DIFFICULTY_PACKET = (byte) 0xc0;
    //public static final byte CHANGE_DIMENSION_PACKET = (byte) 0xc1;
    //public static final byte SET_PLAYER_GAMETYPE_PACKET = (byte) 0xc2;
    byte PLAYER_LIST_PACKET = (byte) 0xc3;
    //public static final byte TELEMETRY_EVENT_PACKET = (byte) 0xc4;

}
