package cn.nukkit.network;

import cn.nukkit.Server;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.player.Player;
import cn.nukkit.scheduler.AsyncTask;
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
public class CompressBatchedTask extends AsyncTask {

    private final List<DataPacket> toBatch;
    private final List<Player> targets;
    private final int level;
    private ByteBuf compressed;

    public CompressBatchedTask(Server server, List<DataPacket> toBatch, List<Player> targets) {
        this(toBatch, targets, 7);
    }

    public CompressBatchedTask(List<DataPacket> toBatch, List<Player> targets, int level) {
        this.toBatch = toBatch;
        this.targets = targets;
        this.level = level;
    }

    @Override
    public void onRun() {
        ByteBuf packetBuffer = ByteBufAllocator.DEFAULT.ioBuffer(32);
        ByteBuf payload = ByteBufAllocator.DEFAULT.ioBuffer();
        try {
            for (DataPacket packet : this.toBatch) {
                packet.tryEncode(packetBuffer);

                Binary.writeVarIntBuffer(payload, packetBuffer);

                packetBuffer.clear();
            }

            this.compressed = ByteBufAllocator.DEFAULT.ioBuffer(payload.readableBytes() / 2);
            try {
                Zlib.DEFAULT.deflate(payload, compressed, this.level);
            } catch (Exception e) {
                compressed.release();
                throw e;
            }
        } finally {
            this.toBatch.forEach(ReferenceCountUtil::release);
            packetBuffer.release();
            payload.release();
        }
    }

    @Override
    public void onCompletion(Server server) {
        BatchPacket packet = new BatchPacket();
        packet.payload = this.compressed;

        try {
            for (Player target : targets) {
                target.directDataPacket(packet);
            }
        } finally {
            packet.release();
        }
    }
}
