package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import lombok.ToString;

@ToString
public class LecternUpdatePacket extends DataPacket {

    public byte page;
    public byte totalPages;
    public BlockVector3 blockPosition;
    public boolean dropBook;

    @Override
    public byte pid() {
        return ProtocolInfo.LECTERN_UPDATE_PACKET;
    }

    @Override
    public void decode() {
        this.page = this.getByte();
        this.totalPages = this.getByte();
        this.blockPosition = this.getBlockVector3();
        this.dropBook = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.page);
        this.putByte(this.totalPages);
        this.putBlockVector3(this.blockPosition);
        this.putBool(this.dropBook);
    }
}
