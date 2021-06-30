package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.CommandOriginData;
import cn.nukkit.network.protocol.types.CommandOutputMessage;
import lombok.ToString;

@ToString
public class CommandOutputPacket extends DataPacket {

    public CommandOriginData commandOriginData;
    public OutputType outputType;
    public int successCount;
    public CommandOutputMessage[] entries = new CommandOutputMessage[0];
    public String unknownString;

    @Override
    public byte pid() {
        return ProtocolInfo.COMMAND_OUTPUT_PACKET;
    }

    @Override
    public void decode() {
        this.commandOriginData = this.getCommandOriginData();
        this.outputType = OutputType.values()[this.getByte() - 1];
        this.successCount = (int) this.getUnsignedVarInt();
        int count = (int) this.getUnsignedVarInt();
        this.entries = new CommandOutputMessage[count];
        for (int i = 0; i < count; i++) {
            this.entries[i] = this.getCommandOutputMessage();
        }
        if (this.outputType == OutputType.DATA_SET) {
            this.unknownString = this.getString();
        }
    }

    @Override
    public void encode() {
        this.putCommandOriginData(this.commandOriginData);
        this.putByte((byte) this.outputType.ordinal());
        this.putUnsignedVarInt(this.successCount);
        this.putUnsignedVarInt(this.entries.length);
        for (CommandOutputMessage entry : this.entries) {
            this.putCommandOutputMessage(entry);
        }
        if (this.outputType == OutputType.DATA_SET) {
            this.putString(this.unknownString);
        }
    }

    private CommandOutputMessage getCommandOutputMessage() {
        CommandOutputMessage commandOutputMessage = new CommandOutputMessage();
        commandOutputMessage.isInternal = this.getBoolean();
        commandOutputMessage.messageId = this.getString();
        int count = (int) this.getUnsignedVarInt();
        commandOutputMessage.parameters = new String[count];
        for (int i = 0; i < count; i++) {
            commandOutputMessage.parameters[i] = this.getString();
        }
        return commandOutputMessage;
    }

    private void putCommandOutputMessage(CommandOutputMessage commandOutputMessage) {
        this.putBoolean(commandOutputMessage.isInternal);
        this.putString(commandOutputMessage.messageId);
        this.putUnsignedVarInt(commandOutputMessage.parameters.length);
        for (String parameter : commandOutputMessage.parameters) {
            this.putString(parameter);
        }
    }

    public enum OutputType {

        LAST,
        SILENT,
        ALL,
        DATA_SET
    }
}
