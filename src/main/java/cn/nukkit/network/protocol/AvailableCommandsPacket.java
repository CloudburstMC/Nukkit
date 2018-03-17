package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.command.data.CommandDataVersions;
import cn.nukkit.command.data.CommandOverload;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.BinaryStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class AvailableCommandsPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.AVAILABLE_COMMANDS_PACKET;
    public Map<String, CommandDataVersions> commands;

    public static final int ARG_FLAG_VALID = 0x100000;
    public static final int ARG_TYPE_INT = 0x01;
    public static final int ARG_TYPE_FLOAT = 0x02;
    public static final int ARG_TYPE_VALUE = 0x03;
    public static final int ARG_TYPE_TARGET = 0x04;

    public static final int ARG_TYPE_STRING = 0x0d;
    public static final int ARG_TYPE_POSITION = 0x0e;

    public static final int ARG_TYPE_RAWTEXT = 0x11;
    public static final int ARG_TYPE_TEXT = 0x13;

    public static final int ARG_TYPE_JSON = 0x16;
    public static final int ARG_TYPE_COMMAND = 0x1d;

    public static final int ARG_FLAG_ENUM = 0x200000;
    public static final int ARG_FLAG_TEMPLATE = 0x01000000;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
    }

    int aliasCommands = 0;

    @Override
    public void encode() {
        this.reset();
        BinaryStream commandsStream = new BinaryStream();
        this.commands.forEach((name, versions) -> {
            if (name.equals("help")) return;
            ArrayList<String> aliases = new ArrayList<>();
            aliases.addAll(Arrays.asList(versions.versions.get(0).aliases));
            aliases.add(name);
            for (String alias : aliases) {
                commandsStream.putString(alias);
                commandsStream.putString(versions.versions.get(0).description);
                commandsStream.putByte((byte) 0);
                commandsStream.putByte((byte) 0);
                commandsStream.putLInt(-1);
                commandsStream.putUnsignedVarInt(versions.versions.get(0).overloads.size());
                for (CommandOverload overload : versions.versions.get(0).overloads.values()) {
                    commandsStream.putUnsignedVarInt(overload.input.parameters.length);
                    for (CommandParameter parameter : overload.input.parameters) {
                        commandsStream.putString(parameter.name);
                        commandsStream.putLInt(ARG_FLAG_VALID | getFlag(parameter.type));
                        commandsStream.putBoolean(parameter.optional);
                    }
                }
            }
            aliasCommands += (aliases.size() - 1);
        });

        this.putUnsignedVarInt(0);
        this.putUnsignedVarInt(0);
        this.putUnsignedVarInt(0);
        this.putUnsignedVarInt(this.commands.size() + aliasCommands);
        this.put(commandsStream.getBuffer());
    }

    int getFlag(String type) {
        switch (type) {
            case CommandParameter.ARG_TYPE_BLOCK_POS:
                return ARG_TYPE_POSITION;
            case CommandParameter.ARG_TYPE_INT:
                return ARG_TYPE_INT;
            case CommandParameter.ARG_TYPE_PLAYER:
                return ARG_TYPE_TARGET;
            case CommandParameter.ARG_TYPE_RAW_TEXT:
                return ARG_TYPE_RAWTEXT;
            case CommandParameter.ARG_TYPE_STRING:
            case CommandParameter.ARG_TYPE_STRING_ENUM:
                return ARG_TYPE_STRING;
            default:
                return 0;
        }
    }

    @Override
    protected void handle(Player player) {
        player.handle(this);
    }
}
