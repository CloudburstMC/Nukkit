package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.Skin;

import java.util.UUID;

/**
 * @author Nukkit Project Team
 */
public class PlayerListPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.PLAYER_LIST_PACKET;

    public static final byte TYPE_ADD = 0;
    public static final byte TYPE_REMOVE = 1;

    public byte type;
    public Entry[] entries = new Entry[0];

    @Override
    public void decode() {
        this.type = (byte) this.getByte();
        this.entries = new Entry[(int) this.getUnsignedVarInt()];
        for(int i = 0; i < this.entries.length; i++) {
            this.entries[i] = new Entry(this.getUUID());
            if(this.type == TYPE_ADD) {
                this.entries[i].entityId = this.getVarLong();
                this.entries[i].name = this.getString();
                this.entries[i].skin = this.getSkin();
                this.entries[i].xboxUserId = this.getString();
                this.entries[i].platformChatId = this.getString();
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
            if (type == TYPE_ADD) {
                this.putVarLong(entry.entityId);
                this.putString(entry.name);
                this.putSkin(entry.skin);
                this.putString(entry.xboxUserId);
                this.putString(entry.platformChatId);
            }
        }

    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public static class Entry {

        public final UUID uuid;
        public long entityId = 0;
        public String name = "";
        public Skin skin;
        public String xboxUserId = ""; //TODO
        public String platformChatId = ""; //TODO

        public Entry(UUID uuid) {
            this.uuid = uuid;
        }

        public Entry(UUID uuid, long entityId, String name, Skin skin) {
            this(uuid, entityId, name, skin, "");
        }

        public Entry(UUID uuid, long entityId, String name, Skin skin, String xboxUserId) {
            this.uuid = uuid;
            this.entityId = entityId;
            this.name = name;
            this.skin = skin;
            this.xboxUserId = xboxUserId == null ? "" : xboxUserId;
        }
    }

}
