package cn.nukkit.network;

import cn.nukkit.Server;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.Zlib;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CompressBatchedTask implements Runnable {

    private final Server server;
    private final List<DataPacket> toBatch;
    private final List<Player> targets;
    private final int level;

    public CompressBatchedTask(Server server, List<DataPacket> toBatch, List<Player> targets) {
        this(server, toBatch, targets, 7);
    }

    public CompressBatchedTask(Server server, List<DataPacket> toBatch, List<Player> targets, int level) {
        this.server = server;
        this.toBatch = toBatch;
        this.targets = targets;
        this.level = level;
    }

    @Override
    public void run() {
        ByteBuf packetBuffer = ByteBufAllocator.DEFAULT.ioBuffer(32);
        ByteBuf payload = ByteBufAllocator.DEFAULT.ioBuffer();
        try {
            for (DataPacket packet : this.toBatch) {
                packet.tryEncode(packetBuffer);

                Binary.writeVarIntBuffer(payload, packetBuffer);

                packetBuffer.clear();
            }

            ByteBuf compressed = ByteBufAllocator.DEFAULT.ioBuffer(payload.readableBytes() / 2);
            try {
                Zlib.DEFAULT.deflate(payload, compressed, this.level);
                this.server.broadcastPacketsCallback(compressed, this.targets);
            } catch (Exception e) {
                compressed.release();
            }
        } finally {
            this.toBatch.forEach(ReferenceCountUtil::release);
            packetBuffer.release();
            payload.release();
        }
    }
}
