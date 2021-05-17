package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.api.MockPlayer;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doCallRealMethod;

/**
 * @author joserobjr
 */
@ExtendWith(PowerNukkitExtension.class)
class EnchantInventoryTest {
    
    @MockPlayer
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
        playerInventory = player.getInventory();
        playerUIInventory = player.getUIInventory();
        
        enchantInventory = new EnchantInventory(playerUIInventory, new Position(1, 2, 3, player.getLevel()));
    }
}
