package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.Skin;
import lombok.ToString;

import java.util.UUID;

/**
 * @author Nukkit Project Team
 */
@ToString
public class PlayerListPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.PLAYER_LIST_PACKET;

    public static final byte TYPE_ADD = 0;
    public static final byte TYPE_REMOVE = 1;

    public byte type;
    public Entry[] entries = new Entry[0];

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.type = this.getByte();
        int count = (int) this.getUnsignedVarInt();
        this.entries = new Entry[count];
        for (int i = 0; i < count; i++) {
            Entry entry = new Entry();
            entry.uuid = this.getUUID();
            if (this.type == TYPE_ADD) {
                entry.entityUniqueId = this.getEntityUniqueId();
                entry.username = this.getString();
                entry.xboxUserId = this.getString();
                entry.platformChatId = this.getString();
                entry.buildPlatform = this.getLInt();
                entry.skin = this.getSkin();
                entry.isTeacher = this.getBoolean();
                entry.isHost = this.getBoolean();
            }
            this.entries[i] = entry;
        }
        if (this.type == TYPE_ADD) {
            for (int i = 0; i < count; i++) {
                this.entries[i].skin.setTrusted(this.getBoolean());
            }
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.type);
        this.putUnsignedVarInt(this.entries.length);
        for (Entry entry : this.entries) {
            this.putUUID(entry.uuid);
            if (this.type == TYPE_ADD) {
                this.putEntityUniqueId(entry.entityUniqueId);
                this.putString(entry.username);
                this.putString(entry.xboxUserId);
                this.putString(entry.platformChatId);
                this.putLInt(entry.buildPlatform);
                this.putSkin(entry.skin);
                this.putBoolean(entry.isTeacher);
                this.putBoolean(entry.isHost);
            }
        }
        if (this.type == TYPE_ADD) {
            for (Entry entry : this.entries) {
                this.putBoolean(entry.skin.isTrusted());
            }
        }
    }

    @ToString
    public static class Entry {

        public UUID uuid;
        public long entityUniqueId;
        public String username = "";
        public String xboxUserId = "";
        public String platformChatId = "";
        public int buildPlatform = -1;
        public Skin skin;
        public boolean isTeacher;
        public boolean isHost

        public Entry(UUID uuid) {
            this.uuid = uuid;
        }

        public Entry(UUID uuid, long entityUniqueId, String username, Skin skin) {
            this(uuid, entityUniqueId, username, skin, "");
        }

        public Entry(UUID uuid, long entityUniqueId, String username, Skin skin, String xboxUserId) {
            this.uuid = uuid;
            this.entityUniqueId = entityUniqueId;
            this.username = username;
            this.skin = skin;
            this.xboxUserId = xboxUserId == null ? "" : xboxUserId;
        }
    }
}
