package cn.nukkit.network.protocol;

import java.util.UUID;

/**
 * @author Nukkit Project Team
 */
public class PlayerListPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.PLAYER_LIST_PACKET;

    public static final int TYPE_ADD = 0;
    public static final int TYPE_REMOVE = 1;

    public int type;
    public Entry[] entries;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        reset();
        putByte((byte) type);
        putInt(entries.length);
        for (Entry entry : entries) {
            if (type == TYPE_ADD) {
                putUUID(entry.uuid);
                putLong(entry.entityId);
                putString(entry.name);
                putByte((byte) (entry.slim ? 1 : 0));
                putShort(entry.skin.length);
                put(entry.skin);
            } else {
                putUUID(entry.uuid);
            }
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public static class Entry {

        public UUID uuid;
        public long entityId = 0;
        public String name = "";
        public boolean slim = false;
        public byte[] skin = new byte[0];

        public Entry(UUID uuid) {
            this.uuid = uuid;
        }

        public Entry(UUID uuid, long entityId, String name, boolean slim, byte[] skin) {
            this.uuid = uuid;
            this.entityId = entityId;
            this.name = name;
            this.slim = slim;
            this.skin = skin;
        }

    }

}
