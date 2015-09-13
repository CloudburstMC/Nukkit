package cn.nukkit.raknet.protocol;

import cn.nukkit.utils.Binary;

import java.util.ArrayList;
import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class DataPacket extends Packet {

    public List<Object> packets = new ArrayList<>();

    public Integer seqNumber;

    @Override
    public void encode() {
        super.encode();
        this.putLTriad(this.seqNumber);
        for (Object packet : this.packets) {
            this.put(packet instanceof EncapsulatedPacket ? ((EncapsulatedPacket) packet).toBinary() : (byte[]) packet);
        }
    }

    public int length() {
        int len = 4;
        int length = 4;
        for (Object packet : this.packets) {
            length += packet instanceof EncapsulatedPacket ? ((EncapsulatedPacket) packet).getTotalLength() : ((byte[]) packet).length;
        }

        return length;
    }

    @Override
    public void decode() {
        super.decode();
        this.seqNumber = this.getLTriad();

        while (!this.feof()) {
            byte[] data = Binary.subBytes(this.buffer, this.offset);
            EncapsulatedPacket packet = EncapsulatedPacket.fromBinary(data, false);
            this.offset += packet.getOffset();
            if (packet.buffer.length == 0) {
                break;
            }
            this.packets.add(packet);
        }
    }

    @Override
    public Packet clean() {
        this.packets.clear();
        this.seqNumber = null;
        return super.clean();
    }

    @Override
    public DataPacket clone() throws CloneNotSupportedException {
        DataPacket packet = (DataPacket) super.clone();
        List<Object> packets = new ArrayList<>();
        for (Object object : this.packets) {
            packets.add(object);
        }
        packet.packets = packets;
        return packet;
    }

}
