package cn.nukkit.network.protocol;

import cn.nukkit.Server;
import cn.nukkit.entity.data.Skin;
import lombok.ToString;

import java.awt.*;
import java.util.UUID;

/**
 * @author Nukkit Project Team
 */
@ToString
public class PlayerListPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.PLAYER_LIST_PACKET;

    public static final byte TYPE_ADD = 0;
    public static final byte TYPE_REMOVE = 1;

    @Deprecated
    public byte type = -1; // for legacy reasons allow using this to override type of all entries
    public Entry[] entries = new Entry[0];

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();

        if (this.entries.length == 0) {
            this.putUnsignedVarInt(0);
            Server.getInstance().getLogger().debug("PlayerListPacket with no entries");
            return;
        }

        this.putUnsignedVarInt(this.entries.length);

        for (Entry entry : this.entries) {
            byte entryType = this.type == -1 ? entry.type : this.type;
            this.putUnsignedVarInt(entryType == TYPE_ADD ? 1 : 0);

            switch (entryType) {
                case TYPE_ADD:
                    this.putByte(entryType);
                    this.putUUID(entry.uuid);
                    this.putVarLong(entry.entityId);
                    this.putString(entry.name);
                    this.putString(entry.xboxUserId);
                    this.putString(entry.platformChatId);
                    this.putLInt(entry.buildPlatform);
                    this.putSkin(entry.skin);
                    this.putBoolean(entry.isTeacher);
                    this.putBoolean(entry.isHost);
                    this.putBoolean(entry.isSubClient);
                    this.putLInt(entry.color.getRGB());
                    break;
                case TYPE_REMOVE:
                    this.putByte(entryType);
                    this.putUUID(entry.uuid);
                    break;
                default:
                    throw new IllegalArgumentException("entryType: " + entryType);
            }
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @ToString
    public static class Entry {

        public byte type;
        public final UUID uuid;
        public long entityId;
        public String name = "";
        public Skin skin;
        public String xboxUserId = "";
        public String platformChatId = "";
        public int buildPlatform = 1;
        public boolean isTeacher;
        public boolean isHost;
        public boolean isSubClient;
        public Color color;

        public Entry(UUID uuid) {
            this.type = TYPE_REMOVE;
            this.uuid = uuid;
        }

        public Entry(UUID uuid, long entityId, String name, Skin skin) {
            this(uuid, entityId, name, skin, "");
        }

        public Entry(UUID uuid, long entityId, String name, Skin skin, String xboxUserId) {
            this(uuid, entityId, name, skin, xboxUserId, Color.WHITE);
        }

        public Entry(UUID uuid, long entityId, String name, Skin skin, String xboxUserId, Color color) {
            this.type = TYPE_ADD;
            this.uuid = uuid;
            this.entityId = entityId;
            this.name = name;
            this.skin = skin;
            this.xboxUserId = xboxUserId == null ? "" : xboxUserId;
            this.color = color;
        }
    }
}
