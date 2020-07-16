package cn.nukkit.level.format.generic;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockWall;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.anvil.Anvil;
import cn.nukkit.math.BlockFace;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class BaseChunkTest {
    
    @Mock
    Anvil anvil;
    
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    BaseChunk chunk;
    
    @BeforeAll
    static void setupEnvironment() {
        Block.init();
    }
    
    @BeforeEach
    void setup() {
        chunk.sections = new ChunkSection[16];
        System.arraycopy(EmptyChunkSection.EMPTY, 0, chunk.sections, 0, 16);
        chunk.setProvider(anvil);
        chunk.providerClass = Anvil.class;
    }
    
    @Test
    void backwardCompatibilityUpdate2InvalidWalls() {
        int wallType = BlockWall.WallType.DIORITE.ordinal();
        int x = 3, y = 4, z = 5, invalid = 0b11_0000 | wallType;
        chunk.setBlock(x, y, z, BlockID.COBBLE_WALL, invalid);
        chunk.setBlock(x, y+1, z, BlockID.COBBLE_WALL, wallType);
        
        chunk.setBlock(x+1, y, z, BlockID.COBBLE_WALL, wallType);
        chunk.setBlock(x+1, y+1, z, BlockID.COBBLE_WALL, wallType);

        ChunkSection section = chunk.getSection(y >> 4);
        section.setContentVersion(1);
        
        assertEquals(BlockID.COBBLE_WALL, chunk.getBlockId(x, y, z));
        assertEquals(invalid, chunk.getBlockData(x, y, z));
        assertEquals(1, section.getContentVersion());
        
        Level level = mock(Level.class, Answers.CALLS_REAL_METHODS);
        doReturn(chunk).when(level).getChunk(x >> 4, z >> 4);
        
        chunk.backwardCompatibilityUpdate(level);
        
        assertEquals(BaseChunk.CONTENT_VERSION, section.getContentVersion());
        assertEquals(BlockID.COBBLE_WALL, chunk.getBlockId(x, y, z));
        assertEquals(BlockID.COBBLE_WALL, chunk.getBlockId(x, y+1, z));
        
        assertEquals(BlockID.COBBLE_WALL, chunk.getBlockId(x+1, y, z));
        assertEquals(BlockID.COBBLE_WALL, chunk.getBlockId(x+1, y+1, z));

        BlockWall wall = new BlockWall(wallType);
        wall.setWallPost(true);
        wall.setConnection(BlockFace.EAST, BlockWall.WallConnectionType.TALL);
        assertEquals(wall.getDamage(), chunk.getBlockData(x, y, z));
        
        wall.setWallPost(true);
        wall.clearConnections();
        wall.setConnection(BlockFace.EAST, BlockWall.WallConnectionType.SHORT);
        assertEquals(wall.getDamage(), chunk.getBlockData(x, y+1, z));
        
        wall.setWallPost(true);
        wall.clearConnections();
        wall.setConnection(BlockFace.WEST, BlockWall.WallConnectionType.TALL);
        assertEquals(wall.getDamage(), chunk.getBlockData(x+1, y, z));
        
        wall.setWallPost(true);
        wall.clearConnections();
        wall.setConnection(BlockFace.WEST, BlockWall.WallConnectionType.SHORT);
        assertEquals(wall.getDamage(), chunk.getBlockData(x+1, y+1, z));
    }
}
