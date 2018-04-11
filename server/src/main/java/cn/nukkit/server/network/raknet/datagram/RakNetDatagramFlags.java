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

import cn.nukkit.server.util.bitset.ByteBitSet;

public class RakNetDatagramFlags {
    private final ByteBitSet flags;

    public RakNetDatagramFlags(byte flags) {
        this.flags = new ByteBitSet(flags);
    }

    public boolean isValid() {
        return flags.get(7);
    }

    public boolean isAck() {
        return flags.get(6);
    }

    public boolean isNak() {
        return flags.get(5);
    }

    public boolean isPacketPair() {
        return !isNak() && flags.get(4);
    }

    public boolean isContinuousSend() {
        return !isNak() && flags.get(3);
    }

    public boolean needsBAndAS() {
        return flags.get(2);
    }

    public byte getFlagByte() {
        return flags.get();
    }

    @Override
    public String toString() {
        return "RakNetDatagramFlags{" +
                "flags=" + flags +
                ", valid=" + isValid() +
                ", ack=" + isAck() +
                ", nak=" + isNak() +
                ", packetPair=" + isPacketPair() +
                ", continuousSend=" + isContinuousSend() +
                ", needsBAndAS=" + needsBAndAS() +
                ", flagByte=" + getFlagByte() +
                '}';
    }
}
