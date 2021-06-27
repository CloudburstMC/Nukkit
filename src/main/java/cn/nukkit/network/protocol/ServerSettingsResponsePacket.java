package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class ServerSettingsResponsePacket extends DataPacket {

    public int formId;
    public String formData;

    @Override
    public byte pid() {
        return ProtocolInfo.SERVER_SETTINGS_RESPONSE_PACKET;
    }

    @Override
    public void decode() {
        this.formId = (int) this.getUnsignedVarInt();
        this.formData = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(this.formId);
        this.putString(this.formData);
    }
}
