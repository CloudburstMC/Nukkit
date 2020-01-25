package test;

//import cn.nukkit.raknet.RakNet;
//import cn.nukkit.raknet.protocol.EncapsulatedPacket;

public class DataInspect3 {
    /*private static Network network = new Network(null);
    public static void main(String[] args) {
        Zlib.setProvider(0);
        byte[] bytes = hexStringToByteArray("02000000450000806da20000801100007f0000017f000001f62a4abc006cbc5c844a40006002b0793f00633f0000fe47b1165edafe07b91e7733a8f1eea936522efeb9aa7c03f907fb191a28eb3ab71f705575e17cbd82b6466fd2baefede39e25f281d6e3d1f71f6d76180d72ca7e1cb4999b4edad7d67506d8a59e4cdedb5edcfbee8a");
        bytes = hexStringToByteArray("02000000450000676dc40000801100007f0000017f000001f62a4abc0053d023845740006001e8863f00703f0000fead3af44f24c42dcf1b9880629d3cd7a044aaf383881cf2391e08a69d53287706c2dd294f70ed8342eb00bc2d1452c19556febe40b2b157358544d208");
        bytes = hexStringToByteArray("0200000045000065986d0000801100007f0000017f0000014abcf62a00519ffa844003006001d8390300e4020000febbfa4976c4164af761b3951e03998b7beb37c52204727343dea7248fcd4540ede0ae9da31575eacdcba4641c5aa94dfaf9b388c58d393e586f96");
        bytes = hexStringToByteArray("02000000450000a684230000801100007f0000017f000001fbe14abc0092b961849806006003e07e060068060000fe20d0d3949473f5bd0c4dc72f5c0be4022cd4d5fd9fcb655c6754193567f088e0ec26739da043eb744e323ec0118dcb5b2ea439aea9f4f0f78ae68726983b1e24a1b85f394c6ec43480ed160dfe1203933c493a9e3aea0dbd3f652fb02475e61968db9a4e5140b624ddb3717f847b5ad2fdfd797eb46be6663c870e");
        bytes = hexStringToByteArray("02000000450000499afd0000801100007f0000017f000001fbe14abc00359b8584ac01006000f8a401008e010000fe3060016e4a065a762e2f5d5dba1c432bc00b9d8271902fce5120ca77900f");
        bytes = hexStringToByteArray("0200000045000078ca3a0000801100007f0000017f000001fbe14abc0064969d84da0000600270c80000af000000fe7801014200bdff237b231a37ce4296d08242f8238a42fa08106d696e6563726166743a706c6179657200001d13011741ce42723d8542fe228a4288348b4178d5a84277d5a842000100490018b7");
        //bytes = hexStringToByteArray("02000000450000349b3c0000801100007f0000017f000001fbe14abc0020511484c30100000088030000000022b70b5d0000000022b70b67");
        bytes = Arrays.copyOfRange(bytes, 8*4, bytes.length);
        //handlePacket(bytes);
        EncapsulatedPacket encapsulatedPacket = EncapsulatedPacket.fromBinary(bytes);
        DataPacket packet = getPacket(encapsulatedPacket.buffer);
        assert packet != null;
        packet.decode();
        if (packet instanceof BatchPacket) {
            BatchPacket batchPacket = (BatchPacket) packet;
            network.processBatch(batchPacket, null);
        }
    }

    public static boolean handlePacket(byte[] packet) {
        if (packet != null && packet.length > 0) {
            byte id = packet[0];
            int offset = 1;
            if (id == RakNet.PACKET_ENCAPSULATED) {
                int len = packet[offset++];
                String identifier = new String(Binary.subBytes(packet, offset, len), StandardCharsets.UTF_8);
                offset += len;
                int flags = packet[offset++];
                byte[] buffer = Binary.subBytes(packet, offset);
                //this.instance.handleEncapsulated(identifier, EncapsulatedPacket.fromBinary(buffer, true), flags);
            } else if (id == RakNet.PACKET_RAW) {
                int len = packet[offset++];
                String address = new String(Binary.subBytes(packet, offset, len), StandardCharsets.UTF_8);
                offset += len;
                int port = Binary.readShort(Binary.subBytes(packet, offset, 2)) & 0xffff;
                offset += 2;
                byte[] payload = Binary.subBytes(packet, offset);
                //this.instance.handleRaw(address, port, payload);
            } else if (id == RakNet.PACKET_SET_OPTION) {
                int len = packet[offset++];
                String name = new String(Binary.subBytes(packet, offset, len), StandardCharsets.UTF_8);
                offset += len;
                String value = new String(Binary.subBytes(packet, offset), StandardCharsets.UTF_8);
                //this.instance.handleOption(name, value);
            } else if (id == RakNet.PACKET_OPEN_SESSION) {
                int len = packet[offset++];
                String identifier = new String(Binary.subBytes(packet, offset, len), StandardCharsets.UTF_8);
                offset += len;
                len = packet[offset++];
                String address = new String(Binary.subBytes(packet, offset, len), StandardCharsets.UTF_8);
                offset += len;
                int port = Binary.readShort(Binary.subBytes(packet, offset, 2)) & 0xffff;
                offset += 2;
                long clientID = Binary.readLong(Binary.subBytes(packet, offset, 8));
                //this.instance.openSession(identifier, address, port, clientID);
            } else if (id == RakNet.PACKET_CLOSE_SESSION) {
                int len = packet[offset++];
                String identifier = new String(Binary.subBytes(packet, offset, len), StandardCharsets.UTF_8);
                offset += len;
                len = packet[offset++];
                String reason = new String(Binary.subBytes(packet, offset, len), StandardCharsets.UTF_8);
                //this.instance.closeSession(identifier, reason);
            } else if (id == RakNet.PACKET_INVALID_SESSION) {
                int len = packet[offset++];
                String identifier = new String(Binary.subBytes(packet, offset, len), StandardCharsets.UTF_8);
                //this.instance.closeSession(identifier, "Invalid session");
            } else if (id == RakNet.PACKET_ACK_NOTIFICATION) {
                int len = packet[offset++];
                String identifier = new String(Binary.subBytes(packet, offset, len), StandardCharsets.UTF_8);
                offset += len;
                int identifierACK = Binary.readInt(Binary.subBytes(packet, offset, 4));
                //this.instance.notifyACK(identifier, identifierACK);
            }
            return true;
        }

        return false;
    }

    private static DataPacket getPacket(byte[] buffer) {
        int start = 0;

        if (buffer[0] == (byte) 0xfe) {
            start++;
        }
        DataPacket data = network.getPacket(ProtocolInfo.BATCH_PACKET);

        if (data == null) {
            return null;
        }

        data.setBuffer(buffer, start);

        return data;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }*/
}
