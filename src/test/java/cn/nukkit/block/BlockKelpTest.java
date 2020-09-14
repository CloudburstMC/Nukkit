package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.ServerTest;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.utils.DyeColor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author joserobjr
 */
@ExtendWith(MockitoExtension.class)
class BlockKelpTest {
    
    @Mock
    PluginManager pluginManager;
    
    @Mock
    Server server;
    
    @Mock
    Level level;
    
    @Mock
    Player player;

    @Test
    void onActivate() {
        BlockKelp kelp = new BlockKelp();
        kelp.position(new Position(2, 3, 4, level));
        Item boneMeal = Item.get(ItemID.DYE, DyeColor.WHITE.getDyeData(), 5);
        
        when(level.getBlock(eq(2), eq(3), eq(4))).thenReturn(kelp);
        when(level.getBlockStateAt(eq(2), eq(4), eq(4))).thenReturn(BlockState.of(BlockID.WATER));
        when(level.getBlock(eq(2), eq(4), eq(4), eq(0)))
                .thenReturn(BlockState.of(BlockID.WATER).getBlock(level, 2, 4, 4));
        
        assertTrue(kelp.onActivate(boneMeal, player));
        
        assertEquals(4, boneMeal.getCount());
    }

    @BeforeEach
    void setUp() {
        ServerTest.setInstance(server);
        lenient().when(server.getPluginManager()).thenReturn(pluginManager);
    }

    @BeforeAll
    static void beforeAll() {
        Block.init();
        Item.init();
    }
}
