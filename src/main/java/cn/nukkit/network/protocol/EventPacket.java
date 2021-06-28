package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class EventPacket extends DataPacket {

    public static final byte TYPE_ACHIEVEMENT_AWARDED = 0;
    public static final byte TYPE_ENTITY_INTERACT = 1;
    public static final byte TYPE_PORTAL_BUILT = 2;
    public static final byte TYPE_PORTAL_USED = 3;
    public static final byte TYPE_MOB_KILLED = 4;
    public static final byte TYPE_CAULDRON_USED = 5;
    public static final byte TYPE_PLAYER_DEATH = 6;
    public static final byte TYPE_BOSS_KILLED = 7;
    public static final byte TYPE_AGENT_COMMAND = 8;
    public static final byte TYPE_AGENT_CREATED = 9;
    public static final byte TYPE_PATTERN_REMOVED = 10; //Idk
    public static final byte TYPE_COMMANED_EXECUTED = 11;
    public static final byte TYPE_FISH_BUCKETED = 12;
    public static final byte TYPE_MOB_BORN = 13;
    public static final byte TYPE_PET_DIED = 14;
    public static final byte TYPE_CAULDRON_BLOCK_USED = 15;
    public static final byte TYPE_COMPOSTER_BLOCK_USED = 16;
    public static final byte TYPE_BELL_BLOCK_USED = 17;
    public static final byte TYPE_ACTOR_DEFINITION = 18;
    public static final byte TYPE_RAID_UPDATE = 19;
    public static final byte TYPE_PLAYER_MOVEMENT_ANOMALY = 20; //Anti-cheat
    public static final byte TYPE_PLAYER_MOVEMENT_CORRECTED = 21;
    public static final byte TYPE_HONEY_HARVESTED = 22;
    public static final byte TYPE_TARGET_BLOCK_HIT = 23;
    public static final byte TYPE_PIGLIN_BARTER = 24;

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
        this.eventType = this.getByte();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putVarInt(this.eventData);
        this.putByte(this.eventType);
    }
}
