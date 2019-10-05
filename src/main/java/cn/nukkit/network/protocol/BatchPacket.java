package cn.nukkit.network.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCounted;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BatchPacket extends DataPacket implements ReferenceCounted {
    public static final short NETWORK_ID = ProtocolInfo.BATCH_PACKET;

    public ByteBuf payload;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(ByteBuf buffer) {
        this.payload = buffer.readRetainedSlice(buffer.readableBytes());
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeBytes(this.payload);
    }

    @Override
    public int refCnt() {
        return payload.refCnt();
    }

    @Override
    public BatchPacket retain() {
        this.payload.retain();
        return this;
    }

    @Override
    public BatchPacket retain(int increment) {
        this.payload.retain(increment);
        return this;
    }

    @Override
    public BatchPacket touch() {
        this.payload.touch();
        return this;
    }

    @Override
    public BatchPacket touch(Object hint) {
        this.payload.touch(hint);
        return this;
    }

    @Override
    public boolean release() {
        return payload.release();
    }

    @Override
    public boolean release(int decrement) {
        return payload.release(decrement);
    }

    public BatchPacket copy() {
        BatchPacket batchPacket = new BatchPacket();
        batchPacket.payload = this.payload.retainedDuplicate();
        return batchPacket;
    }
}
