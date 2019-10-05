package cn.nukkit.network.protocol;

import cn.nukkit.resourcepacks.ResourcePack;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class ResourcePackStackPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.RESOURCE_PACK_STACK_PACKET;

    public boolean mustAccept = false;
    public ResourcePack[] behaviourPackStack = new ResourcePack[0];
    public ResourcePack[] resourcePackStack = new ResourcePack[0];
    public boolean isExperimental = false;

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        buffer.writeBoolean(this.mustAccept);

        Binary.writeUnsignedVarInt(buffer, this.behaviourPackStack.length);
        for (ResourcePack entry : this.behaviourPackStack) {
            Binary.writeString(buffer, entry.getPackId().toString());
            Binary.writeString(buffer, entry.getPackVersion());
            Binary.writeString(buffer, ""); //TODO: subpack name
        }

        Binary.writeUnsignedVarInt(buffer, this.resourcePackStack.length);
        for (ResourcePack entry : this.resourcePackStack) {
            Binary.writeString(buffer, entry.getPackId().toString());
            Binary.writeString(buffer, entry.getPackVersion());
            Binary.writeString(buffer, ""); //TODO: subpack name
        }

        buffer.writeBoolean(isExperimental);
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }
}
