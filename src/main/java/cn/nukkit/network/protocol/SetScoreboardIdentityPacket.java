package cn.nukkit.network.protocol;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class SetScoreboardIdentityPacket extends DataPacket {

	public static final byte TYPE_REGISTER_IDENTITY = 0;
	public static final byte TYPE_CLEAR_IDENTITY = 1;

	public byte type;
	public List<SetScoreboardIdentityEntry> setScoreboardIdentityEntries = new ArrayList<>();

    @Override
    public byte pid() {
        return ProtocolInfo.SET_SCOREBOARD_IDENTITY_PACKET;
    }

    @Override
    public void decode() {
    	this.type = this.getByte();
        for (int i = 0, count = this.getUnsignedVarInt(); i < count; i++) {
			SetScoreboardIdentityEntry setScoreboardIdentityEntry = new SetScoreboardIdentityEntry();
			setScoreboardIdentityEntry.scoreboardId = this.getVarLong();
			if(this.type == TYPE_REGISTER_IDENTITY){
				setScoreboardIdentityEntry.entityUniqueId = this.getEntityUniqueId();
			}
			this.setScoreboardIdentityEntries.add(setScoreboardIdentityEntry)
		}
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.type);
		this.putUnsignedVarInt(this.setScoreboardIdentityEntries.size());
		for (SetScoreboardIdentityEntry setScoreboardIdentityEntry : this.setScoreboardIdentityEntries) {
			this.putVarLong(setScoreboardIdentityEntry.scoreboardId);
			if(this.type == TYPE_REGISTER_IDENTITY){
				this.putEntityUniqueId(setScoreboardIdentityEntry.entityUniqueId);
			}
		}
    }
    
    public static class SetScoreboardIdentityEntry {

        public int scoreboardId;
        public long entityUniqueId;
    }
}
