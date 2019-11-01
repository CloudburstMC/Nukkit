package cn.nukkit.network.protocol.types;

import cn.nukkit.Player;
import cn.nukkit.inventory.AnvilInventory;
import cn.nukkit.inventory.BeaconInventory;
import cn.nukkit.inventory.EnchantInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.transaction.action.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import lombok.ToString;

import java.util.Optional;

/**
 * @author CreeperFace
 */
@ToString
public class NetworkInventoryAction {

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


    public int sourceType;
    public int windowId;
    public long unknown;
    public int inventorySlot;
    public Item oldItem;
    public Item newItem;

    public NetworkInventoryAction read(InventoryTransactionPacket packet) {
        this.sourceType = (int) packet.getUnsignedVarInt();

        switch (this.sourceType) {
            case SOURCE_CONTAINER:
                this.windowId = packet.getVarInt();
                break;
            case SOURCE_WORLD:
                this.unknown = packet.getUnsignedVarInt();
                break;
            case SOURCE_CREATIVE:
                break;
            case SOURCE_CRAFT_SLOT:
            case SOURCE_TODO:
                this.windowId = packet.getVarInt();

                switch (this.windowId) {
                    case SOURCE_TYPE_CRAFTING_RESULT:
                    case SOURCE_TYPE_CRAFTING_USE_INGREDIENT:
                        packet.isCraftingPart = true;
                        break;
                }
                break;
        }

        this.inventorySlot = (int) packet.getUnsignedVarInt();
        this.oldItem = packet.getSlot();
        this.newItem = packet.getSlot();

        return this;
    }

    public void write(InventoryTransactionPacket packet) {
        packet.putUnsignedVarInt(this.sourceType);

        switch (this.sourceType) {
            case SOURCE_CONTAINER:
                packet.putVarInt(this.windowId);
                break;
            case SOURCE_WORLD:
                packet.putUnsignedVarInt(this.unknown);
                break;
            case SOURCE_CREATIVE:
                break;
            case SOURCE_CRAFT_SLOT:
            case SOURCE_TODO:
                packet.putVarInt(this.windowId);
                break;
        }

        packet.putUnsignedVarInt(this.inventorySlot);
        packet.putSlot(this.oldItem);
        packet.putSlot(this.newItem);
    }

