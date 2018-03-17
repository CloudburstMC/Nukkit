package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.resourcepacks.ResourcePack;

public class ResourcePackClientResponsePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET;

    public static final byte STATUS_REFUSED = 1;
    public static final byte STATUS_SEND_PACKS = 2;
    public static final byte STATUS_HAVE_ALL_PACKS = 3;
    public static final byte STATUS_COMPLETED = 4;

    public byte responseStatus;
    public String[] packIds;

    @Override
    public void decode() {
        this.responseStatus = (byte) this.getByte();
        this.packIds = new String[this.getLShort()];
        for (int i = 0; i < this.packIds.length; i++) {
            this.packIds[i] = this.getString();
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.responseStatus);
        this.putLShort(this.packIds.length);
        for (String id : this.packIds) {
            this.putString(id);
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void handle(Player player) {
        player.handle(this);
    }
}
