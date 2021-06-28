package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.CommandOriginData;
import lombok.ToString;

import java.util.UUID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class CommandRequestPacket extends DataPacket {

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
    public CommandOriginData commandOriginData;
    public boolean isInternal;

    @Override
    public byte pid() {
        return ProtocolInfo.COMMAND_REQUEST_PACKET;
    }

    @Override
    public void decode() {
        this.command = this.getString();
        this.commandOriginData = this.getCommandOriginData();
        this.isInternal = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.command);
        this.putCommandOriginData(this.commandOriginData);
        this.putBoolean(this.isInternal);
    }
}
