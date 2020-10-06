package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.DyeColor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.powernukkit.tests.api.MockServer;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

/**
 * @author joserobjr
 */
@ExtendWith(PowerNukkitExtension.class)
@MockServer(callsRealMethods = false)
class BlockKelpTest {
    
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
}
