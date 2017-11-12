package cn.nukkit.network.protocol;

import cn.nukkit.command.data.CommandDataVersions;
import cn.nukkit.command.data.CommandEnum;
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

    public Map<String, CommandDataVersions> commands;
    public String jsonCommands;

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
    public byte pid(PlayerProtocol protocol) {
        return protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113) ?
                ProtocolInfo113.AVAILABLE_COMMANDS_PACKET :
                ProtocolInfo.AVAILABLE_COMMANDS_PACKET;
    }

    @Override
    public void decode(PlayerProtocol protocol) {
    }

    int aliasCommands = 0;
    private ArrayList<String> enum_values = new ArrayList<>();
    private ArrayList<CommandEnum> enums = new ArrayList<>();

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        if (protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113)){
            this.putString(this.jsonCommands);
            this.putString("");
            return;
        }
        BinaryStream commandsStream = new BinaryStream();
        this.commands.forEach((name, versions) -> {
            if (name.equals("help")) return;
            ArrayList<String> aliases = new ArrayList<>();
            aliases.addAll(Arrays.asList(versions.versions.get(0).aliases));
            aliases.add(name);
            for (String alias : aliases) {
                commandsStream.putString(alias); //name
                commandsStream.putString(versions.versions.get(0).description); //description
                commandsStream.putByte((byte) 0); //?
                commandsStream.putByte((byte) 0); //?
                commandsStream.putLInt(-1); //aliases enum
                commandsStream.putUnsignedVarInt(versions.versions.get(0).overloads.size());
                for (CommandOverload overload : versions.versions.get(0).overloads.values()) {
                    commandsStream.putUnsignedVarInt(overload.input.parameters.length);
                    for (CommandParameter parameter : overload.input.parameters) {
                        commandsStream.putString(parameter.name);
                        if (parameter.enum_values == null || parameter.enum_values.length == 0){
                            commandsStream.putLInt(ARG_FLAG_VALID | getFlag(parameter.type));
                        }
                        else {
                            ArrayList<Integer> enumValuesIndexes = new ArrayList<>();
                            for (String enumValue : parameter.enum_values){
                                if (this.enum_values.contains(enumValue)) {
                                    enumValuesIndexes.add(this.enum_values.indexOf(enumValue));
                                }
                                else {
                                    enumValuesIndexes.add(this.enum_values.size());
                                    this.enum_values.add(enumValue);
                                }
                            }
                            CommandEnum commandEnum = new CommandEnum(parameter.name, enumValuesIndexes);
                            int enumIndex = this.enums.size();
                            if (!this.enums.contains(commandEnum)) {
                                this.enums.add(commandEnum);
                            }
                            else {
                                enumIndex = this.enums.indexOf(commandEnum);
                                CommandEnum previous = this.enums.get(enumIndex);
                                commandEnum.getEnumIndexes().forEach(actualIndex -> {
                                    if (!previous.getEnumIndexes().contains(actualIndex))
                                        previous.getEnumIndexes().add(actualIndex);
                                });
                            }
                            commandsStream.putLInt(ARG_FLAG_ENUM | ARG_FLAG_VALID | enumIndex);
                        }
                        commandsStream.putBoolean(parameter.optional);
                    }
                }
            }
            aliasCommands += aliases.size()-1;
        });
        this.putUnsignedVarInt(this.enum_values.size()); //String[]
        this.enum_values.forEach(this::putString);
        this.putUnsignedVarInt(0); //Template[]
        this.putUnsignedVarInt(this.enums.size()); //Enum[]
        this.enums.forEach(commandEnum -> {
            this.putString(commandEnum.getName());
            this.putUnsignedVarInt(commandEnum.getEnumIndexes().size());
            commandEnum.getEnumIndexes().forEach(index -> {
                if (index <= Byte.MAX_VALUE) this.putByte(index.byteValue());
                else if (index <= Short.MAX_VALUE) this.putLShort(index);
                else this.putInt(index);
            });
        });
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
            case CommandParameter.ARG_TYPE_TEXT:
                return ARG_TYPE_TEXT;
            case CommandParameter.ARG_TYPE_STRING:
            case CommandParameter.ARG_TYPE_STRING_ENUM:
                return ARG_TYPE_STRING;
            default:
                return 0;
        }
    }
}
