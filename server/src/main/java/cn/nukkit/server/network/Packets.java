package cn.nukkit.server.network;

import cn.nukkit.server.network.raknet.NetworkPacket;
import gnu.trove.map.TObjectShortMap;
import gnu.trove.map.TShortObjectMap;
import gnu.trove.map.hash.TObjectShortHashMap;
import gnu.trove.map.hash.TShortObjectHashMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;

@UtilityClass
public class Packets {
    private static final TObjectShortMap<Class<? extends NetworkPacket>> PACKET_MAPPING = new TObjectShortHashMap<>(256);
    private static final TShortObjectMap<PacketCodec> CODECS = new TShortObjectHashMap<>(2, 0.75f);


    public static void registerPacketMappings(PacketType type, Class<? extends NetworkPacket>[] packets, PacketCodec codec) {
        for (short i = 0; i < packets.length; i++) {
            Class clazz = packets[i];
            if (clazz != null) {
                PACKET_MAPPING.put(clazz, i);
            }
        }
        CODECS.put(type.intPacketType(), codec);
    }

    public static int getId(NetworkPacket packet) {
        Class<? extends NetworkPacket> pkgClass = packet.getClass();
        int res = PACKET_MAPPING.get(pkgClass);
        if (res == -1) {
            throw new IllegalArgumentException("Packet ID for " + pkgClass.getName() + " does not exist.");
        }
        return res;
    }

    public static PacketCodec getCodec(PacketType type) {
        PacketCodec codec = CODECS.get(type.intPacketType());
        if (codec == null) {
            throw new IllegalArgumentException("PacketCodec is not registered!");
        }
        return codec;
    }

    @Log4j2
    public abstract static class PacketCodec {

        @Nullable
        protected final Class<? extends NetworkPacket> readClass(int id) {
            return getPackets()[id];
        }

        protected final NetworkPacket instantiateClass(Class<? extends NetworkPacket> pkClass) {
            NetworkPacket networkPacket;
            try {
                networkPacket = pkClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Unable to create packet instance", e);
            }

            return networkPacket;
        }

        public NetworkPacket tryDecode(ByteBuf buf) {
            int id = buf.readUnsignedByte();
            Class<? extends NetworkPacket> packetClass = readClass(id);

            if (packetClass == null) {
                // Class is null so we can't instantiate it.
                return null;
            }

            NetworkPacket packet = instantiateClass(packetClass);
            // Decode packet
            packet.decode(buf);

            if (log.isDebugEnabled() && buf.readableBytes() > 0) {
                log.debug("{} still has {} more bytes to read", packetClass.getSimpleName(), buf.readableBytes());
            }

            return packet;
        }

        public ByteBuf tryEncode(NetworkPacket packet) {
            int id = getId(packet);

            ByteBuf buf = PooledByteBufAllocator.DEFAULT.directBuffer();
            buf.writeByte((id & 0xFF));

            packet.encode(buf);

            return buf;
        }

        protected abstract Class<? extends NetworkPacket>[] getPackets();
    }
}
