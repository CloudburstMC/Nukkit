package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX & iNevet
 * Nukkit Project
 */
public interface ProtocolInfo {

    /**
     * Actual Minecraft: PE protocol version
     */
    byte CURRENT_PROTOCOL = 81;

    byte LOGIN_PACKET = (byte) 0x01;
    byte PLAY_STATUS_PACKET = (byte) 0x02;
    byte SERVER_TO_CLIENT_HANDSHAKE_PACKET = (byte) 0x03;
    byte CLIENT_TO_SERVER_HANDSHAKE_PACKET = (byte) 0x04;
    byte DISCONNECT_PACKET = (byte) 0x05;
    byte BATCH_PACKET = (byte) 0x06;
    byte TEXT_PACKET = (byte) 0x07;
    byte SET_TIME_PACKET = (byte) 0x08;
    byte START_GAME_PACKET = (byte) 0x09;
    byte ADD_PLAYER_PACKET = (byte) 0x0a;
    byte ADD_ENTITY_PACKET = (byte) 0x0b;
    byte REMOVE_ENTITY_PACKET = (byte) 0x0c;
    byte ADD_ITEM_ENTITY_PACKET = (byte) 0x0d;
    byte TAKE_ITEM_ENTITY_PACKET = (byte) 0x0e;
    byte MOVE_ENTITY_PACKET = (byte) 0x0f;
    byte MOVE_PLAYER_PACKET = (byte) 0x10;
    byte RIDER_JUMP_PACKET = (byte) 0x11;
    byte REMOVE_BLOCK_PACKET = (byte) 0x12;
    byte UPDATE_BLOCK_PACKET = (byte) 0x13;
    byte ADD_PAINTING_PACKET = (byte) 0x14;
    byte EXPLODE_PACKET = (byte) 0x15;
    byte LEVEL_EVENT_PACKET = (byte) 0x16;
    byte BLOCK_EVENT_PACKET = (byte) 0x17;
    byte ENTITY_EVENT_PACKET = (byte) 0x18;
    byte MOB_EFFECT_PACKET = (byte) 0x19;
    byte UPDATE_ATTRIBUTES_PACKET = (byte) 0x1a;
    byte MOB_EQUIPMENT_PACKET = (byte) 0x1b;
    byte MOB_ARMOR_EQUIPMENT_PACKET = (byte) 0x1c;
    byte INTERACT_PACKET = (byte) 0x1e;
    byte USE_ITEM_PACKET = (byte) 0x1f;
    byte PLAYER_ACTION_PACKET = (byte) 0x20;
    byte HURT_ARMOR_PACKET = (byte) 0x21;
    byte SET_ENTITY_DATA_PACKET = (byte) 0x22;
    byte SET_ENTITY_MOTION_PACKET = (byte) 0x23;
    byte SET_ENTITY_LINK_PACKET = (byte) 0x24;
    byte SET_HEALTH_PACKET = (byte) 0x25;
    byte SET_SPAWN_POSITION_PACKET = (byte) 0x26;
    byte ANIMATE_PACKET = (byte) 0x27;
    byte RESPAWN_PACKET = (byte) 0x28;
    byte DROP_ITEM_PACKET = (byte) 0x29;
    byte CONTAINER_OPEN_PACKET = (byte) 0x2a;
    byte CONTAINER_CLOSE_PACKET = (byte) 0x2b;
    byte CONTAINER_SET_SLOT_PACKET = (byte) 0x2c;
    byte CONTAINER_SET_DATA_PACKET = (byte) 0x2d;
    byte CONTAINER_SET_CONTENT_PACKET = (byte) 0x2e;
    byte CRAFTING_DATA_PACKET = (byte) 0x2f;
    byte CRAFTING_EVENT_PACKET = (byte) 0x30;
    byte ADVENTURE_SETTINGS_PACKET = (byte) 0x31;
    byte BLOCK_ENTITY_DATA_PACKET = (byte) 0x32;
    byte PLAYER_INPUT_PACKET = (byte) 0x33;
    byte FULL_CHUNK_DATA_PACKET = (byte) 0x34;
    byte SET_DIFFICULTY_PACKET = (byte) 0x35;
    byte CHANGE_DIMENSION_PACKET = (byte) 0x36;
    byte SET_PLAYER_GAMETYPE_PACKET = (byte) 0x37;
    byte PLAYER_LIST_PACKET = (byte) 0x38;
    byte TELEMETRY_EVENT_PACKET = (byte) 0x39;
    byte SPAWN_EXPERIENCE_ORB_PACKET = (byte) 0x3a;
    byte CLIENTBOUND_MAP_ITEM_DATA_PACKET = (byte) 0x3b;
    byte MAP_INFO_REQUEST_PACKET = (byte) 0x3c;
    byte REQUEST_CHUNK_RADIUS_PACKET = (byte) 0x3d;
    byte CHUNK_RADIUS_UPDATED_PACKET = (byte) 0x3e;
    byte ITEM_FRAME_DROP_ITEM_PACKET = (byte) 0x3f;
    byte REPLACE_SELECTED_ITEM_PACKET = (byte) 0x40;
    byte ADD_ITEM_PACKET = (byte) 0x41;
}