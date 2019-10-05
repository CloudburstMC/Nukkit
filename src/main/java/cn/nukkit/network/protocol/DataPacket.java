package cn.nukkit.network.protocol;

import cn.nukkit.Server;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.Zlib;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class DataPacket implements Cloneable {

    public int senderId;
    public int clientId;

    public abstract short pid();

    public final void tryDecode(ByteBuf buffer) {
        // Already read the packet ID
        this.decode(buffer);
    }

    protected abstract void decode(ByteBuf buffer);

    public final void tryEncode(ByteBuf buffer) {
        Binary.writeUnsignedVarInt(buffer, this.pid());
        this.encode(buffer);
    }

    protected abstract void encode(ByteBuf buffer);

    @Override
    public DataPacket clone() {
        try {
            return (DataPacket) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public BatchPacket compress() {
        return compress(UnpooledByteBufAllocator.DEFAULT);
    }

    public BatchPacket compress(int level) {
        return compress(UnpooledByteBufAllocator.DEFAULT, level);
    }

    public BatchPacket compress(ByteBufAllocator allocator) {
        return compress(allocator, Server.getInstance().networkCompressionLevel);
    }

    public BatchPacket compress(ByteBufAllocator allocator, int level) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.ioBuffer();
        ByteBuf uncompressed = ByteBufAllocator.DEFAULT.ioBuffer();
        try {
            this.tryEncode(buffer);
            Binary.writeVarIntBuffer(uncompressed, buffer);

            ByteBuf compressed = allocator.ioBuffer();
            Zlib.DEFAULT.deflate(uncompressed, compressed, level);
            BatchPacket packet = new BatchPacket();
            packet.payload = compressed;
            return packet;
        } finally {
            buffer.release();
            uncompressed.release();
        }
    }
}
