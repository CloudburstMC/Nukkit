package cn.nukkit.network.protocol.types;

import cn.nukkit.inventory.AnvilInventory;
import cn.nukkit.inventory.BeaconInventory;
import cn.nukkit.inventory.EnchantInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.transaction.action.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.player.Player;
import com.nukkitx.protocol.bedrock.data.InventoryActionData;
import com.nukkitx.protocol.bedrock.data.InventorySource;
import com.nukkitx.protocol.bedrock.packet.InventoryTransactionPacket;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;

/**
 * @author CreeperFace
 */
@Log4j2
@ToString
public class InventoryTransactionUtils {

    public static final int SOURCE_CONTAINER = 0;

    public static final int SOURCE_WORLD = 2; //drop/pickup item entity
    public static final int SOURCE_CREATIVE = 3;
    public static final int SOURCE_TODO = 99999;
    public static final int SOURCE_CRAFT_SLOT = 100;

    /**
     * Fake window IDs for the SOURCE_TODO type (99999)
     * <p>
     * These identifiers are used for inventory source types which are not currently implemented server-side in MCPE.
     * As a general rule of thumb, anything that doesn't have a permanent inventory is client-side. These types are
     * to allow servers to track what is going on in client-side windows.
     * <p>
     * Expect these to change in the future.
     */
    public static final int SOURCE_TYPE_CRAFTING_ADD_INGREDIENT = -2;
    public static final int SOURCE_TYPE_CRAFTING_REMOVE_INGREDIENT = -3;
    public static final int SOURCE_TYPE_CRAFTING_RESULT = -4;
    public static final int SOURCE_TYPE_CRAFTING_USE_INGREDIENT = -5;

    public static final int SOURCE_TYPE_ANVIL_INPUT = -10;
    public static final int SOURCE_TYPE_ANVIL_MATERIAL = -11;
    public static final int SOURCE_TYPE_ANVIL_RESULT = -12;
    public static final int SOURCE_TYPE_ANVIL_OUTPUT = -13;

    public static final int SOURCE_TYPE_ENCHANT_INPUT = -15;
    public static final int SOURCE_TYPE_ENCHANT_MATERIAL = -16;
    public static final int SOURCE_TYPE_ENCHANT_OUTPUT = -17;

    public static final int SOURCE_TYPE_TRADING_INPUT_1 = -20;
    public static final int SOURCE_TYPE_TRADING_INPUT_2 = -21;
    public static final int SOURCE_TYPE_TRADING_USE_INPUTS = -22;
    public static final int SOURCE_TYPE_TRADING_OUTPUT = -23;

    public static final int SOURCE_TYPE_BEACON = -24;

    /**
     * Any client-side window dropping its contents when the player closes it
     */
    public static final int SOURCE_TYPE_CONTAINER_DROP_CONTENTS = -100;

    public static final int USE_ITEM_ACTION_CLICK_BLOCK = 0;
    public static final int USE_ITEM_ACTION_CLICK_AIR = 1;
    public static final int USE_ITEM_ACTION_BREAK_BLOCK = 2;

    public static final int RELEASE_ITEM_ACTION_RELEASE = 0; //bow shoot
    public static final int RELEASE_ITEM_ACTION_CONSUME = 1; //eat food, drink potion

    public static final int USE_ITEM_ON_ENTITY_ACTION_INTERACT = 0;
    public static final int USE_ITEM_ON_ENTITY_ACTION_ATTACK = 1;


    public static final int ACTION_MAGIC_SLOT_DROP_ITEM = 0;
    public static final int ACTION_MAGIC_SLOT_PICKUP_ITEM = 1;

    public static final int ACTION_MAGIC_SLOT_CREATIVE_DELETE_ITEM = 0;
    public static final int ACTION_MAGIC_SLOT_CREATIVE_CREATE_ITEM = 1;

    public static boolean containsCraftingPart(InventoryTransactionPacket packet) {
        for (InventoryActionData inventoryActionData : packet.getActions()) {
            InventorySource source = inventoryActionData.getSource();
            if (source.getType() == InventorySource.Type.NON_IMPLEMENTED_TODO &&
                    source.getContainerId() == SOURCE_TYPE_CRAFTING_RESULT ||
                    source.getContainerId() == SOURCE_TYPE_CRAFTING_USE_INGREDIENT) {
                return true;
            }
        }
        return false;
    }

