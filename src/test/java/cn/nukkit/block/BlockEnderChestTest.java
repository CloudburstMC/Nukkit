package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityEnderChest;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.powernukkit.tests.api.MockLevel;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * @author joserobjr
 */
@ExtendWith(PowerNukkitExtension.class)
class BlockEnderChestTest {
    @MockLevel
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
    void setUp() {
        lenient().when(player.getLevel()).thenReturn(level);
    }
}
