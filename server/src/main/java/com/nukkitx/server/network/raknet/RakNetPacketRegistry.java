package com.nukkitx.server.network.raknet;

import com.google.common.base.Preconditions;
import com.nukkitx.server.network.PacketCodec;
import com.nukkitx.server.network.PacketFactory;
import com.nukkitx.server.network.minecraft.packet.WrappedPacket;
import com.nukkitx.server.network.raknet.packet.*;
import gnu.trove.map.TObjectByteMap;
import gnu.trove.map.hash.TObjectByteHashMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class RakNetPacketRegistry implements PacketCodec<RakNetPacket> {
    private static final RakNetPacketRegistry INSTANCE = new RakNetPacketRegistry();

    private final PacketFactory<RakNetPacket>[] factories = (PacketFactory<RakNetPacket>[]) new PacketFactory[256];
    private final TObjectByteMap<Class<? extends RakNetPacket>> idMapping = new TObjectByteHashMap<>(64, 0.75f, (byte) -1);

    private RakNetPacketRegistry() {
        factories[0x00] = ConnectedPingPacket::new;
        factories[0x01] = UnconnectedPingPacket::new;
        factories[0x03] = ConnectedPongPacket::new;
        factories[0x05] = OpenConnectionRequest1Packet::new;
        factories[0x06] = OpenConnectionReply1Packet::new;
        factories[0x07] = OpenConnectionRequest2Packet::new;
        factories[0x08] = OpenConnectionReply2Packet::new;
        factories[0x09] = ConnectionRequestPacket::new;
        factories[0x10] = ConnectionRequestAcceptedPacket::new;
        factories[0x13] = NewIncomingConnectionPacket::new;
        factories[0x14] = NoFreeIncomingConnectionsPacket::new;
        factories[0x15] = DisconnectNotificationPacket::new;
        factories[0x17] = ConnectionBannedPacket::new;
        factories[0x19] = IncompatibleProtocolVersion::new;
        factories[0x1a] = IpRecentlyConnectedPacket::new;
        factories[0x1c] = UnconnectedPongPacket::new;
        factories[0xa0] = NakPacket::new;
        factories[0xc0] = AckPacket::new;
        factories[0xfe] = WrappedPacket::new;

        for (int i = 0; i < factories.length; i++) {
            if (factories[i] == null) continue;
            idMapping.put(factories[i].newInstance().getClass(), (byte) i);
        }
    }


    @Override
    public RakNetPacket tryDecode(ByteBuf byteBuf) {
        short id = byteBuf.readUnsignedByte();
        RakNetPacket packet = factories[id].newInstance();
        packet.decode(byteBuf);

        if (log.isDebugEnabled() && byteBuf.isReadable()) {
            log.debug(packet.getClass().getSimpleName() + " still has " + byteBuf.readableBytes() + " bytes to read!");
        }
        return packet;
    }

    @Override
    public ByteBuf tryEncode(RakNetPacket packet) {
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer();
        byteBuf.writeByte(getId(packet));
        packet.encode(byteBuf);
        return byteBuf;
    }

    public static RakNetPacket decode(ByteBuf byteBuf) {
        return INSTANCE.tryDecode(byteBuf);
    }

    public static ByteBuf encode(RakNetPacket packet) {
        return INSTANCE.tryEncode(packet);
    }

    public static void registerPacket(int id, PacketFactory<RakNetPacket> packetFactory) {
        Preconditions.checkArgument(id >= 0 && id <= 255);
        Preconditions.checkNotNull(packetFactory, "packetFactory");
        Preconditions.checkArgument(INSTANCE.factories[id] == null, "Packet id already registered");
        INSTANCE.factories[id] = packetFactory;
    }

    public static byte getId(RakNetPacket packet) {
        Class<? extends RakNetPacket> clazz = packet.getClass();
        byte id = INSTANCE.idMapping.get(clazz);
        if (id == -1) {
            throw new IllegalArgumentException("Packet ID for " + clazz.getName() + " does not exist.");
        }
        return id;
    }
}
