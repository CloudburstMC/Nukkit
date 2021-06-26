package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class TransferPacket extends DataPacket {

    public String address;
    public short port = 19132;

    @Override
    public byte pid() {
        return ProtocolInfo.TRANSFER_PACKET;
    }

    @Override
    public void decode() {
        this.address = this.getString();
        this.port = this.getLShort();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.address);
        this.putLShort(this.port);
    }
}
