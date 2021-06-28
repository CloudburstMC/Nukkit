package cn.nukkit.network.protocol;

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
    public long minecartRuntimeId;
    public String command;
    public String lastOutput;
    public String name;
    public boolean shouldTrackOutput;
    public int tickDelay;
    public boolean executeOnFirstTick;

    @Override
    public byte pid() {
        return ProtocolInfo.COMMAND_BLOCK_UPDATE_PACKET;
    }

    @Override
    public void decode() {
        this.isBlock = this.getBoolean();
        if (this.isBlock) {
            this.getBlockVector3(this.x, this.y, this.z);
            this.commandBlockMode = (int) this.getUnsignedVarInt();
            this.isRedstoneMode = this.getBoolean();
            this.isConditional = this.getBoolean();
        } else {
            this.minecartRuntimeId = this.getEntityRuntimeId();
        }
        this.command = this.getString();
        this.lastOutput = this.getString();
        this.name = this.getString();
        this.shouldTrackOutput = this.getBoolean();
        this.tickDelay = this.getLInt();
        this.executeOnFirstTick = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putBoolean(this.isBlock);
        if (this.isBlock) {
            this.putBlockVector3(this.x, this.y, this.z);
            this.putUnsignedVarInt(this.commandBlockMode);
            this.putBoolean(this.isRedstoneMode);
            this.putBoolean(this.isConditional);
        } else {
            this.putEntityRuntimeId(this.minecartRuntimeId);
        }
        this.putString(this.command);
        this.putString(this.lastOutput);
        this.putString(this.name);
        this.putBoolean(this.shouldTrackOutput);
        this.putLInt(this.tifkDelay);
        this.putBoolean(this.executeOnFirstTick);
    }
}
