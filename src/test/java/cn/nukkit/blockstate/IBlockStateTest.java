package cn.nukkit.blockstate;

import cn.nukkit.block.*;
import cn.nukkit.blockstate.exception.InvalidBlockStateException;
import cn.nukkit.math.BlockFace;
import cn.nukkit.test.LogLevelAdjuster;
import cn.nukkit.utils.MainLogger;
import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static cn.nukkit.block.BlockID.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(PowerNukkitExtension.class)
class IBlockStateTest {
    static LogLevelAdjuster logLevelAdjuster = new LogLevelAdjuster();

    Block pos = BlockState.AIR.getBlock(null, 1, 2, 3);
    
    
    @BeforeAll
    static void beforeAll() {
        logLevelAdjuster.setLevel(MainLogger.class, Level.ERROR);
        logLevelAdjuster.setLevel(BlockStateRegistry.class, Level.ERROR);
    }

    @AfterAll
    static void afterAll() {
        logLevelAdjuster.restoreLevels();
    }

    @Test
    void getBlock() {
        Block block = BlockState.of(PLANKS).getBlock();
        assertEquals(PLANKS, block.getId());
        assertEquals(0, block.getExactIntStorage());
        assertThat(block).isInstanceOf(BlockPlanks.class);

        BlockState invalidBlockState = BlockState.of(PLANKS, 5000);
        assertThrows(InvalidBlockStateException.class, invalidBlockState::getBlock);
        
        Block fixedBlock = invalidBlockState.getBlockRepairing(pos);
        assertEquals(PLANKS, fixedBlock.getId());
        assertEquals(0, fixedBlock.getExactIntStorage());
        assertThat(block).isInstanceOf(BlockPlanks.class);
    }

    @Test
    void getBlockRepairing() {
        Block block = BlockState.of(BEEHIVE, 3 | (7 << 3)).getBlockRepairing(pos);
        assertEquals(BEEHIVE, block.getId());
        assertEquals(3, block.getExactIntStorage());
        assertThat(block).isInstanceOf(BlockBeehive.class);
    }

    @Test
    void getStateId() {
        BlockWall wall = (BlockWall) Block.get(COBBLE_WALL);
        wall.setWallType(BlockWall.WallType.MOSSY_STONE_BRICK);
        wall.setWallPost(true);
        wall.setConnection(BlockFace.SOUTH, BlockWall.WallConnectionType.TALL);
        assertEquals("minecraft:cobblestone_wall;wall_block_type=mossy_stone_brick;wall_connection_type_east=none;wall_connection_type_north=none;wall_connection_type_south=tall;wall_connection_type_west=none;wall_post_bit=1", 
                wall.getStateId());
        
        Block block = Block.get(CRIMSON_PLANKS);
        assertEquals("minecraft:crimson_planks", block.getStateId());

        /*int fakeId = Block.MAX_BLOCK_ID - 1;
        Block fake = Block.get(fakeId);
        assertThat(fake).isInstanceOf(BlockUnknown.class);
        assertEquals("blockid:"+ fakeId +";nukkit-unknown=0", fake.getStateId());
        
        assertEquals("blockid:"+fakeId, BlockStateRegistry.getPersistenceName(fakeId));
        assertEquals("blockid:10000", BlockStateRegistry.getPersistenceName(10_000));
        assertEquals(80000, BlockStateRegistry.getBlockId("blockid:80000"));*/
    }
    
    @Test
    void negativeByte() {
        BlockState state = BlockState.of(COBBLE_WALL, 173);
        assertEquals(173, state.getDataStorage());
        MutableBlockState mutableState = BlockStateRegistry.createMutableState(COBBLE_WALL);
        mutableState.setDataStorage(state.getDataStorage());
        assertEquals(173, mutableState.getDataStorage());
    }
}
