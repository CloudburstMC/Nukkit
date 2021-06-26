package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class FilterTextPacket extends DataPacket {

    public String text;
    public boolean fromServer;

    @Override
    public byte pid() {
        return ProtocolInfo.FILTER_TEXT_PACKET;
    }

    @Override
    public void decode() {
        this.text = this.getString();
        this.fromServer = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.text);
        this.putBoolean(this.fromServer);
    }
}
