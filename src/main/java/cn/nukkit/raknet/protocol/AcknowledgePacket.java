package cn.nukkit.raknet.protocol;

import cn.nukkit.utils.Binary;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class AcknowledgePacket extends Packet {

    public Integer[] packets;

    @Override
    public void encode() {
        super.encode();
        int count = this.packets.length;
        short records = 0;
        ByteBuffer payload = ByteBuffer.allocate(count * 7 + 7);

        if (count > 0) {
            int pointer = 1;
            int start = this.packets[0];
            int last = this.packets[0];

            while (pointer < count) {
                int current = this.packets[pointer++];
                int diff = current - last;
                if (diff == 1) {
                    last = current;
                } else if (diff > 1) {

                    if (start == last) {
                        payload.put((byte) 0x01);
                        payload.put(Binary.writeLTriad(start));
                        start = last = current;
                    } else {
                        payload.put((byte) 0x00);
                        payload.put(Binary.writeLTriad(start));
                        payload.put(Binary.writeLTriad(last));
                        start = last = current;
                    }
                    ++records;
                }
            }

            if (start == last) {
                payload.put((byte) 0x01);
                payload.put(Binary.writeLTriad(start));
            } else {
                payload.put((byte) 0x00);
                payload.put(Binary.writeLTriad(start));
                payload.put(Binary.writeLTriad(last));
            }
            ++records;
        }

        this.putShort(records);
        this.buffer = Binary.appendBytes(
                this.buffer,
                Arrays.copyOf(payload.array(), payload.position())
        );
    }

    @Override
    public void decode() {
        super.decode();
        short count = this.getShort();
        List<Integer> packets = new ArrayList<>();
        int cnt = 0;
        for (int i = 0; i < count && !this.feof() && cnt < 4096; ++i) {
            if (this.getByte() == 0) {
                int start = this.getLTriad();
                int end = this.getLTriad();
                if ((end - start) > 512) {
                    end = start + 512;
                }
                for (int c = start; c <= end; ++c) {
                    cnt++;
                    packets.add(c);
                }
            } else {
                cnt++;
                packets.add(this.getLTriad());
            }
        }

        this.packets = packets.toArray(new Integer[packets.size()]);
    }

    @Override
    public Packet clean() {
        this.packets = new Integer[0];
        return super.clean();
    }
}
