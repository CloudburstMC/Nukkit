package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.event.block.BlockSpreadEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.powernukkit.tests.api.MockServer;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * @author joserobjr
 */
@ExtendWith(PowerNukkitExtension.class)
@MockServer(callsRealMethods = false)
class BlockGrassTest {
    static final BlockState STATE_DIRT = BlockState.of(BlockID.DIRT);
    static final BlockState STATE_GRASS = BlockState.of(BlockID.GRASS);
    
    int x = 1, y = 2, z = 3;
    
    @Mock
    Level level;
    
    BlockGrass grass;

    @Test
    void spread() {
        Map<Vector3, BlockState> states = Collections.synchronizedMap(new LinkedHashMap<>());
        when(level.getFullLight(any())).thenReturn(15);
        when(level.getBlock(any())).then(call-> {
            Vector3 pos = call.getArgument(0);
            return states.getOrDefault(pos, BlockState.AIR)
                    .getBlock((Level) call.getMock(), pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
        });
        when(level.getBlock(anyInt(), anyInt(), anyInt(), anyInt())).then(call-> {
            int x = call.getArgument(0);
            int y = call.getArgument(1);
            int z = call.getArgument(2);
            int layer = call.getArgument(3);
            if (layer != 0) return BlockState.AIR.getBlock(level, x, y, z, layer);
            return states.getOrDefault(new Vector3(x, y, z), BlockState.AIR).getBlock(level, x, y, z, layer);
        });
        when(level.setBlock(any(), any())).then(call-> {
            Vector3 pos = call.getArgument(0);
            Block block = call.getArgument(1);
            states.put(new Vector3(pos.x, pos.y, pos.z), block.getCurrentState());
            return null;
        });

        for (int y1 = y - 15; y1 <= y + 15; y1 += 2) {
            for (int x1 = x - 15; x1 <= x + 15; x1++) {
                for (int z1 = z - 15; z1 <= z + 15; z1++) {
                    states.put(new Vector3(x1, y1, z1), STATE_DIRT);
                }
            }
        }
        
        states.put(new Vector3(x, y+1, z), BlockState.AIR);
        states.put(new Vector3(x, y, z), STATE_GRASS);
        
        Map<Vector3, BlockState> expected = new LinkedHashMap<>(states);
        for (int y1 = y - 3; y1 <= y + 1; y1 += 2) {
            for (int x1 = x - 1; x1 <= x + 1; x1++) {
                for (int z1 = z - 1; z1 <= z + 1; z1++) {
                    expected.put(new Vector3(x1, y1, z1), STATE_GRASS);
                }
            }
        }

        expected.put(new Vector3(x, y + 1, z), BlockState.AIR);
        expected.put(new Vector3(x, y - 1, z), STATE_DIRT);

        for (int i = 0; i < 1_000; i++) {
            grass.onUpdate(Level.BLOCK_UPDATE_RANDOM);
        }
        
        verify(level, times(0)).setBlock(eq(new Vector3(x, y, z)), any());
        verify(Server.getInstance().getPluginManager(), times(9 * 3 - 2)).callEvent(isA(BlockSpreadEvent.class));
        assertEquals(expected, states);
    }
    
    @Test
    void decay() {
        when(level.getBlock(anyInt(), anyInt(), anyInt(), anyInt())).then(call-> {
            int x = call.getArgument(0);
            int y = call.getArgument(1);
            int z = call.getArgument(2);
            int layer = call.getArgument(3);
            return STATE_DIRT.getBlock(level, x, y, z, layer);
        });
        
        grass.onUpdate(Level.BLOCK_UPDATE_RANDOM);
        verify(level).setBlock(eq(new Vector3(x,y,z)), eq(Block.get(BlockID.DIRT)));
    }

    @BeforeEach
    void setUp() {
        grass = (BlockGrass) Block.get(BlockID.GRASS);
        grass.level = level;
        grass.x = x;
        grass.y = y;
        grass.z = z;
    }
}
