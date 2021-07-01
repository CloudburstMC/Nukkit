package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockPodzol;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.blockstate.BlockStateRegistry;
import cn.nukkit.level.format.anvil.Anvil;
import cn.nukkit.level.generator.Flat;
import cn.nukkit.math.Vector3;
import cn.nukkit.test.LogLevelAdjuster;
import co.aikar.timings.Timings;
import org.iq80.leveldb.util.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(PowerNukkitExtension.class)
class LevelTest {
    static final LogLevelAdjuster logLevelAdjuster = new LogLevelAdjuster(); 
    
    File levelFolder;
    
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
        Server server = Server.getInstance();
        levelFolder = new File(server.getDataPath(), "worlds/TestLevel");
        String path = levelFolder.getAbsolutePath()+File.separator;
        Anvil.generate(path, "TestLevel", 0, Flat.class);
        Timings.isTimingsEnabled(); // Initialize timings to avoid concurrent updates on initialization
        level = new Level(server, "TestLevel", path, Anvil.class);
        level.setAutoSave(true);
        
        server.getLevels().put(level.getId(), level);
        server.setDefaultLevel(level);
    }

    @AfterEach
    void tearDown() {
        FileUtils.deleteRecursively(levelFolder);
    }

    @AfterAll
    static void afterAll() {
        logLevelAdjuster.restoreLevels();
    }
}
