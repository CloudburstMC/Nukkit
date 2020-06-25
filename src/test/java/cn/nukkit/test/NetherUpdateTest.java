package cn.nukkit.test;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.Network;
import cn.nukkit.network.protocol.BatchPacket;
import com.google.common.io.BaseEncoding;
import com.google.common.io.Resources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NetherUpdateTest {
    
    @Mock
    Server server;
    
    @Mock
    Player player;
    
    Network network;
    
    @BeforeEach
    void setup() {
        network = new Network(server);
    }
    
    @Test
    void smallPacket() throws IOException {
        BatchPacket batchPacket = loadBatchPacket("smallPacket");

        network.processBatch(batchPacket, player);
        
        verify(player).handleDataPacket(any());
    }

    @Test
    void vanillaPacket() throws IOException {
        BatchPacket batchPacket = loadBatchPacket("vanillaPacket");
        network.processBatch(batchPacket, player);

        verify(player).handleDataPacket(any());
    }
    
    private BatchPacket loadBatchPacket(String dumpName) throws IOException {
        byte[] content = BaseEncoding.base16().decode(loadDump(dumpName));
        BatchPacket batchPacket = new BatchPacket();
        batchPacket.setBuffer(content);
        assertEquals(0xfe, batchPacket.getByte());
        batchPacket.decode();
        return batchPacket;
    }
    
    @SuppressWarnings("UnstableApiUsage")
    private static String loadDump(String name) throws IOException {
        return Resources.readLines(
                Resources.getResource(NetherUpdateTest.class, "NetherUpdateTest."+name+".dump.txt"),
                StandardCharsets.ISO_8859_1
        ).get(0);
    }
}
