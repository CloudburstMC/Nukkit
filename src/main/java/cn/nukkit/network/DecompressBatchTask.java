package cn.nukkit.network;

import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.Zlib;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DecompressBatchTask implements Runnable {
    private final Network network;
    private final Player player;
    private final ByteBuf compressed;

    public DecompressBatchTask(Network network, Player player, ByteBuf compressed) {
        this.network = network;
        this.player = player;
        this.compressed = compressed;
    }

    @Override
    public void run() {
        ByteBuf decompressed = ByteBufAllocator.DEFAULT.directBuffer();
        try {
            Zlib.DEFAULT.inflate(compressed, decompressed, Network.MAX_BATCH_SIZE);
        } catch (Exception e) {
            log.debug("Error decompressing", e);
            decompressed.release();
            return;
        } finally {
            compressed.release();
        }
        try {
            while (decompressed.isReadable()) {
                player.packetsRecieved++;
                if (player.packetsRecieved >= 1000) {
                    player.close("", "Illegal Batch Packet");
                    return;
                }

                ByteBuf buffer = Binary.readVarIntBuffer(decompressed);

                int header = (int) Binary.readUnsignedVarInt(buffer);
                DataPacket dataPacket = this.network.getPacket((short) (header & 0x3ff));
                if (dataPacket == null) continue;

                dataPacket.senderId = (header >>> 10) & 0x3;
                dataPacket.clientId = (header >>> 12) & 0x3;
                dataPacket.tryDecode(buffer);

                this.player.handleDataPacket(dataPacket);
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Error whilst decoding batch packet", e);
            }
        } finally {
            decompressed.release();
        }
    }
}
