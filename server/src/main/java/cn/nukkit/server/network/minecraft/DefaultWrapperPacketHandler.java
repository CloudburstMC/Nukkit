package cn.nukkit.server.network.minecraft;

import cn.nukkit.server.nbt.util.VarInt;
import cn.nukkit.server.network.Packets;
import cn.nukkit.server.network.minecraft.packet.UnknownPacket;
import cn.nukkit.server.network.minecraft.session.MinecraftSession;
import cn.nukkit.server.util.CompressionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.DataFormatException;


public class DefaultWrapperPacketHandler implements MinecraftSession.WrapperCompressionHandler {

    @Override
    public ByteBuf compressPackets(Collection<MinecraftPacket> packets) {
        ByteBuf source = PooledByteBufAllocator.DEFAULT.directBuffer();
        try {
            for (MinecraftPacket packet : packets) {
                ByteBuf packetBuf = null;
                try {
                    packetBuf = Packets.getCodec(MinecraftPackets.TYPE).tryEncode(packet);
                    VarInt.writeUnsignedInt(source, packetBuf.readableBytes());
                    source.writeBytes(packetBuf);
                } finally {
                    if (packetBuf != null) {
                        packetBuf.release();
                    }
                }
            }
            return CompressionUtil.zlibDeflate(source);
        } catch (DataFormatException e) {
            throw new RuntimeException("Unable to deflate buffer data", e);
        } finally {
            source.release();
        }
    }

    @Override
    public Collection<MinecraftPacket> decompressPackets(ByteBuf compressed) {
        List<MinecraftPacket> packets = new ArrayList<>();
        ByteBuf decompressed = null;
        try {
            decompressed = CompressionUtil.zlibInflate(compressed);
            while (decompressed.isReadable()) {
                int length = VarInt.readUnsignedInt(decompressed);
                ByteBuf data = decompressed.readSlice(length);

                if (data.readableBytes() == 0) {
                    throw new DataFormatException("Contained packet is empty.");
                }

                MinecraftPacket pkg = (MinecraftPacket) Packets.getCodec(MinecraftPackets.TYPE).tryDecode(data);
                if (pkg != null) {
                    packets.add(pkg);
                } else {
                    data.readerIndex(0);
                    UnknownPacket unknown = new UnknownPacket();
                    unknown.decode(data);
                    packets.add(unknown);
                }
            }
        } catch (DataFormatException e) {
            throw new RuntimeException("Unable to inflate buffer data", e);
        } finally {
            if (decompressed != null) {
                decompressed.release();
            }
        }
        return packets;
    }
}
