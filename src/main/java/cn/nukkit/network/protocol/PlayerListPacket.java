package cn.nukkit.network.protocol;

import java.util.UUID;

/**
 * @author Nukkit Project Team
 */
public class PlayerListPacket extends DataPacket {

    public static final byte NETWORK_ID = Info.PLAYER_LIST_PACKET;

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
        putByte(type);
        putInt(entries.length);
        for (Entry entry : entries) {
            if (type == TYPE_ADD) {
                putUUID(entry.uuid);
                putLong(entry.entityId);
                putString(entry.name);
                putByte(entry.slim ? 1 : 0);
                putString(entry.skin);
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

        private final UUID uuid;
        private final long entityId;
        private final String name;
        private final boolean slim;
        private final String skin;

        public Entry(UUID uuid, long entityId, String name, boolean slim, String skin) {
            this.uuid = uuid;
            this.entityId = entityId;
            this.name = name;
            this.slim = slim;
            this.skin = skin;
        }

    }

}
