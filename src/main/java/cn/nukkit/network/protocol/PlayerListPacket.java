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
                this.putString(entry.thirdPartyName);
                this.putVarInt(entry.platformId);
                this.putSkin(entry.skin);
                if (entry.skin.getCape().getData().length == 0) {
                    this.putLInt(0); //isEmpty
                } else {
                    this.putLInt(1); //isEmpty
                    this.putByteArray(entry.skin.getCape().getData());
                }
                this.putString(entry.geometryModel);
                this.putByteArray(entry.geometryData);
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
        public String thirdPartyName;
        public int platformId;
        public Skin skin;
        public byte[] capeData = new byte[0]; //TODO
        public String geometryModel = "";
        public byte[] geometryData = new byte[0]; //TODO
        public String xboxUserId = ""; //TODO
        public String platformChatId;

        public Entry(UUID uuid) {
            this.uuid = uuid;
        }

        public Entry(UUID uuid, long entityId, String name, Skin skin) {
            this(uuid, entityId, name, "", 0, skin, "", "");
        }

        public Entry(UUID uuid, long entityId, String name, Skin skin, String xboxUserId) {
            this(uuid, entityId, name, "", 0, skin, xboxUserId, "");
        }

        public Entry(UUID uuid, long entityId, String name, String thirdPartyName, int platformId, Skin skin, String xboxUserId, String platformChatId) {
            this.uuid = uuid;
            this.entityId = entityId;
            this.name = name;
            this.thirdPartyName = thirdPartyName == null ? "" : thirdPartyName;
            this.platformId = platformId;
            this.skin = skin;
            this.capeData = skin.getCape().getData();
            this.xboxUserId = xboxUserId == null ? "" : xboxUserId;
            this.platformChatId = platformChatId == null ? "" : platformChatId;
        }
    }

}
