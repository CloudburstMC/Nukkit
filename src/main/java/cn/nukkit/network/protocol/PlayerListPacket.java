package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.Skin;
import lombok.ToString;

import java.util.UUID;

/**
 * @author Nukkit Project Team
 */
@ToString
public class PlayerListPacket extends DataPacket {

    public static final byte TYPE_ADD = 0;
    public static final byte TYPE_REMOVE = 1;

    public byte type;
    public List<PlayerListEntry> playerListEntries = new ArrayList<>();

    @Override
    public byte pid() {
        return ProtocolInfo.PLAYER_LIST_PACKET;
    }

    @Override
    public void decode() {
    	this.type = this.getByte();
		for (int i = 0, count = this.getUnsignedVarInt(); i < count; i++) {
			PlayerListEntry playerListEntry = new PlayerListEntry();
			playerListEntry.uuid = this.getUUID();
			if (this.type == TYPE_ADD) {
				playerListEntry.entityUniqueId = this.getEntityUniqueId();
				playerListEntry.username = this.getString();
				playerListEntry.xboxUserId = this.getString();
				playerListEntry.platformChatId = this.getString();
				playerListEntry.buildPlatform = this.getLInt();
				playerListEntry.skin = this.getSkin();
				playerListEntry.isTeacher = this.getBoolean();
				playerListEntry.isHost = this.getBoolean();
			}
			this.playerListEntries.add(playerListEntry);
		}
		if (this.type == TYPE_ADD) {
			for (PlayerListEntry playerListEntry : this.playerListEntries) {
				playerListEntry.skin.setTrusted(this.getBoolean());
			}
		}
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.type);
		this.putUnsignedVarInt(this.playerListEntries.size());
		for (PlayerListEntry playerListEntry : this.playerListEntries) {
			this.putUUID(playerListEntry.uuid);
			if (this.type == TYPE_ADD) {
				this.putEntityUniqueId(playerListEntry.entityUniqueId);
				this.putString(playerListEntry.username);
				this.putString(playerListEntry.xboxUserId);
				this.putString(playerListEntry.platformChatId);
				this.putLInt(playerListEntry.buildPlatform);
				this.putSkin(playerListEntry.skin);
				this.putBool(playerListEntry.isTeacher);
				this.putBool(playerListEntry.isHost);
			}
		}
		if(this.type == TYPE_ADD){
			for (PlayerListEntry playerListEntry : this.playerListEntries) {
				this.putBoolean(playerListEntry.skin.isTrusted());
			}
		}
    }

    @ToString
    public static class PlayerListEntry {
    	
        public UUID uuid;
        public long entityUniqueId;
        public String username;
        public String xboxUserId;
        public String platformChatId;
        public int buildPlatform = -1;
        public Skin skin;
        public boolean isTeacher;
        public boolean isHost
    }
}
