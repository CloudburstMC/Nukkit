package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class CommandBlockUpdatePacket extends DataPacket {

    public boolean isBlock;
    public int x;
    public int y;
    public int z;
    public int commandBlockMode;
    public boolean isRedstoneMode;
    public boolean isConditional;
    public long minecartEid;
    public String command;
    public String lastOutput;
    public String name;
    public boolean shouldTrackOutput;

    @Override
    public short pid() {
        return ProtocolInfo.COMMAND_BLOCK_UPDATE_PACKET;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        this.isBlock = buffer.readBoolean();
        if (this.isBlock) {
            BlockVector3 v = Binary.readBlockVector3(buffer);
            this.x = v.x;
            this.y = v.y;
            this.z = v.z;
            this.commandBlockMode = (int) Binary.readUnsignedVarInt(buffer);
            this.isRedstoneMode = buffer.readBoolean();
            this.isConditional = buffer.readBoolean();
        } else {
            this.minecartEid = Binary.readEntityRuntimeId(buffer);
        }
        this.command = Binary.readString(buffer);
        this.lastOutput = Binary.readString(buffer);
        this.name = Binary.readString(buffer);
        this.shouldTrackOutput = buffer.readBoolean();
    }

    @Override
    protected void encode(ByteBuf buffer) {
        buffer.writeBoolean(this.isBlock);
        if (this.isBlock) {
            Binary.writeBlockVector3(buffer, this.x, this.y, this.z);
            Binary.writeUnsignedVarInt(buffer, this.commandBlockMode);
            buffer.writeBoolean(this.isRedstoneMode);
            buffer.writeBoolean(this.isConditional);
        } else {
            Binary.writeEntityRuntimeId(buffer, this.minecartEid);
        }
        Binary.writeString(buffer, this.command);
        Binary.writeString(buffer, this.lastOutput);
        Binary.writeString(buffer, this.name);
        buffer.writeBoolean(this.shouldTrackOutput);
    }
}
