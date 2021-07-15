package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class LevelEventPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.LEVEL_EVENT_PACKET;

    public static final int EVENT_SOUND_CLICK = 1000;
    public static final int EVENT_SOUND_CLICK_FAIL = 1001;
    public static final int EVENT_SOUND_SHOOT = 1002;
    public static final int EVENT_SOUND_DOOR = 1003;
    public static final int EVENT_SOUND_FIZZ = 1004;
    public static final int EVENT_SOUND_TNT = 1005;
    //TODO: 1006
    public static final int EVENT_SOUND_GHAST = 1007;
    public static final int EVENT_SOUND_BLAZE_SHOOT = 1008;
    public static final int EVENT_SOUND_GHAST_SHOOT = 1009;
    public static final int EVENT_SOUND_DOOR_BUMP = 1010;
    //TODO: 1011
    public static final int EVENT_SOUND_DOOR_CRASH = 1012;
    //TODO: 1013-1017
    public static final int EVENT_SOUND_ENDERMAN_TELEPORT = 1018;
    //TODO: 1019
    public static final int EVENT_SOUND_ANVIL_BREAK = 1020;
    public static final int EVENT_SOUND_ANVIL_USE = 1021;
    public static final int EVENT_SOUND_ANVIL_FALL = 1022;
    //TODO: 1023-1029
    public static final int EVENT_SOUND_ITEM_DROP = 1030;
    public static final int EVENT_SOUND_ITEM_THROWN = 1031;
    public static final int EVENT_SOUND_PORTAL = 1032;
    //TODO: 1033-1039
    public static final int EVENT_SOUND_ITEM_FRAME_ITEM_ADDED = 1040;
    public static final int EVENT_SOUND_ITEM_FRAME_PLACED = 1041;
    public static final int EVENT_SOUND_ITEM_FRAME_REMOVED = 1042;
    public static final int EVENT_SOUND_ITEM_FRAME_ITEM_REMOVED = 1043;
    public static final int EVENT_SOUND_ITEM_FRAME_ITEM_ROTATED = 1044;
    //TODO: 1045-1049
    public static final int EVENT_SOUND_CAMERA_TAKE_PICTURE = 1050;
    public static final int EVENT_SOUND_EXPERIENCE_ORB = 1051;
    public static final int EVENT_SOUND_TOTEM = 1052;
    //TODO: 1053-1059
    public static final int EVENT_SOUND_ARMOR_STAND_BREAK = 1060;
    public static final int EVENT_SOUND_ARMOR_STAND_HIT = 1061;
    public static final int EVENT_SOUND_ARMOR_STAND_FALL = 1062;
    public static final int EVENT_SOUND_ARMOR_STAND_PLACE = 1063;
    //TODO: 1064-1999
    public static final int EVENT_PARTICLE_SHOOT = 2000;
    public static final int EVENT_PARTICLE_DESTROY = 2001;
    public static final int EVENT_PARTICLE_SPLASH = 2002;
    public static final int EVENT_PARTICLE_EYE_DESPAWN = 2003;
    public static final int EVENT_PARTICLE_SPAWN = 2004;
    public static final int EVENT_PARTICLE_BONEMEAL = 2005;
    public static final int EVENT_GUARDIAN_CURSE = 2006;
    //TODO: 2007
    public static final int EVENT_PARTICLE_BLOCK_FORCE_FIELD = 2008;
    public static final int EVENT_PARTICLE_PROJECTILE_HIT = 2009;
    public static final int EVENT_PARTICLE_DRAGON_EGG_TELEPORT = 2010;
    //TODO: 1011 and 1012
    public static final int EVENT_PARTICLE_ENDERMAN_TELEPORT = 2013;
    public static final int EVENT_PARTICLE_PUNCH_BLOCK = 2014;
    //TODO: 2015-3000
    public static final int EVENT_START_RAIN = 3001;
    public static final int EVENT_START_THUNDER = 3002;
    public static final int EVENT_STOP_RAIN = 3003;
    public static final int EVENT_STOP_THUNDER = 3004;
    public static final int EVENT_PAUSE_GAME = 3005; //Data: 1 to pause, 0 to resume
    public static final int EVENT_PAUSE_GAME_NO_SCREEN = 3006; //Data: 1 to pause, 0 to resume - same effect as normal pause but without screen
    public static final int EVENT_SET_GAME_SPEED = 3007; //x coordinate of pos = scale factor (default 1.0)
    //TODO: 3008-3499
    public static final int EVENT_REDSTONE_TRIGGER = 3500;
    public static final int EVENT_SOUND_EXPLODE = 3501;
    public static final int EVENT_SOUND_CAULDRON_DYE_ARMOR = 3502;
    public static final int EVENT_SOUND_CAULDRON_FILL_POTION = 3504;
    public static final int EVENT_SOUND_CAULDRON_TAKE_POTION = 3505;
    public static final int EVENT_SOUND_CAULDRON_FILL_WATER = 3506;
    public static final int EVENT_CAULDRON_TAKE_WATER = 3507;
    public static final int EVENT_CAULDRON_ADD_DYE = 3508;
    public static final int EVENT_CAULDRON_CLEAN_BANNER = 3509;
    //TODO: 3510-3599
    public static final int EVENT_BLOCK_START_BREAK = 3600;
    public static final int EVENT_BLOCK_STOP_BREAK = 3601;
    //TODO: 3602-3999
    public static final int EVENT_SET_DATA = 4000;
    //TODO: 4001-9799
    public static final int EVENT_PLAYERS_SLEEPING = 9800;

    public static final int EVENT_ADD_PARTICLE_MASK = 0x4000;

    public int event;
    public Vector3f position;
    public int data;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.event = this.getVarInt();
        this.position = this.getVector3f();
        this.data = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.event);
        this.putVector3f(this.position);
        this.putVarInt(this.data);
    }
}
