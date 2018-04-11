/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.network.raknet.datagram;

import io.netty.buffer.ByteBuf;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class RakNetDatagram extends AbstractReferenceCounted {
    private final List<EncapsulatedRakNetPacket> packets = new ArrayList<>();
    private RakNetDatagramFlags flags = new RakNetDatagramFlags((byte) 0x84);
    private int datagramSequenceNumber;

    @Override
    public RakNetDatagram retain() {
        super.retain();
        return this;
    }

    @Override
    public RakNetDatagram retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public ReferenceCounted touch(Object hint) {
        for (EncapsulatedRakNetPacket packet : packets) {
            packet.touch(hint);
        }
        return this;
    }

    public void decode(ByteBuf buf) {
        flags = new RakNetDatagramFlags(buf.readByte());
        datagramSequenceNumber = buf.readMediumLE();
        while (buf.isReadable()) {
            EncapsulatedRakNetPacket packet = new EncapsulatedRakNetPacket();
            packet.decode(buf);
            packets.add(packet);
        }
    }

    public void encode(ByteBuf buf) {
        buf.writeByte(flags.getFlagByte());
        buf.writeMediumLE(datagramSequenceNumber);
        for (EncapsulatedRakNetPacket packet : packets) {
            packet.encode(buf);
        }
    }

    public List<EncapsulatedRakNetPacket> getPackets() {
        return Collections.unmodifiableList(packets);
    }

    public boolean tryAddPacket(EncapsulatedRakNetPacket packet, short mtu) {
        int packetLn = packet.totalLength();
        if (packetLn >= mtu - 4) {
            return false; // Packet is too large
        }

        int existingLn = 0;
        for (EncapsulatedRakNetPacket netPacket : getPackets()) {
            existingLn += netPacket.totalLength();
        }

        if (existingLn + packetLn >= mtu - 4) {
            return false;
        }

        packets.add(packet);
        if (packet.isHasSplit()) {
            flags = new RakNetDatagramFlags((byte) 0x8c); // set continuous send
        }
        return true;
    }

    @Override
    protected void deallocate() {
        for (EncapsulatedRakNetPacket packet : packets) {
            ReferenceCountUtil.release(packet);
        }
    }
}