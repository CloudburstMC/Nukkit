package cn.nukkit;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.dispenser.DispenseBehaviorRegister;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.lang.BaseLang;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.anvil.Chunk;
import cn.nukkit.network.Network;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import cn.nukkit.network.protocol.LoginPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.types.NetworkInventoryAction;
import cn.nukkit.permission.BanList;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;
import cn.nukkit.resourcepacks.ResourcePackManager;
import cn.nukkit.scheduler.ServerScheduler;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.PlayerDataSerializer;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.iq80.leveldb.util.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerTest {
    private final Long clientId = 32L;
    private final String clientIp = "1.2.3.4";
    private final int clientPort = 3232;
    
    @Mock
    SourceInterface sourceInterface;
    
    @Mock
    Server server;
    
    @Mock
    PluginManager pluginManager;
    
    @Mock
    Skin skin;
    
    @Mock
    ServerScheduler scheduler;
    
    @Mock
    BanList banList;
    
    @Mock
    PlayerDataSerializer playerDataSerializer;
    
    @Mock
    LevelProvider levelProvider;
    
    @Mock
    Level level;
    
    @Mock
    ResourcePackManager resourcePackManager;
    
    @Mock
    Network network;
    
    File dataPath;
            
    Player player;

    @Test
    void dupeCommand() {
        Item stick = Item.get(ItemID.STICK);
        Item air = Item.getBlock(BlockID.AIR, 0, 0);
        
        player.getInventory().addItem(stick);
        List<NetworkInventoryAction> actions = new ArrayList<>();
        NetworkInventoryAction remove = new NetworkInventoryAction();
        remove.sourceType = NetworkInventoryAction.SOURCE_CONTAINER;
        remove.windowId = 0;
        remove.stackNetworkId = 1;
        remove.inventorySlot = 0;
        remove.oldItem = stick;
        remove.newItem = air;
        actions.add(remove);

        for (int slot = 1; slot < 35; slot++) {
            if (slot > 1) {
                actions.add(remove);
            }
            
            NetworkInventoryAction add = new NetworkInventoryAction();
            add.sourceType = NetworkInventoryAction.SOURCE_CONTAINER;
            add.windowId = 0;
            add.stackNetworkId = 1;
            add.inventorySlot = slot;
            add.oldItem = air;
            add.newItem = stick;
            
            actions.add(add);
        }

        InventoryTransactionPacket packet = new InventoryTransactionPacket();
        packet.actions = actions.toArray(new NetworkInventoryAction[0]);
        
        player.handleDataPacket(packet);

        int count = countItems(stick);
        assertEquals(1, count);
    }
    
    private int countItems(Item item) {
        int count = 0;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            Item inv = player.getInventory().getItem(i);
            if (item.equals(inv)) {
                count += inv.getCount();
            }
        }
        return count;
    }

    @BeforeEach
    void setUp() throws NoSuchFieldException {
        ServerTest.setInstance(server);
        ServerTest.setPluginManager(server, pluginManager);
        ServerTest.setConfig(server, new Config());
        ServerTest.setLanguage(server, new BaseLang(BaseLang.FALLBACK_LANGUAGE));
        
        ReflectionUtil.setFieldValue(Level.class.getDeclaredField("updateEntities"), level, new Long2ObjectOpenHashMap<>());
        
        dataPath = FileUtils.createTempDir("powernukkit-player-test-data");
        dataPath.deleteOnExit();

        when(server.getDataPath()).thenReturn(dataPath.getAbsolutePath());
        when(server.getMaxPlayers()).thenReturn(10);
        when(server.isWhitelisted(anyString())).thenReturn(true);
        when(server.getOfflinePlayerData(any(UUID.class), anyBoolean())).thenCallRealMethod();
        
        when(server.getPluginManager()).thenReturn(pluginManager);
        when(server.getScheduler()).thenReturn(scheduler);
        when(server.getNameBans()).thenReturn(banList);
        when(server.getIPBans()).thenReturn(banList);
        when(server.getDefaultLevel()).thenReturn(level);
        when(server.getResourcePackManager()).thenReturn(resourcePackManager);
        when(server.getNetwork()).thenReturn(network);
        
        when(level.getSafeSpawn()).thenReturn(new Position(100,64,200,level));
        when(level.getName()).thenReturn("DefaultLevel");
        when(level.getChunk(anyInt(), anyInt(), anyBoolean())).thenReturn(new Chunk(levelProvider));
        
        when(levelProvider.getLevel()).thenReturn(level);
        when(level.getServer()).thenReturn(server);
        
        doCallRealMethod().when(server).setPlayerDataSerializer(any());
        server.setPlayerDataSerializer(playerDataSerializer);

        when(server.getConfig()).thenCallRealMethod();
        when(server.getConfig(anyString(), any())).thenCallRealMethod();
        when(server.getLogger()).thenCallRealMethod();
        when(server.getLanguage()).thenCallRealMethod();
        
        when(skin.isValid()).thenReturn(true);
        
        player = new Player(sourceInterface, clientId, clientIp, clientPort);
        LoginPacket loginPacket = new LoginPacket();
        loginPacket.username = "TestPlayer";
        loginPacket.protocol = ProtocolInfo.CURRENT_PROTOCOL;
        loginPacket.clientId = 2L;
        loginPacket.clientUUID = new UUID(3, 3);
        loginPacket.skin = skin;
        loginPacket.putLInt(2);
        loginPacket.put("{}".getBytes());
        loginPacket.putLInt(0);
        player.handleDataPacket(loginPacket);
        player.completeLoginSequence();
        
        assertTrue(player.isOnline(), "Failed to make the fake player login");
    }

    @AfterEach
    void tearDown() {
        FileUtils.deleteRecursively(dataPath);
    }

    @BeforeAll
    static void beforeAll() {
        Block.init();
        Enchantment.init();
        Item.init();
        EnumBiome.values(); //load class, this also registers biomes
        Effect.init();
        Potion.init();
        Attribute.init();
        DispenseBehaviorRegister.init();
        GlobalBlockPalette.getOrCreateRuntimeId(0, 0); //Force it to load
    }
}
