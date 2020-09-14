package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockPodzol;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.blockstate.BlockStateRegistry;
import cn.nukkit.dispenser.DispenseBehaviorRegister;
import cn.nukkit.entity.Attribute;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.anvil.Anvil;
import cn.nukkit.level.generator.Flat;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.Network;
import cn.nukkit.permission.BanList;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;
import cn.nukkit.resourcepacks.ResourcePackManager;
import cn.nukkit.scheduler.ServerScheduler;
import cn.nukkit.test.LogLevelAdjuster;
import cn.nukkit.utils.PlayerDataSerializer;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.util.FileUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LevelTest {
    static final LogLevelAdjuster logLevelAdjuster = new LogLevelAdjuster(); 
    
    /// Server Mocks ///
    @Mock
    PluginManager pluginManager;

    @Mock
    ServerScheduler scheduler;

    @Mock
    BanList banList;

    @Mock
    PlayerDataSerializer playerDataSerializer;

    @Mock
    ResourcePackManager resourcePackManager;

    @Mock
    Network network;

    @Mock
    DB db;

    File dataPath = FileUtils.createTempDir("powernukkit-level-test-data");

    @InjectMocks
    Server server = mock(Server.class, withSettings()
            .useConstructor(dataPath)
            .defaultAnswer(CALLS_REAL_METHODS));

    Level level;

    @Test
    void repairing() throws Exception {
        logLevelAdjuster.onlyNow(BlockStateRegistry.class, org.apache.logging.log4j.Level.OFF, ()->
                level.setBlockStateAt(2, 2, 2, BlockState.of(BlockID.PODZOL, 1))
        );
        Block block = level.getBlock(new Vector3(2, 2, 2));
        assertThat(block).isInstanceOf(BlockPodzol.class);
        assertEquals(BlockID.PODZOL, block.getId());
        assertEquals(0, block.getExactIntStorage());
        
        assertEquals(BlockState.of(BlockID.PODZOL), level.getBlockStateAt(2, 2, 2));
        
        assertTrue(level.unloadChunk(block.getChunkX(), block.getChunkZ()));

        assertEquals(BlockState.of(BlockID.PODZOL), level.getBlockStateAt(2, 2, 2));
    }

    @BeforeEach
    void setUp() throws IOException {
        String path = new File(dataPath, "worlds/TestLevel").getAbsolutePath()+File.separator;
        Anvil.generate(path, "TestLevel", 0, Flat.class);
        level = new Level(server, "TestLevel", path, Anvil.class);
        
        server.getLevels().put(level.getId(), level);
        server.setDefaultLevel(level);
    }

    @AfterEach
    void tearDown() {
        FileUtils.deleteRecursively(dataPath);
    }

    @AfterAll
    static void afterAll() {
        logLevelAdjuster.restoreLevels();
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
        BlockStateRegistry.getRuntimeId(BlockState.AIR); //Force it to load

        Generator.addGenerator(Flat.class, "flat", Generator.TYPE_FLAT);
    }
}
