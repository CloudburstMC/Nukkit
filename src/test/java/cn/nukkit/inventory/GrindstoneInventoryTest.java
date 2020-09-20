package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.api.MockPlayer;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author joserobjr
 */
@ExtendWith(PowerNukkitExtension.class)
class GrindstoneInventoryTest {
    @MockPlayer
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
        playerUIInventory = player.getUIInventory();
        grindstoneInventory = new GrindstoneInventory(playerUIInventory, new Position());
    }
}
