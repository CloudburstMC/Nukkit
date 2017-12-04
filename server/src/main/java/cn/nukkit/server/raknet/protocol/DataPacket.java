package cn.nukkit.server.raknet.protocol;

import cn.nukkit.server.utils.Binary;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class DataPacket extends Packet {

    public ConcurrentLinkedQueue<Object> packets = new ConcurrentLinkedQueue<>();

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
        this.packets = new ConcurrentLinkedQueue<>();
        this.seqNumber = null;
        return super.clean();
    }

    @Override
    public DataPacket clone() throws CloneNotSupportedException {
        DataPacket packet = (DataPacket) super.clone();
        packet.packets = new ConcurrentLinkedQueue<>(this.packets);
        return packet;
    }

}
