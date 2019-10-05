package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class LecternUpdatePacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.LECTERN_UPDATE_PACKET;

    public int page;
    public int totalPages;
    public BlockVector3 blockPosition;
    public boolean dropBook;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        this.page = buffer.readByte();
        this.totalPages = buffer.readByte();
        this.blockPosition = Binary.readBlockVector3(buffer);
        this.dropBook = buffer.readBoolean();
    }

    @Override
    protected void encode(ByteBuf buffer) {
    }
}
