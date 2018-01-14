package cn.nukkit.server.network.raknet;

import cn.nukkit.server.network.PacketType;
import cn.nukkit.server.network.Packets;
import cn.nukkit.server.network.raknet.packet.*;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class RakNetPackets {

    private static final Class<? extends NetworkPacket>[] RAKNET_PACKETS = new Class[256];
    public static PacketType TYPE = PacketType.forName("RAKNET");

    static {
        RAKNET_PACKETS[0x00] = ConnectedPingPacket.class;
        RAKNET_PACKETS[0x01] = UnconnectedPingPacket.class;
        RAKNET_PACKETS[0x03] = ConnectedPongPacket.class;
        RAKNET_PACKETS[0x05] = OpenConnectionRequest1Packet.class;
        RAKNET_PACKETS[0x06] = OpenConnectionReply1Packet.class;
        RAKNET_PACKETS[0x07] = OpenConnectionRequest2Packet.class;
        RAKNET_PACKETS[0x08] = OpenConnectionReply2Packet.class;
        RAKNET_PACKETS[0x09] = ConnectionRequestPacket.class;
        RAKNET_PACKETS[0x10] = ConnectionRequestAcceptedPacket.class;
        RAKNET_PACKETS[0x13] = NewIncomingConnectionPacket.class;
        RAKNET_PACKETS[0x14] = NoFreeIncomingConnectionsPacket.class;
        RAKNET_PACKETS[0x15] = DisconnectNotificationPacket.class;
        RAKNET_PACKETS[0x17] = ConnectionBannedPacket.class;
        RAKNET_PACKETS[0x19] = IncompatibleProtocolVersion.class;
        RAKNET_PACKETS[0x1a] = IpRecentlyConnectedPacket.class;
        RAKNET_PACKETS[0x1c] = UnconnectedPongPacket.class;
        RAKNET_PACKETS[0xa0] = NakPacket.class;
        RAKNET_PACKETS[0xc0] = AckPacket.class;

        Packets.registerPacketMappings(TYPE, RAKNET_PACKETS, new RakNetPacketCodec());
    }

    private static class RakNetPacketCodec extends Packets.PacketCodec {

        @Override
        protected Class<? extends NetworkPacket>[] getPackets() {
            return RAKNET_PACKETS;
        }
    }
}