    public InventoryAction createInventoryAction(Player player) {
        switch (this.sourceType) {
            case SOURCE_CONTAINER:
                if (this.windowId == ContainerIds.ARMOR) {
                    //TODO: HACK!
                    this.inventorySlot += 36;
                    this.windowId = ContainerIds.INVENTORY;
                }

                Inventory window = player.getWindowById(this.windowId);
                if (window != null) {
                    return new SlotChangeAction(window, this.inventorySlot, this.oldItem, this.newItem);
                }

                player.getServer().getLogger().debug("Player " + player.getName() + " has no open container with window ID " + this.windowId);
                return null;
            case SOURCE_WORLD:
                if (this.inventorySlot != InventoryTransactionPacket.ACTION_MAGIC_SLOT_DROP_ITEM) {
                    player.getServer().getLogger().debug("Only expecting drop-item world actions from the client!");
                    return null;
                }

                return new DropItemAction(this.oldItem, this.newItem);
            case SOURCE_CREATIVE:
                int type;

                switch (this.inventorySlot) {
                    case InventoryTransactionPacket.ACTION_MAGIC_SLOT_CREATIVE_DELETE_ITEM:
                        type = CreativeInventoryAction.TYPE_DELETE_ITEM;
                        break;
                    case InventoryTransactionPacket.ACTION_MAGIC_SLOT_CREATIVE_CREATE_ITEM:
                        type = CreativeInventoryAction.TYPE_CREATE_ITEM;
                        break;
                    default:
                        player.getServer().getLogger().debug("Unexpected creative action type " + this.inventorySlot);
                        return null;
                }

                return new CreativeInventoryAction(this.oldItem, this.newItem, type);
            case SOURCE_CRAFT_SLOT:
            case SOURCE_TODO:
                //These types need special handling.
                switch (this.windowId) {
                    case SOURCE_TYPE_CRAFTING_ADD_INGREDIENT:
                    case SOURCE_TYPE_CRAFTING_REMOVE_INGREDIENT:
                        return new SlotChangeAction(player.getCraftingGrid(), this.inventorySlot, this.oldItem, this.newItem);
                    case SOURCE_TYPE_CONTAINER_DROP_CONTENTS:
                        Optional<Inventory> inventory = player.getTopWindow();
                        if (!inventory.isPresent()) {
                            // No window open?
                            return null;
                        }
                        return new SlotChangeAction(inventory.get(), this.inventorySlot, this.oldItem, this.newItem);
                    case SOURCE_TYPE_CRAFTING_RESULT:
                        return new CraftingTakeResultAction(this.oldItem, this.newItem);
                    case SOURCE_TYPE_CRAFTING_USE_INGREDIENT:
                        return new CraftingTransferMaterialAction(this.oldItem, this.newItem, this.inventorySlot);
                }

                if (this.windowId >= SOURCE_TYPE_ANVIL_OUTPUT && this.windowId <= SOURCE_TYPE_ANVIL_INPUT) { //anvil actions
                    Inventory inv = player.getWindowById(Player.ANVIL_WINDOW_ID);

                    if (!(inv instanceof AnvilInventory)) {
                        player.getServer().getLogger().debug("Player " + player.getName() + " has no open anvil inventory");
                        return null;
                    }
                    AnvilInventory anvil = (AnvilInventory) inv;

                    switch (this.windowId) {
                        case SOURCE_TYPE_ANVIL_INPUT:
                            //System.out.println("action input");
                            this.inventorySlot = 0;
                            return new SlotChangeAction(anvil, this.inventorySlot, this.oldItem, this.newItem);
                        case SOURCE_TYPE_ANVIL_MATERIAL:
                            //System.out.println("material");
                            this.inventorySlot = 1;
                            return new SlotChangeAction(anvil, this.inventorySlot, this.oldItem, this.newItem);
                        case SOURCE_TYPE_ANVIL_OUTPUT:
                            //System.out.println("action output");
                            break;
                        case SOURCE_TYPE_ANVIL_RESULT:
                            this.inventorySlot = 2;
                            anvil.clear(0);
                            Item material = anvil.getItem(1);
                            if (!material.isNull()) {
                                material.setCount(material.getCount() - 1);
                                anvil.setItem(1, material);
                            }
                            anvil.setItem(2, this.oldItem);
                            //System.out.println("action result");
                            return new SlotChangeAction(anvil, this.inventorySlot, this.oldItem, this.newItem);
                    }
                }

                if (this.windowId >= SOURCE_TYPE_ENCHANT_OUTPUT && this.windowId <= SOURCE_TYPE_ENCHANT_INPUT) {
                    Inventory inv = player.getWindowById(Player.ENCHANT_WINDOW_ID);

                    if (!(inv instanceof EnchantInventory)) {
                        player.getServer().getLogger().debug("Player " + player.getName() + " has no open enchant inventory");
                        return null;
                    }
                    EnchantInventory enchant = (EnchantInventory) inv;

                    // TODO: This is all a temporary hack. Enchanting needs it's own transaction class.
                    switch (this.windowId) {
                        case SOURCE_TYPE_ENCHANT_INPUT:
                            if (this.inventorySlot != 0) {
                                // Input should only be in slot 0.
                                return null;
                            }
                            break;
                        case SOURCE_TYPE_ENCHANT_MATERIAL:
                            if (this.inventorySlot != 1) {
                                // Material should only be in slot 1.
                                return null;
                            }
                            break;
                        case SOURCE_TYPE_ENCHANT_OUTPUT:
                            if (this.inventorySlot != 0) {
                                // Outputs should only be in slot 0.
                                return null;
                            }
                            if (Item.get(ItemID.DYE, 4).equals(this.newItem, true, false)) {
                                this.inventorySlot = 2; // Fake slot to store used material
                                if (this.newItem.getCount() < 1 || this.newItem.getCount() > 3) {
                                    // Invalid material
                                    return null;
                                }
                                Item material = enchant.getItem(1);
                                // Material to take away.
                                int toRemove = this.newItem.getCount();
                                if (material.getId() != ItemID.DYE && material.getDamage() != 4 &&
                                        material.getCount() < toRemove) {
                                    // Invalid material or not enough
                                    return null;
                                }
                            } else {
                                Item toEnchant = enchant.getItem(0);
                                Item material = enchant.getItem(1);
                                if (toEnchant.equals(this.newItem, true, true) &&
                                        (material.getId() == ItemID.DYE && material.getDamage() == 4 || player.isCreative())) {
                                    this.inventorySlot = 3; // Fake slot to store the resultant item.

                                    //TODO: Check (old) item has valid enchantments
                                    enchant.setItem(3, this.oldItem, false);
                                } else {
                                    return null;
                                }
                            }
                    }

                    return new SlotChangeAction(enchant, this.inventorySlot, this.oldItem, this.newItem);
                }

                if (this.windowId == SOURCE_TYPE_BEACON) {
                    Inventory inv = player.getWindowById(Player.BEACON_WINDOW_ID);

                    if (!(inv instanceof BeaconInventory)) {
                        player.getServer().getLogger().debug("Player " + player.getName() + " has no open beacon inventory");
                        return null;
                    }
                    BeaconInventory beacon = (BeaconInventory) inv;

                    this.inventorySlot = 0;
                    return new SlotChangeAction(beacon, this.inventorySlot, this.oldItem, this.newItem);
                }

                //TODO: more stuff
                player.getServer().getLogger().debug("Player " + player.getName() + " has no open container with window ID " + this.windowId);
                return null;
            default:
                player.getServer().getLogger().debug("Unknown inventory source type " + this.sourceType);
                return null;
        }
    }
}
