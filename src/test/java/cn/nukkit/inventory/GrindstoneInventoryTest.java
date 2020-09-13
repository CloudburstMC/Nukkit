package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.ServerTest;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author joserobjr
 */
@ExtendWith(MockitoExtension.class)
class GrindstoneInventoryTest {
    
    @Mock
    Server server;
    
    @Mock
    PluginManager pluginManager;
    
    @Mock
    Player player;
            
    PlayerUIInventory playerUIInventory;
    
    GrindstoneInventory grindstoneInventory;
    
    @Test
    void enchantedBook() {
        Item enchantedBook = Item.get(ItemID.ENCHANTED_BOOK, 0, 4);
        enchantedBook.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_FORTUNE_FISHING).setLevel(2));
        grindstoneInventory.setFirstItem(enchantedBook.clone(), false);
        
        assertEquals(Item.get(ItemID.BOOK, 0, 4), grindstoneInventory.getResult());
        for (int i = 0; i < 20; i++) {
            assertThat(grindstoneInventory.getResultExperience()).isBetween(12 * 4, 24 * 4);
            grindstoneInventory.recalculateResultExperience();
        }
        
        grindstoneInventory.setSecondItem(enchantedBook.clone(), false);

        Item air = Item.get(0);
        assertEquals(air, grindstoneInventory.getResult());
        assertEquals(0, grindstoneInventory.getResultExperience());
        
        grindstoneInventory.setFirstItem(air, false);

        assertEquals(Item.get(ItemID.BOOK, 0, 4), grindstoneInventory.getResult());
        for (int i = 0; i < 20; i++) {
            assertThat(grindstoneInventory.getResultExperience()).isBetween(12 * 4, 24 * 4);
            grindstoneInventory.recalculateResultExperience();
        }
        
        enchantedBook.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_DURABILITY));
        enchantedBook.setCount(1);
        grindstoneInventory.setSecondItem(enchantedBook.clone());

        assertEquals(Item.get(ItemID.BOOK, 0, 4), grindstoneInventory.getResult());
        for (int i = 0; i < 100; i++) {
            assertThat(grindstoneInventory.getResultExperience()).isBetween(12 + 3, 24 + 5);
            grindstoneInventory.recalculateResultExperience();
        }
    }

    @BeforeEach
    void setUp() {
        ServerTest.setInstance(server);
        playerUIInventory = new PlayerUIInventory(player);
        grindstoneInventory = new GrindstoneInventory(playerUIInventory, new Position());
        
        when(server.getPluginManager()).thenReturn(pluginManager);
    }

    @BeforeAll
    static void beforeAll() {
        Block.init();
        Item.init();
        Enchantment.init();
    }
}
