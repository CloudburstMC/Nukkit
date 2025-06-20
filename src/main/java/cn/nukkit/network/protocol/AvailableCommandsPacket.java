package cn.nukkit.network.protocol;

import cn.nukkit.command.data.*;
import cn.nukkit.utils.BinaryStream;
import lombok.ToString;

import java.util.*;
import java.util.function.ObjIntConsumer;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
@ToString
public class AvailableCommandsPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.AVAILABLE_COMMANDS_PACKET;

    private static final ObjIntConsumer<BinaryStream> WRITE_BYTE = (s, v) -> s.putByte((byte) v);
    private static final ObjIntConsumer<BinaryStream> WRITE_SHORT = BinaryStream::putLShort;
    private static final ObjIntConsumer<BinaryStream> WRITE_INT = BinaryStream::putLInt;

    public static final int ARG_FLAG_VALID = 0x100000;
    public static final int ARG_FLAG_ENUM = 0x200000;
    public static final int ARG_FLAG_POSTFIX = 0x1000000;
    public static final int ARG_FLAG_SOFT_ENUM = 0x4000000;

    public static final int ARG_TYPE_INT = 1;
    public static final int ARG_TYPE_FLOAT = 3;
    public static final int ARG_TYPE_VALUE = 4;
    public static final int ARG_TYPE_WILDCARD_INT = 5;
    public static final int ARG_TYPE_OPERATOR = 6;
    public static final int ARG_TYPE_COMPARE_OPERATOR = 7;
    public static final int ARG_TYPE_TARGET = 8;
    public static final int ARG_TYPE_WILDCARD_TARGET = 10;
    public static final int ARG_TYPE_FILE_PATH = 17;
    public static final int ARG_TYPE_FULL_INTEGER_RANGE = 23;
    public static final int ARG_TYPE_EQUIPMENT_SLOT = 47;
    public static final int ARG_TYPE_STRING = 56;
    public static final int ARG_TYPE_BLOCK_POSITION = 64;
    public static final int ARG_TYPE_POSITION = 65;
    public static final int ARG_TYPE_MESSAGE = 68;
    public static final int ARG_TYPE_RAWTEXT = 70;
    public static final int ARG_TYPE_JSON = 74;
    public static final int ARG_TYPE_BLOCK_STATES = 84;
    public static final int ARG_TYPE_COMMAND = 87;

    public Map<String, CommandDataVersions> commands;
    public final Map<String, List<String>> softEnums = new HashMap<>();

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

        ObjIntConsumer<BinaryStream> indexWriter;
        if (enumValues.size() < 256) {
            indexWriter = WRITE_BYTE;
        } else if (enumValues.size() < 65536) {
            indexWriter = WRITE_SHORT;
        } else {
            indexWriter = WRITE_INT;
        }

        this.putUnsignedVarInt(enumValues.size());
        enumValues.forEach(this::putString);

        this.putUnsignedVarInt(0); //subCommandValues

        this.putUnsignedVarInt(postFixes.size());
        postFixes.forEach(this::putString);

        this.putUnsignedVarInt(enums.size());
        enums.forEach((cmdEnum) -> {
            putString(cmdEnum.getName());

            List<String> values = cmdEnum.getValues();
            putUnsignedVarInt(values.size());

            for (String val : values) {
                int i = enumValues.indexOf(val);

                if (i < 0) {
                    throw new IllegalStateException("Enum value '" + val + "' not found");
                }

                indexWriter.accept(this, i);
            }
        });

        this.putUnsignedVarInt(0); //subCommandData

        this.putUnsignedVarInt(commands.size());
        commands.forEach((name, cmdData) -> {
            CommandData data = cmdData.versions.get(0);

            putString(name);
            putString(data.description);
            putLShort(data.flags);
            putByte((byte) data.permission);

            putLInt(data.aliases == null ? -1 : enums.indexOf(data.aliases));

            putUnsignedVarInt(0); //subcommands

            putUnsignedVarInt(data.overloads.size());
            for (CommandOverload overload : data.overloads.values()) {
                putBoolean(false); //isChaining
                putUnsignedVarInt(overload.input.parameters.length);

                for (CommandParameter parameter : overload.input.parameters) {
                    putString(parameter.name);

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
                            int i = enums.indexOf(parameter.enumData);
                            if (i < 0) {
                                throw new IllegalStateException("Enum '" + parameter.enumData.getName() + "' isn't in enums array");
                            }
                            type |= ARG_FLAG_ENUM | i;
                        } else {
                            type |= parameter.type.getId();
                        }
                    }

                    putLInt(type);
                    putBoolean(parameter.optional);
                    putByte(parameter.options);
                }
            }
        });

        this.putUnsignedVarInt(softEnums.size());

        softEnums.forEach((name, values) -> {
            this.putString(name);
            this.putUnsignedVarInt(values.size());
            values.forEach(this::putString);
        });

        this.putUnsignedVarInt(0); //enumConstraints
    }
}
