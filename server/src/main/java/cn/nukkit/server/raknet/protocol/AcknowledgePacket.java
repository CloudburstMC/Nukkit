package cn.nukkit.server.raknet.protocol;

import cn.nukkit.server.utils.Binary;
import cn.nukkit.server.utils.BinaryStream;

import java.util.TreeMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class AcknowledgePacket extends Packet {

    public TreeMap<Integer, Integer> packets;

    @Override
    public void encode() {
        super.encode();
        int count = this.packets.size();
        int[] packets = new int[count];

        int index = 0;
        for (int i : this.packets.values()) {
            packets[index++] = i;
        }
        short records = 0;
        BinaryStream payload = new BinaryStream();

        if (count > 0) {
            int pointer = 1;
            int start = packets[0];
            int last = packets[0];

            while (pointer < count) {
                int current = packets[pointer++];
                int diff = current - last;
                if (diff == 1) {
                    last = current;
                } else if (diff > 1) {

                    if (start == last) {
                        payload.putByte((byte) 0x01);
                        payload.put(Binary.writeLTriad(start));
                        start = last = current;
                    } else {
                        payload.putByte((byte) 0x00);
                        payload.put(Binary.writeLTriad(start));
                        payload.put(Binary.writeLTriad(last));
                        start = last = current;
                    }
                    ++records;
                }
            }

            if (start == last) {
                payload.putByte((byte) 0x01);
                payload.put(Binary.writeLTriad(start));
            } else {
                payload.putByte((byte) 0x00);
                payload.put(Binary.writeLTriad(start));
                payload.put(Binary.writeLTriad(last));
            }
            ++records;
        }

        this.putShort(records);
        this.buffer = Binary.appendBytes(
                this.buffer,
                payload.getBuffer()
        );
    }

    @Override
    public void decode() {
        super.decode();
        short count = this.getSignedShort();
        this.packets = new TreeMap<>();
        int cnt = 0;
        for (int i = 0; i < count && !this.feof() && cnt < 4096; ++i) {
            if (this.getByte() == 0) {
                int start = this.getLTriad();
                int end = this.getLTriad();
                if ((end - start) > 512) {
                    end = start + 512;
                }
                for (int c = start; c <= end; ++c) {
                    packets.put(cnt++, c);
                }
            } else {
                this.packets.put(cnt++, this.getLTriad());
            }
        }
    }

    @Override
    public Packet clean() {
        this.packets = new TreeMap<>();
        return super.clean();
    }

    @Override
    public AcknowledgePacket clone() throws CloneNotSupportedException {
        AcknowledgePacket packet = (AcknowledgePacket) super.clone();
        packet.packets = new TreeMap<>(this.packets);
        return packet;
    }
}
