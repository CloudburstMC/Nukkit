package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class SetScoreboardIdentityPacket extends DataPacket {

    public static final byte TYPE_REGISTER_IDENTITY = 0;
    public static final byte TYPE_CLEAR_IDENTITY = 1;

    public byte type;
    public SetScoreboardIdentityEntry[] entries = new SetScoreboardIdentityEntry[0]

    @Override
    public byte pid() {
        return ProtocolInfo.SET_SCOREBOARD_IDENTITY_PACKET;
    }

    @Override
    public void decode() {
        this.type = this.getByte();
        int count = (int) this.getUnsignedVarInt();
        for (int i = 0; i < count; i++) {
            SetScoreboardIdentityEntry setScoreboardIdentityEntry = new SetScoreboardIdentityEntry();
            setScoreboardIdentityEntry.scoreboardId = this.getVarLong();
            if (this.type == TYPE_REGISTER_IDENTITY) {
                setScoreboardIdentityEntry.entityUniqueId = this.getEntityUniqueId();
            }
            this.entries[i] = setScoreboardIdentityEntry;
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.type);
        this.putUnsignedVarInt(this.setScoreboardIdentityEntries.length);
        for (SetScoreboardIdentityEntry setScoreboardIdentityEntry : this.entries) {
            this.putVarLong(setScoreboardIdentityEntry.scoreboardId);
            if (this.type == TYPE_REGISTER_IDENTITY) {
                this.putEntityUniqueId(setScoreboardIdentityEntry.entityUniqueId);
            }
        }
    }

    public static class SetScoreboardIdentityEntry {

        public long scoreboardId;
        public long entityUniqueId;
    }
}
