package cn.nukkit.server.network.raknet.datagram;

import java.util.BitSet;

public class RakNetDatagramFlags {
    private final BitSet flags;

    public RakNetDatagramFlags(byte flags) {
        this.flags = BitSet.valueOf(new byte[]{flags});
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
        return flags.toByteArray()[0];
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
