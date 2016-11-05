package cn.nukkit.network.protocol;

import cn.nukkit.Server;
import cn.nukkit.command.data.CommandArgs;
import com.google.gson.Gson;

import java.math.BigInteger;

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


    public String command;
    public String overload;
    public long uvarint1;
    public long uvarint2;
    public boolean bool;
    public BigInteger uvarint64;
    public CommandArgs args = new CommandArgs(); //JSON formatted command arguments
    public String string4;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.command = this.getString();
        this.overload = this.getString();
        this.uvarint1 = this.getUnsignedVarInt();
        this.uvarint2 = this.getUnsignedVarInt();
        this.bool = this.getBoolean();
        this.uvarint64 = this.getUnsignedVarLong();
        String argsString = this.getString();
        this.args = new Gson().fromJson(argsString, CommandArgs.class);
        this.string4 = this.getString();
        while (!this.feof()) {
            this.getByte(); //prevent assertion errors. TODO: find out why there are always 3 extra bytes at the end of this packet.
        }

    }

    @Override
    public void encode() {
    }

}
