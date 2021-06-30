package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class EventPacket extends DataPacket {

    public static final byte EVENT_ACHIEVEMENT_AWARDED = 0;
    public static final byte EVENT_ENTITY_INTERACT = 1;
    public static final byte EVENT_PORTAL_BUILT = 2;
    public static final byte EVENT_PORTAL_USED = 3;
    public static final byte EVENT_MOB_KILLED = 4;
    public static final byte EVENT_CAULDRON_USED = 5;
    public static final byte EVENT_PLAYER_DEATH = 6;
    public static final byte EVENT_BOSS_KILLED = 7;
    public static final byte EVENT_AGENT_COMMAND = 8;
    public static final byte EVENT_AGENT_CREATED = 9;
    public static final byte EVENT_PATTERN_REMOVED = 10; //Idk
    public static final byte EVENT_COMMANED_EXECUTED = 11;
    public static final byte EVENT_FISH_BUCKETED = 12;
    public static final byte EVENT_MOB_BORN = 13;
    public static final byte EVENT_PET_DIED = 14;
    public static final byte EVENT_CAULDRON_BLOCK_USED = 15;
    public static final byte EVENT_COMPOSTER_BLOCK_USED = 16;
    public static final byte EVENT_BELL_BLOCK_USED = 17;
    public static final byte EVENT_ACTOR_DEFINITION = 18;
    public static final byte EVENT_RAID_UPDATE = 19;
    public static final byte EVENT_PLAYER_MOVEMENT_ANOMALY = 20; //Anti-cheat
    public static final byte EVENT_PLAYER_MOVEMENT_CORRECTED = 21;
    public static final byte EVENT_HONEY_HARVESTED = 22;
    public static final byte EVENT_TARGET_BLOCK_HIT = 23;
    public static final byte EVENT_PIGLIN_BARTER = 24;

    public long entityRuntimeId;
    public int eventData;
    public byte eventType;

    @Override
    public byte pid() {
        return ProtocolInfo.EVENT_PACKET;
    }

    @Override
    public void decode() {
        this.entityRuntimeId = this.getEntityRuntimeId();
        this.eventData = this.getVarInt();
        this.event = this.getByte();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putVarInt(this.eventData);
        this.putByte(this.event);
    }
}
