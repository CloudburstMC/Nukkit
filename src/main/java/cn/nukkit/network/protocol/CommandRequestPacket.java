package cn.nukkit.network.protocol;

import cn.nukkit.command.data.CommandArgs;
import cn.nukkit.network.protocol.types.CommandOriginData;
import com.google.gson.Gson;

import java.util.UUID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CommandRequestPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.COMMAND_REQUEST_PACKET;

    public static final int TYPE_PLAYER = 0;
    public static final int TYPE_COMMAND_BLOCK = 1;
    public static final int TYPE_MINECART_COMMAND_BLOCK = 2;
    public static final int TYPE_DEV_CONSOLE = 3;
    public static final int TYPE_AUTOMATION_PLAYER = 4;
    public static final int TYPE_CLIENT_AUTOMATION = 5;
    public static final int TYPE_DEDICATED_SERVER = 6;
    public static final int TYPE_ENTITY = 7;
    public static final int TYPE_VIRTUAL = 8;
    public static final int TYPE_GAME_ARGUMENT = 9;
    public static final int TYPE_INTERNAL = 10;

    public String command;
    //1.2.5 origin data
    public CommandOriginData data;
    //1.2.0-1.2.3 easy data
    public int type;
    public String requestId;
    public long playerUniqueId;

    //1.1 harder data
    public String overload;
    public long uvarint1;
    public long currentStep;
    public boolean done;
    public long clientId;
    public CommandArgs args = new CommandArgs(); //JSON formatted command arguments
    public String outputJson;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113) ?
                ProtocolInfo113.COMMAND_STEP_PACKET :
                ProtocolInfo.COMMAND_REQUEST_PACKET;
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        if (protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113)){
            this.command = this.getString();
            this.overload = this.getString();
            this.uvarint1 = this.getUnsignedVarInt();
            this.currentStep = this.getUnsignedVarInt();
            this.done = this.getBoolean();
            this.clientId = this.getVarLong();
            String argsString = this.getString();
            this.args = new Gson().fromJson(argsString, CommandArgs.class);
            this.outputJson = this.getString();
            while (!this.feof()) {
                this.getByte(); //prevent assertion errors
            }
            return;
        }
        this.command = this.getString();
        if (protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_130)){
            this.type = this.getVarInt();
            this.requestId = this.getString();
            this.playerUniqueId = this.getVarLong();
            return;
        }
        CommandOriginData.Origin type = CommandOriginData.Origin.values()[(int) this.getUnsignedVarInt()];
        UUID uuid = this.getUUID(protocol);
        String requestId = this.getString();
        Long varLong = null;
        if (type == CommandOriginData.Origin.DEV_CONSOLE || type == CommandOriginData.Origin.TEST) {
            varLong = this.getVarLong();
        }
        this.data = new CommandOriginData(type, uuid, requestId, varLong);
    }

    @Override
    public void encode(PlayerProtocol protocol) {
    }

}
