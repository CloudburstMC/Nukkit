package cn.nukkit.server.network.minecraft.wrapper;

import cn.nukkit.server.nbt.util.VarInt;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.MinecraftPacketRegistry;
import cn.nukkit.server.network.minecraft.packet.UnknownPacket;
import cn.nukkit.server.network.util.CompressionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

@Log4j2
public class DefaultWrapperHandler implements WrapperHandler {

    @Override
    public ByteBuf compressPackets(MinecraftPacket... packets) {
        ByteBuf source = PooledByteBufAllocator.DEFAULT.directBuffer();
        try {
            for (MinecraftPacket packet : packets) {
                @Cleanup("release") ByteBuf packetBuf = MinecraftPacketRegistry.encode(packet);
                VarInt.writeUnsignedInt(source, packetBuf.readableBytes());
                source.writeBytes(packetBuf);
            }
            return CompressionUtil.zlibDeflate(source);
        } catch (DataFormatException e) {
            throw new RuntimeException("Unable to deflate buffer data", e);
        } finally {
            source.release();
        }
    }

    @Override
    public List<MinecraftPacket> decompressPackets(ByteBuf compressed) {
        List<MinecraftPacket> packets = new ArrayList<>();
        ByteBuf decompressed = null;
        try {
            try {
                decompressed = CompressionUtil.zlibInflate(compressed);
            } catch (Exception e) {
                log.debug("INFLATION ERROR \n{}", ByteBufUtil.prettyHexDump(compressed));
                throw new RuntimeException("Unable to decompress packet", e);
            }
            while (decompressed.isReadable()) {
                int length = VarInt.readUnsignedInt(decompressed);
                ByteBuf data = decompressed.readSlice(length);

                if (!data.isReadable()) {
                    throw new DataFormatException("Contained packet is empty.");
                }

                MinecraftPacket pkg = MinecraftPacketRegistry.decode(data);
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
