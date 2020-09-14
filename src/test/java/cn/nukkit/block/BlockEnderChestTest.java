package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntityEnderChest;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.blockstate.BlockStateRegistry;
import cn.nukkit.dispenser.DispenseBehaviorRegister;
import cn.nukkit.entity.Attribute;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.anvil.Anvil;
import cn.nukkit.level.generator.Flat;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.Network;
import cn.nukkit.permission.BanList;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;
import cn.nukkit.resourcepacks.ResourcePackManager;
import cn.nukkit.scheduler.ServerScheduler;
import cn.nukkit.utils.PlayerDataSerializer;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.util.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author joserobjr
 */
@ExtendWith(MockitoExtension.class)
class BlockEnderChestTest {
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

    File dataPath = FileUtils.createTempDir("powernukkit-enderchest-test-data");

    @InjectMocks
    Server server = mock(Server.class, withSettings()
            .useConstructor(dataPath)
            .defaultAnswer(CALLS_REAL_METHODS));

    Level level;
    
    @Mock
    Player player;
    
    final Vector3 pos = new Vector3(3, 4, 5);
    
    @Test
    void place() {
        Position playerPos = new Position(pos.x, pos.y, pos.z, level);
        when(player.getPosition()).thenReturn(playerPos);
        when(player.getNextPosition()).thenReturn(playerPos.clone());
        when(player.getDirection()).thenReturn(BlockFace.NORTH);
        
        Item item = Item.getBlock(BlockID.ENDER_CHEST);
        assertTrue(level.setBlock(pos.down(), Block.get(BlockID.STONE)));
        Item placed = level.useItemOn(pos.down(), item, BlockFace.UP, .5f, .5f, .5f, player);
        assertNotEquals(item, placed);
        assertNotNull(placed);
        assertTrue(placed.isNull());
        assertEquals(BlockID.ENDER_CHEST, level.getBlockIdAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), 0));
        assertThat(level.getBlockEntity(pos)).isInstanceOf(BlockEntityEnderChest.class);
    }

    @BeforeEach
    void setUp() throws IOException {
        String path = new File(dataPath, "worlds/TestLevel").getAbsolutePath()+File.separator;
        Anvil.generate(path, "TestLevel", 0, Flat.class);
        level = new Level(server, "TestLevel", path, Anvil.class);

        server.getLevels().put(level.getId(), level);
        server.setDefaultLevel(level);
        
        lenient().when(player.getLevel()).thenReturn(level);
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
        BlockStateRegistry.getRuntimeId(BlockState.AIR); //Force it to load

        Generator.addGenerator(Flat.class, "flat", Generator.TYPE_FLAT);
    }

}