    public static InventoryAction createInventoryAction(Player player, InventoryActionData inventoryActionData) {
        InventorySource source = inventoryActionData.getSource();
        int containerId = source.getContainerId();
        int slot = inventoryActionData.getSlot();
        Item oldItem = Item.fromNetwork(inventoryActionData.getFromItem());
        Item newItem = Item.fromNetwork(inventoryActionData.getToItem());

        switch (source.getType()) {
            case CONTAINER:
                if (containerId == ContainerIds.ARMOR) {
                    //TODO: HACK!
                    slot += 36;
                    containerId = ContainerIds.INVENTORY;
                }

                Inventory window = player.getWindowById(containerId);
                if (window != null) {
                    return new SlotChangeAction(window, slot, oldItem, newItem);
                }

                log.debug("Player " + player.getName() + " has no open container with container ID " + containerId);
                return null;
            case WORLD_INTERACTION:
                if (slot != ACTION_MAGIC_SLOT_DROP_ITEM) {
                    log.debug("Only expecting drop-item world actions from the client!");
                    return null;
                }

                return new DropItemAction(oldItem, newItem);
            case CREATIVE:
                int type;

                switch (slot) {
                    case ACTION_MAGIC_SLOT_CREATIVE_DELETE_ITEM:
                        type = CreativeInventoryAction.TYPE_DELETE_ITEM;
                        break;
                    case ACTION_MAGIC_SLOT_CREATIVE_CREATE_ITEM:
                        type = CreativeInventoryAction.TYPE_CREATE_ITEM;
                        break;
                    default:
                        log.debug("Unexpected creative action type " + slot);
                        return null;
                }

                return new CreativeInventoryAction(oldItem, newItem, type);
            case UNTRACKED_INTERACTION_UI:
            case NON_IMPLEMENTED_TODO:
                //These types need special handling.
                switch (containerId) {
                    case SOURCE_TYPE_CRAFTING_ADD_INGREDIENT:
                    case SOURCE_TYPE_CRAFTING_REMOVE_INGREDIENT:
                        return new SlotChangeAction(player.getCraftingGrid(), slot, oldItem, newItem);
                    case SOURCE_TYPE_CONTAINER_DROP_CONTENTS:
                        Optional<Inventory> inventory = player.getTopWindow();
                        if (!inventory.isPresent()) {
                            // No window open?
                            return null;
                        }
                        return new SlotChangeAction(inventory.get(), slot, oldItem, newItem);
                    case SOURCE_TYPE_CRAFTING_RESULT:
                        return new CraftingTakeResultAction(oldItem, newItem);
                    case SOURCE_TYPE_CRAFTING_USE_INGREDIENT:
                        return new CraftingTransferMaterialAction(oldItem, newItem, slot);
                }

                if (containerId >= SOURCE_TYPE_ANVIL_OUTPUT && containerId <= SOURCE_TYPE_ANVIL_INPUT) { //anvil actions
                    Inventory inv = player.getWindowById(ContainerIds.ANVIL);

                    if (!(inv instanceof AnvilInventory)) {
                        log.debug("Player " + player.getName() + " has no open anvil inventory");
                        return null;
                    }
                    AnvilInventory anvil = (AnvilInventory) inv;

                    switch (containerId) {
                        case SOURCE_TYPE_ANVIL_INPUT:
                            //System.out.println("action input");
                            slot = 0;
                            return new SlotChangeAction(anvil, slot, oldItem, newItem);
                        case SOURCE_TYPE_ANVIL_MATERIAL:
                            //System.out.println("material");
                            slot = 1;
                            return new SlotChangeAction(anvil, slot, oldItem, newItem);
                        case SOURCE_TYPE_ANVIL_OUTPUT:
                            //System.out.println("action output");
                            break;
                        case SOURCE_TYPE_ANVIL_RESULT:
                            slot = 2;
                            anvil.clear(0);
                            Item material = anvil.getItem(1);
                            if (!material.isNull()) {
                                material.setCount(material.getCount() - 1);
                                anvil.setItem(1, material);
                            }
                            anvil.setItem(2, oldItem);
                            //System.out.println("action result");
                            return new SlotChangeAction(anvil, slot, oldItem, newItem);
                    }
                }

                if (containerId >= SOURCE_TYPE_ENCHANT_OUTPUT && containerId <= SOURCE_TYPE_ENCHANT_INPUT) {
                    Inventory inv = player.getWindowById(ContainerIds.ENCHANTING_TABLE);

                    if (!(inv instanceof EnchantInventory)) {
                        log.debug("Player " + player.getName() + " has no open enchant inventory");
                        return null;
                    }
                    EnchantInventory enchant = (EnchantInventory) inv;

                    // TODO: This is all a temporary hack. Enchanting needs it's own transaction class.
                    switch (containerId) {
                        case SOURCE_TYPE_ENCHANT_INPUT:
                            if (slot != 0) {
                                // Input should only be in slot 0.
                                return null;
                            }
                            break;
                        case SOURCE_TYPE_ENCHANT_MATERIAL:
                            if (slot != 1) {
                                // Material should only be in slot 1.
                                return null;
                            }
                            break;
                        case SOURCE_TYPE_ENCHANT_OUTPUT:
                            if (slot != 0) {
                                // Outputs should only be in slot 0.
                                return null;
                            }
                            if (Item.get(ItemIds.DYE, 4).equals(newItem, true, false)) {
                                slot = 2; // Fake slot to store used material
                                if (newItem.getCount() < 1 || newItem.getCount() > 3) {
                                    // Invalid material
                                    return null;
                                }
                                Item material = enchant.getItem(1);
                                // Material to take away.
                                int toRemove = newItem.getCount();
                                if (material.getId() != ItemIds.DYE && material.getMeta() != 4 &&
                                        material.getCount() < toRemove) {
                                    // Invalid material or not enough
                                    return null;
                                }
                            } else {
                                Item toEnchant = enchant.getItem(0);
                                Item material = enchant.getItem(1);
                                if (toEnchant.equals(newItem, true, true) &&
                                        (material.getId() == ItemIds.DYE && material.getMeta() == 4 || player.isCreative())) {
                                    slot = 3; // Fake slot to store the resultant item.

                                    //TODO: Check (old) item has valid enchantments
                                    enchant.setItem(3, oldItem, false);
                                } else {
                                    return null;
                                }
                            }
                    }

                    return new SlotChangeAction(enchant, slot, oldItem, newItem);
                }

                if (containerId == SOURCE_TYPE_BEACON) {
                    Inventory inv = player.getWindowById(ContainerIds.BEACON);

                    if (!(inv instanceof BeaconInventory)) {
                        log.debug("Player " + player.getName() + " has no open beacon inventory");
                        return null;
                    }
                    BeaconInventory beacon = (BeaconInventory) inv;

                    slot = 0;
                    return new SlotChangeAction(beacon, slot, oldItem, newItem);
                }

                //TODO: more stuff
                log.debug("Player " + player.getName() + " has no open container with window ID " + containerId);
                return null;
            default:
                log.debug("Unknown inventory source type " + source.getType());
                return null;
        }
    }
}
