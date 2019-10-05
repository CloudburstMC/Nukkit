package cn.nukkit.network.protocol;

import cn.nukkit.command.data.*;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

import java.util.*;
import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class AvailableCommandsPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.AVAILABLE_COMMANDS_PACKET;

    private static final ObjIntConsumer<ByteBuf> WRITE_BYTE = ByteBuf::writeByte;
    private static final ObjIntConsumer<ByteBuf> WRITE_SHORT = ByteBuf::writeShortLE;
    private static final ObjIntConsumer<ByteBuf> WRITE_INT = ByteBuf::writeIntLE;
    private static final ToIntFunction<ByteBuf> READ_BYTE = ByteBuf::readUnsignedByte;
    private static final ToIntFunction<ByteBuf> READ_SHORT = ByteBuf::readUnsignedShortLE;
    private static final ToIntFunction<ByteBuf> READ_INT = ByteBuf::readIntLE;

    public static final int ARG_FLAG_VALID = 0x100000;
    public static final int ARG_FLAG_ENUM = 0x200000;
    public static final int ARG_FLAG_POSTFIX = 0x1000000;
    public static final int ARG_FLAG_SOFT_ENUM = 0x4000000;

    public static final int ARG_TYPE_INT = 1;
    public static final int ARG_TYPE_FLOAT = 2;
    public static final int ARG_TYPE_VALUE = 3;
    public static final int ARG_TYPE_WILDCARD_INT = 4;
    public static final int ARG_TYPE_OPERATOR = 5;
    public static final int ARG_TYPE_TARGET = 6;
    public static final int ARG_TYPE_WILDCARD_TARGET = 7;

    public static final int ARG_TYPE_FILE_PATH = 14;

    public static final int ARG_TYPE_INT_RANGE = 18;

    public static final int ARG_TYPE_STRING = 27;
    public static final int ARG_TYPE_POSITION = 29;

    public static final int ARG_TYPE_MESSAGE = 32;
    public static final int ARG_TYPE_RAWTEXT = 34;
    public static final int ARG_TYPE_JSON = 37;
    public static final int ARG_TYPE_COMMAND = 44;

    public Map<String, CommandDataVersions> commands;
    public final Map<String, List<String>> softEnums = new HashMap<>();

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        commands = new HashMap<>();

        List<String> enumValues = new ArrayList<>();
        List<String> postFixes = new ArrayList<>();
        List<CommandEnum> enums = new ArrayList<>();

        int len = (int) Binary.readUnsignedVarInt(buffer);
        while (len-- > 0) {
            enumValues.add(Binary.readString(buffer));
        }

        len = (int) Binary.readUnsignedVarInt(buffer);
        while (len-- > 0) {
            postFixes.add(Binary.readString(buffer));
        }

        ToIntFunction<ByteBuf> indexReader;
        if (enumValues.size() < 256) {
            indexReader = READ_BYTE;
        } else if (enumValues.size() < 65536) {
            indexReader = READ_SHORT;
        } else {
            indexReader = READ_INT;
        }

        len = (int) Binary.readUnsignedVarInt(buffer);
        while (len-- > 0) {
            String enumName = Binary.readString(buffer);
            int enumLength = (int) Binary.readUnsignedVarInt(buffer);

            List<String> values = new ArrayList<>();

            while (enumLength-- > 0) {
                int index = indexReader.applyAsInt(buffer);

                String enumValue;

                if (index < 0 || (enumValue = enumValues.get(index)) == null) {
                    throw new IllegalStateException("Enum value not found for index " + index);
                }

                values.add(enumValue);
            }

            enums.add(new CommandEnum(enumName, values));
        }

        len = (int) Binary.readUnsignedVarInt(buffer);

        while (len-- > 0) {
            String name = Binary.readString(buffer);
            String description = Binary.readString(buffer);
            int flags = buffer.readByte();
            int permission = buffer.readByte();
            CommandEnum alias = null;

            int aliasIndex = buffer.readIntLE();
            if (aliasIndex >= 0) {
                alias = enums.get(aliasIndex);
            }

            Map<String, CommandOverload> overloads = new HashMap<>();

            int length = (int) Binary.readUnsignedVarInt(buffer);
            while (length-- > 0) {
                CommandOverload overload = new CommandOverload();

                int paramLen = (int) Binary.readUnsignedVarInt(buffer);

                overload.input.parameters = new CommandParameter[paramLen];
                for (int i = 0; i < paramLen; i++) {
                    String paramName = Binary.readString(buffer);
                    int type = buffer.readIntLE();
                    boolean optional = buffer.readBoolean();

                    CommandParameter parameter = new CommandParameter(paramName, optional);


                    if ((type & ARG_FLAG_POSTFIX) != 0) {
                        parameter.postFix = postFixes.get(type & 0xffff);
                    } else if ((type & ARG_FLAG_VALID) == 0) {
                        throw new IllegalStateException("Invalid parameter type received");
                    } else {
                        int index = type & 0xffff;
                        if ((type & ARG_FLAG_ENUM) != 0) {
                            parameter.enumData = enums.get(index);
                        } else if ((type & ARG_FLAG_SOFT_ENUM) != 0) {
                            // TODO: 22/01/2019 soft enums
                        } else {
                            throw new IllegalStateException("Unknown parameter type!");
                        }
                    }

                    overload.input.parameters[i] = parameter;
                }

                overloads.put(Integer.toString(length), overload);
            }

            CommandData data = new CommandData();
            data.aliases = alias;
            data.overloads = overloads;
            data.description = description;
            data.flags = flags;
            data.permission = permission;

            CommandDataVersions versions = new CommandDataVersions();
            versions.versions.add(data);

            this.commands.put(name, versions);
        }
    }

    @Override
    protected void encode(ByteBuf buffer) {

        LinkedHashSet<String> enumValuesSet = new LinkedHashSet<>();
        LinkedHashSet<String> postFixesSet = new LinkedHashSet<>();
        LinkedHashSet<CommandEnum> enumsSet = new LinkedHashSet<>();

        commands.forEach((name, data) -> {
            CommandData cmdData = data.versions.get(0);

            if (cmdData.aliases != null) {
                enumsSet.add(cmdData.aliases);

                enumValuesSet.addAll(cmdData.aliases.getValues());
            }

            for (CommandOverload overload : cmdData.overloads.values()) {
                for (CommandParameter parameter : overload.input.parameters) {
                    if (parameter.enumData != null) {
                        enumsSet.add(parameter.enumData);

                        enumValuesSet.addAll(parameter.enumData.getValues());
                    }

                    if (parameter.postFix != null) {
                        postFixesSet.add(parameter.postFix);
                    }
                }
            }
        });

        List<String> enumValues = new ArrayList<>(enumValuesSet);
        List<CommandEnum> enums = new ArrayList<>(enumsSet);
        List<String> postFixes = new ArrayList<>(postFixesSet);

        Binary.writeUnsignedVarInt(buffer, enumValues.size());
        enumValues.forEach(s -> Binary.writeString(buffer, s));

        Binary.writeUnsignedVarInt(buffer, postFixes.size());
        postFixes.forEach(s -> Binary.writeString(buffer, s));

        ObjIntConsumer<ByteBuf> indexWriter;
        if (enumValues.size() < 256) {
            indexWriter = WRITE_BYTE;
        } else if (enumValues.size() < 65536) {
            indexWriter = WRITE_SHORT;
        } else {
            indexWriter = WRITE_INT;
        }

        Binary.writeUnsignedVarInt(buffer, enums.size());
        enums.forEach((cmdEnum) -> {
            Binary.writeString(buffer, cmdEnum.getName());

            List<String> values = cmdEnum.getValues();
            Binary.writeUnsignedVarInt(buffer, values.size());

            for (String val : values) {
                int i = enumValues.indexOf(val);

                if (i < 0) {
                    throw new IllegalStateException("Enum value '" + val + "' not found");
                }

                indexWriter.accept(buffer, i);
            }
        });

        Binary.writeUnsignedVarInt(buffer, commands.size());

        commands.forEach((name, cmdData) -> {
            CommandData data = cmdData.versions.get(0);

            Binary.writeString(buffer, name);
            Binary.writeString(buffer, data.description);
            buffer.writeByte(data.flags);
            buffer.writeByte(data.permission);

            buffer.writeIntLE(data.aliases == null ? -1 : enums.indexOf(data.aliases));

            Binary.writeUnsignedVarInt(buffer, data.overloads.size());
            for (CommandOverload overload : data.overloads.values()) {
                Binary.writeUnsignedVarInt(buffer, overload.input.parameters.length);

                for (CommandParameter parameter : overload.input.parameters) {
                    Binary.writeString(buffer, parameter.name);

                    int type = 0;
                    if (parameter.postFix != null) {
                        int i = postFixes.indexOf(parameter.postFix);
                        if (i < 0) {
                            throw new IllegalStateException("Postfix '" + parameter.postFix + "' isn't in postfix array");
                        }
                        type = ARG_FLAG_POSTFIX | i;
                    } else {
                        type |= ARG_FLAG_VALID;
                        if (parameter.enumData != null) {
                            type |= ARG_FLAG_ENUM | enums.indexOf(parameter.enumData);
                        } else {
                            type |= parameter.type.getId();
                        }
                    }

                    buffer.writeIntLE(type);
                    buffer.writeBoolean(parameter.optional);
                    buffer.writeByte(parameter.options); // TODO: 19/03/2019 Bit flags. Only first bit is used for GameRules.
                }
            }
        });

        Binary.writeUnsignedVarInt(buffer, softEnums.size());

        softEnums.forEach((name, values) -> {
            Binary.writeString(buffer, name);
            Binary.writeUnsignedVarInt(buffer, values.size());
            values.forEach(s -> Binary.writeString(buffer, s));
        });
    }
}
