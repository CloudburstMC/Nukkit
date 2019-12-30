package cn.nukkit.network.protocol;

import cn.nukkit.pack.Pack;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class ResourcePackStackPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.RESOURCE_PACK_STACK_PACKET;

    public boolean mustAccept = false;
    public final List<Pack> behaviourPackStack = new ArrayList<>();
    public final List<Pack> resourcePackStack = new ArrayList<>();
    public boolean isExperimental = false;
    public String gameVersion = "*"; // Why it sends this, I don't know

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        buffer.writeBoolean(this.mustAccept);

        Binary.writeUnsignedVarInt(buffer, this.behaviourPackStack.size());
        for (Pack entry : this.behaviourPackStack) {
            Binary.writeString(buffer, entry.getId().toString());
            Binary.writeString(buffer, entry.getVersion().toString());
            Binary.writeString(buffer, ""); //TODO: subpack name
        }

        Binary.writeUnsignedVarInt(buffer, this.resourcePackStack.size());
        for (Pack entry : this.resourcePackStack) {
            Binary.writeString(buffer, entry.getId().toString());
            Binary.writeString(buffer, entry.getVersion().toString());
            Binary.writeString(buffer, ""); //TODO: subpack name
        }
        buffer.writeBoolean(this.isExperimental);
        Binary.writeString(buffer, this.gameVersion);
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }
}
