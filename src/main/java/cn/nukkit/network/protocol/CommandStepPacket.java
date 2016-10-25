package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CommandStepPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.COMMAND_STEP_PACKET;

    /**
     * unknown (string)
     * unknown (string)
     * unknown (uvarint)
     * unknown (uvarint)
     * unknown (bool)
     * unknown (uvarint64)
     * unknown (string)
     * unknown (string)
     * https://gist.github.com/dktapps/8285b93af4ca38e0104bfeb9a6c87afd
     */

    /*
    public String command;
    public String overload;
    public uvarint1;
    public uvarint2;
    public boolean bool;
    public uvarint64;
    public SimpleConfig args; //JSON formatted command arguments
    public String string4;*/

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(){
        /*  TODO
        this.command = this.getString();
        this.overload = this.getString();
        this.uvarint1 = this.getUnsignedVarInt();
        this.uvarint2 = this.getUnsignedVarInt();
        this.bool = this.getBoolean();
        this.uvarint64 = this.getUnsignedVarInt(); //TODO: varint64
        this.args = new Gson().fromJson(this.getString(), SimpleConfig.class);
        this.string4 = this.getString();
        while(!this.feof()){
            this.getByte(); //prevent assertion errors. TODO: find out why there are always 3 extra bytes at the end of this packet.
        }
        */
    }

    @Override
    public void encode(){}

}
