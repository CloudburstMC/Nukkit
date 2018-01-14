package cn.nukkit.server.network.raknet.datagram;

import cn.nukkit.server.network.raknet.session.RakNetSession;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCounted;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static cn.nukkit.server.network.raknet.RakNetUtil.MAX_ENCAPSULATED_HEADER_SIZE;
import static cn.nukkit.server.network.raknet.RakNetUtil.MAX_MESSAGE_HEADER_SIZE;

@Data
public class EncapsulatedRakNetPacket implements ReferenceCounted {
    private RakNetReliability reliability;
    private int reliabilityNumber;
    private int sequenceIndex;
    private int orderingIndex;
    private byte orderingChannel;
    private boolean hasSplit;
    private int partCount;
    private short partId;
    private int partIndex;
    private ByteBuf buffer;

    public static List<EncapsulatedRakNetPacket> encapsulatePackage(ByteBuf buffer, RakNetSession session, boolean isOrdered) {
        // Potentially split the package.
        List<ByteBuf> bufs = new ArrayList<>();
        int by = session.getMtu() - MAX_ENCAPSULATED_HEADER_SIZE - MAX_MESSAGE_HEADER_SIZE;
        if (buffer.readableBytes() > by) {
            // Packet requires splitting
            ByteBuf from = buffer.slice();
            int split = ((buffer.readableBytes() - 1) / by) + 1;
            for (int i = 0; i < split; i++) {
                bufs.add(from.readSlice(Math.min(by, from.readableBytes())));
            }
        } else {
            // No splitting required.
            bufs.add(buffer);
        }

        // Now create the packets.
        List<EncapsulatedRakNetPacket> packets = new ArrayList<>();
        short splitId = (short) (System.nanoTime() % Short.MAX_VALUE);
        int orderNumber = isOrdered ? session.getOrderSequenceGenerator().getAndIncrement() : 0;
        for (int i = 0; i < bufs.size(); i++) {
            // Encryption requires RELIABLE_ORDERED
            EncapsulatedRakNetPacket packet = new EncapsulatedRakNetPacket();
            packet.setBuffer(bufs.get(i));
            packet.setReliability(isOrdered ? RakNetReliability.RELIABLE_ORDERED : RakNetReliability.RELIABLE);
            packet.setReliabilityNumber(session.getReliabilitySequenceGenerator().getAndIncrement());
            packet.setOrderingIndex(orderNumber);
            if (bufs.size() > 1) {
                packet.setHasSplit(true);
                packet.setPartIndex(i);
                packet.setPartCount(bufs.size());
                packet.setPartId(splitId);
            }
            packets.add(packet);
        }
        return packets;
    }

    public void encode(ByteBuf buf) {
        int flags = reliability.ordinal();
        buf.writeByte((byte) ((flags << 5) | (hasSplit ? 0b00010000 : 0x00))); // flags
        buf.writeShort(buffer.readableBytes() * 8); // size

        if (reliability == RakNetReliability.RELIABLE || reliability == RakNetReliability.RELIABLE_ORDERED ||
                reliability == RakNetReliability.RELIABLE_SEQUENCED || reliability == RakNetReliability.RELIABLE_WITH_ACK_RECEIPT ||
                reliability == RakNetReliability.RELIABLE_ORDERED_WITH_ACK_RECEIPT) {
            buf.writeMediumLE(reliabilityNumber);
        }

        if (reliability == RakNetReliability.UNRELIABLE_SEQUENCED || reliability == RakNetReliability.RELIABLE_SEQUENCED) {
            buf.writeMediumLE(sequenceIndex);
        }

        if (reliability == RakNetReliability.UNRELIABLE_SEQUENCED || reliability == RakNetReliability.RELIABLE_SEQUENCED ||
                reliability == RakNetReliability.RELIABLE_ORDERED || reliability == RakNetReliability.RELIABLE_ORDERED_WITH_ACK_RECEIPT) {
            buf.writeMediumLE(orderingIndex);
            buf.writeByte(orderingChannel);
        }

        if (hasSplit) {
            buf.writeInt(partCount);
            buf.writeShort(partId);
            buf.writeInt(partIndex);
        }

        buf.writeBytes(buffer);
    }

    public void decode(ByteBuf buf) {
        short flags = buf.readUnsignedByte();
        reliability = RakNetReliability.values()[((flags & 0b11100000) >> 5)];
        hasSplit = ((flags & 0b00010000) > 0);
        short size = (short) Math.ceil(buf.readShort() / 8D);

        if (reliability == RakNetReliability.RELIABLE || reliability == RakNetReliability.RELIABLE_ORDERED ||
                reliability == RakNetReliability.RELIABLE_SEQUENCED || reliability == RakNetReliability.RELIABLE_WITH_ACK_RECEIPT ||
                reliability == RakNetReliability.RELIABLE_ORDERED_WITH_ACK_RECEIPT) {
            reliabilityNumber = buf.readUnsignedMediumLE();
        }

        if (reliability == RakNetReliability.UNRELIABLE_SEQUENCED || reliability == RakNetReliability.RELIABLE_SEQUENCED) {
            sequenceIndex = buf.readUnsignedMediumLE();
        }

        if (reliability == RakNetReliability.UNRELIABLE_SEQUENCED || reliability == RakNetReliability.RELIABLE_SEQUENCED ||
                reliability == RakNetReliability.RELIABLE_ORDERED || reliability == RakNetReliability.RELIABLE_ORDERED_WITH_ACK_RECEIPT) {
            orderingIndex = buf.readUnsignedMediumLE();
            orderingChannel = buf.readByte();
        }

        if (hasSplit) {
            partCount = buf.readInt();
            partId = buf.readShort();
            partIndex = buf.readInt();
        }

        buffer = buf.readBytes(size);
    }

    public ByteBuf getBuffer() {
        return buffer;
    }

    public void setBuffer(ByteBuf buffer) {
        this.buffer = buffer;
    }

    public int totalLength() {
        // Back of the envelope calculation, YMMV
        return buffer.writerIndex() + 24;
    }

    @Override
    public int refCnt() {
        return buffer.refCnt();
    }

    @Override
    public ReferenceCounted retain() {
        return buffer.retain();
    }

    @Override
    public ReferenceCounted retain(int i) {
        return buffer.retain(i);
    }

    @Override
    public ReferenceCounted touch() {
        return buffer.touch();
    }

    @Override
    public ReferenceCounted touch(Object o) {
        return buffer.touch(o);
    }

    @Override
    public boolean release() {
        return buffer.release();
    }

    @Override
    public boolean release(int i) {
        return buffer.release(i);
    }
}

