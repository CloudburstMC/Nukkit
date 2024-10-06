package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class EventPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.EVENT_PACKET;

    public long eid;
    public int eventData;
    public byte eventType;

    public static final int TYPE_ACHIEVEMENT_AWARDED = 0;
    public static final int TYPE_ENTITY_INTERACT = 1;
    public static final int TYPE_PORTAL_BUILT = 2;
    public static final int TYPE_PORTAL_USED = 3;
    public static final int TYPE_MOB_KILLED = 4;
    public static final int TYPE_CAULDRON_USED = 5;
    public static final int TYPE_PLAYER_DEATH = 6;
    public static final int TYPE_BOSS_KILLED = 7;
    public static final int TYPE_AGENT_COMMAND = 8;
    public static final int TYPE_AGENT_CREATED = 9;
    public static final int TYPE_PATTERN_REMOVED = 10;
    public static final int TYPE_COMMANED_EXECUTED = 11;
    public static final int TYPE_FISH_BUCKETED = 12;
    public static final int TYPE_MOB_BORN = 13;
    public static final int TYPE_PET_DIED = 14;
    public static final int TYPE_CAULDRON_BLOCK_USED = 15;
    public static final int TYPE_COMPOSTER_BLOCK_USED = 16;
    public static final int TYPE_BELL_BLOCK_USED = 17;
    public static final int TYPE_ACTOR_DEFINITION = 18;
    public static final int TYPE_RAID_UPDATE = 19;
    public static final int TYPE_PLAYER_MOVEMENT_ANOMALY = 20;
    public static final int TYPE_PLAYER_MOVEMENT_CORRECTED = 21;
    public static final int TYPE_HONEY_HARVESTED = 22;
    public static final int TYPE_TARGET_BLOCK_HIT = 23;
    public static final int TYPE_PIGLIN_BARTER = 24;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.encodeUnsupported();
    }
}
