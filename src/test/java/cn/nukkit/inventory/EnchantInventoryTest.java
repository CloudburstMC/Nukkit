package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.ServerTest;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author joserobjr
 */
@ExtendWith(MockitoExtension.class)
class EnchantInventoryTest {
    @Mock
    PluginManager pluginManager;
    
    @Mock
    Server server;
    
    @Mock
    Level level;

    @Mock
    Player player;
    
    PlayerInventory playerInventory;

    PlayerUIInventory playerUIInventory;

    EnchantInventory enchantInventory;

    @Test
    void close() {
        doCallRealMethod().when(player).resetCraftingGridType();
        enchantInventory.setItem(0, Item.get(ItemID.IRON_SWORD));
        enchantInventory.close(player);
        int count = 0;
        for (int i = 0; i < playerInventory.getSize(); i++) {
            if (playerInventory.getItem(i).getId() == ItemID.IRON_SWORD) {
                count++;
            }
        }
        for (int i = 0; i < playerUIInventory.getSize(); i++) {
            if (playerUIInventory.getItem(i).getId() == ItemID.IRON_SWORD) {
                count++;
            }
        }
        assertEquals(1, count, "The sword was duplicated!");
    }

    @BeforeEach
    void setUp() {
        playerInventory = spy(new PlayerInventory(player));
        playerUIInventory = spy(new PlayerUIInventory(player));
        
        lenient().when(player.getInventory()).thenReturn(playerInventory);
        lenient().when(player.getUIInventory()).thenReturn(playerUIInventory);
        lenient().when(player.getLevel()).thenReturn(level);
        lenient().when(player.getServer()).thenReturn(server);
        lenient().when(level.getServer()).thenReturn(server);
        lenient().when(server.getPluginManager()).thenReturn(pluginManager);
        ServerTest.setInstance(server);
        
        enchantInventory = new EnchantInventory(playerUIInventory, new Position(1, 2, 3, level));
    }

    @BeforeAll
    static void beforeAll() {
        Block.init();
        Item.init();
    }
}
