package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class EventPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.EVENT_PACKET;

    public long entityRuntimeId;
    public int eventData;
    public Event event;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.entityRuntimeId = this.getEntityRuntimeId();
        this.eventData = this.getVarInt();
        this.event = Event.values()[this.getByte()];
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putVarInt(this.eventData);
        this.putByte((byte) this.event.ordinal());
    }

    public static enum Event {

        ACHIEVEMENT_AWARDED,
        ENTITY_INTERACT,
        PORTAL_BUILT,
        PORTAL_USED,
        MOB_KILLED,
        CAULDRON_USED,
        PLAYER_DEATH,
        BOSS_KILLED,
        AGENT_COMMAND,
        AGENT_CREATED,
        PATTERN_REMOVED,
        COMMANED_EXECUTED,
        FISH_BUCKETED,
        MOB_BORN,
        PET_DIED,
        CAULDRON_BLOCK_USED,
        COMPOSTER_BLOCK_USED,
        BELL_BLOCK_USED,
        ACTOR_DEFINITION,
        RAID_UPDATE,
        PLAYER_MOVEMENT_ANOMALY,
        PLAYER_MOVEMENT_CORRECTED,
        HONEY_HARVESTED,
        TARGET_BLOCK_HIT,
        PIGLIN_BARTER
    }
}
