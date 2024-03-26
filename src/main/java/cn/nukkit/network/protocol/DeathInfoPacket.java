package cn.nukkit.network.protocol;

public class DeathInfoPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.DEATH_INFO_PACKET;

    public String messageTranslationKey;
    public String[] messageParameters = new String[0];

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.messageTranslationKey);
        this.putUnsignedVarInt(this.messageParameters.length);
        for (String parameter : this.messageParameters) {
            this.putString(parameter);
        }
    }
}
