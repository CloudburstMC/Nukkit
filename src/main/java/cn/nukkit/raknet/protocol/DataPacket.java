package cn.nukkit.raknet.protocol;

import cn.nukkit.utils.Binary;

import java.util.ArrayList;
import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class DataPacket extends Packet {

    public List<EncapsulatedPacket> packets = new ArrayList<>();

    public Integer seqNumber;

    @Override
    public void encode() {
        super.encode();
        this.putLTriad(this.seqNumber);
        for (EncapsulatedPacket packet : this.packets) {
            this.put(packet.toBinary());
        }
    }

    public int length() {
        int length = 4;
        for (EncapsulatedPacket packet : this.packets) {
            length += packet.getTotalLength();
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

}
