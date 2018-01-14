package cn.nukkit.server.network.raknet.util;

import cn.nukkit.server.network.raknet.datagram.EncapsulatedRakNetPacket;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.ReferenceCountUtil;

import java.util.Optional;

public class SplitPacketHelper {
    private final EncapsulatedRakNetPacket[] packets;
    private final long created = System.currentTimeMillis();
    private boolean released = false;

    public SplitPacketHelper(int expectedLength) {
        Preconditions.checkArgument(expectedLength >= 1, "expectedLength is less than 1 (%s)", expectedLength);
        this.packets = new EncapsulatedRakNetPacket[expectedLength];
    }

    public Optional<ByteBuf> add(EncapsulatedRakNetPacket packet) {
        Preconditions.checkNotNull(packet, "packet");
        Preconditions.checkArgument(packet.isHasSplit(), "packet is not split");
        Preconditions.checkState(!released, "packet has been released");
        Preconditions.checkElementIndex(packet.getPartIndex(), packets.length);

        packets[packet.getPartIndex()] = packet;

        int sz = 0;
        for (EncapsulatedRakNetPacket netPacket : packets) {
            if (netPacket == null) {
                return Optional.empty();
            }
            sz += netPacket.getBuffer().readableBytes();
        }

        // We can't use a composite buffer as the native code will choke on it
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.directBuffer(sz);
        for (EncapsulatedRakNetPacket netPacket : packets) {
            buf.writeBytes(netPacket.getBuffer());
        }

        release();
        return Optional.of(buf);
    }

    public boolean expired() {
        // If we're waiting on a split packet for more than 30 seconds, the client on the other end is either severely
        // lagging, or has died.
        Preconditions.checkState(!released, "packet has been released");
        return System.currentTimeMillis() - created >= 30000;
    }

    public void release() {
        Preconditions.checkState(!released, "packet has been released");

        released = true;
        for (int i = 0; i < packets.length; i++) {
            ReferenceCountUtil.release(packets[i]);
            packets[i] = null;
        }
    }
}
