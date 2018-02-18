package cn.nukkit.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class HurtArmorPacket extends DataPacket {

    public int health;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("HURT_ARMOR_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {

    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putVarInt(this.health);
    }

}
