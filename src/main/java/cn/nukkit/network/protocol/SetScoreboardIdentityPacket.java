package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class SetScoreboardIdentityPacket extends DataPacket {

    public static final byte TYPE_REGISTER_IDENTITY = 0;
    public static final byte TYPE_CLEAR_IDENTITY = 1;

    public byte type;
    public Entry[] entries = new Entry[0];

    @Override
    public byte pid() {
        return ProtocolInfo.SET_SCOREBOARD_IDENTITY_PACKET;
    }

    @Override
    public void decode() {
        this.type = this.getByte();
        int count = (int) this.getUnsignedVarInt();
        this.entries = new Entry[count];
        for (int i = 0; i < count; i++) {
            Entry entry = new Entry();
            entry.scoreboardId = this.getVarLong();
            if (this.type == TYPE_REGISTER_IDENTITY) {
                entry.entityUniqueId = this.getEntityUniqueId();
            }
            this.entries[i] = entry;
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.type);
        this.putUnsignedVarInt(this.entries.length);
        for (Entry entry : this.entries) {
            this.putVarLong(entry.scoreboardId);
            if (this.type == TYPE_REGISTER_IDENTITY) {
                this.putEntityUniqueId(entry.entityUniqueId);
            }
        }
    }

    public static class Entry {

        public long scoreboardId;
        public long entityUniqueId;
    }
}
