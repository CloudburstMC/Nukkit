package com.nukkitx.server.network.raknet.packet;

import com.google.common.collect.ImmutableList;
import com.nukkitx.server.network.raknet.RakNetPacket;
import com.nukkitx.server.network.raknet.util.IntRange;
import gnu.trove.list.TIntList;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Data
public abstract class AcknowledgePacket implements RakNetPacket {
    private final List<IntRange> ids = new ArrayList<>();

    public static List<IntRange> intoRanges(TIntList ids) {
        if (ids.isEmpty()) {
            throw new NoSuchElementException();
        }

        List<IntRange> ranges = new ArrayList<>();
        if (ids.size() == 1) {
            return ImmutableList.of(new IntRange(ids.get(0)));
        }
        ids.sort();

        int start = ids.get(0);
        int cur = start;

        for (int id : ids.subList(1, ids.size()).toArray()) {
            if (cur + 1 == id) {
                cur = id;
            } else {
                ranges.add(new IntRange(start, cur));
                start = id;
                cur = id;
            }
        }

        if (start == cur) {
            ranges.add(new IntRange(start));
        } else {
            ranges.add(new IntRange(start, cur));
        }

        return ranges;
    }

    @Override
    public void decode(ByteBuf buffer) {
        short size = buffer.readShort();
        for (int i = 0; i < size; i++) {
            boolean isSingleton = buffer.readBoolean();
            int lower = buffer.readMediumLE();
            if (isSingleton) {
                ids.add(new IntRange(lower, lower));
            } else {
                int upper = buffer.readMediumLE();
                ids.add(new IntRange(lower, upper));
            }
        }
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeShort(ids.size());
        for (IntRange id : ids) {
            boolean singleton = id.getStart() == id.getEnd();
            buffer.writeBoolean(singleton);
            buffer.writeMediumLE(id.getStart());
            if (!singleton) {
                buffer.writeMediumLE(id.getEnd());
            }
        }
    }
}
