package cn.nukkit.network.protocol.types;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.inventory.*;
import cn.nukkit.inventory.transaction.action.*;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;

/**
 * @author CreeperFace
 */
@ToString
@Log4j2
public class NetworkInventoryAction {
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final NetworkInventoryAction[] EMPTY_ARRAY = new NetworkInventoryAction[0];

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
    @Since("1.3.0.0-PN")
    public int stackNetworkId;

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
                    case SOURCE_TYPE_ANVIL_INPUT:
                    case SOURCE_TYPE_ANVIL_MATERIAL:
                    case SOURCE_TYPE_ANVIL_OUTPUT:
                    case SOURCE_TYPE_ANVIL_RESULT:
                        packet.isCraftingPart = true;
                        break;
                    case SOURCE_TYPE_ENCHANT_INPUT:
                    case SOURCE_TYPE_ENCHANT_OUTPUT:
                    case SOURCE_TYPE_ENCHANT_MATERIAL:
                        packet.isEnchantingPart = true;
                        break;
                }
                break;
        }

        this.inventorySlot = (int) packet.getUnsignedVarInt();
        this.oldItem = packet.getSlot();
        this.newItem = packet.getSlot();

        if (packet.hasNetworkIds) {
            this.stackNetworkId = packet.getVarInt();
        }
        return this;
    }

    public void write(InventoryTransactionPacket packet) {
        packet.putUnsignedVarInt(this.sourceType);

        switch (this.sourceType) {
            case SOURCE_CONTAINER:
            case SOURCE_CRAFT_SLOT:
            case SOURCE_TODO:
                packet.putVarInt(this.windowId);
                break;
            case SOURCE_WORLD:
                packet.putUnsignedVarInt(this.unknown);
                break;
            case SOURCE_CREATIVE:
                break;
        }

        packet.putUnsignedVarInt(this.inventorySlot);
        packet.putSlot(this.oldItem);
        packet.putSlot(this.newItem);

        if (packet.hasNetworkIds) {
            packet.putVarInt(this.stackNetworkId);
        }
    }

    public InventoryAction createInventoryAction(Player player) {
        switch (this.sourceType) {
            case SOURCE_CONTAINER:
                if (this.windowId == ContainerIds.ARMOR) {
                    //TODO: HACK!
                    this.inventorySlot += 36;
                    this.windowId = ContainerIds.INVENTORY;
                }
                // ID 124 with slot 14/15 is enchant inventory
                if (this.windowId == ContainerIds.UI) {
                    if (this.inventorySlot == EnchantInventory.ENCHANT_INPUT_ITEM_UI_SLOT) {
                        if (player.getWindowById(Player.ENCHANT_WINDOW_ID) == null) {
                            log.error("Player {} does not have enchant window open", player.getName());
                            return null;
                        }
                        this.windowId = Player.ENCHANT_WINDOW_ID;
                        this.inventorySlot = 0;
                        // TODO, check if unenchanted item and send EnchantOptionsPacket
                    } else if (this.inventorySlot == EnchantInventory.ENCHANT_REAGENT_UI_SLOT) {
                        if (player.getWindowById(Player.ENCHANT_WINDOW_ID) == null) {
                            log.error("Player {} does not have enchant window open", player.getName());
                            return null;
                        }
                        this.windowId = Player.ENCHANT_WINDOW_ID;
                        this.inventorySlot = 1;
                    }
                }

                Inventory window = player.getWindowById(this.windowId);
                if (window != null) {
                    return new SlotChangeAction(window, this.inventorySlot, this.oldItem, this.newItem);
                }

                log.debug("Player {} has no open container with window ID {}", player.getName(), this.windowId);
                return null;
            case SOURCE_WORLD:
                if (this.inventorySlot != InventoryTransactionPacket.ACTION_MAGIC_SLOT_DROP_ITEM) {
                    log.debug("Only expecting drop-item world actions from the client!");
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
                        log.debug("Unexpected creative action type {}", this.inventorySlot);
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

                    if (inv instanceof AnvilInventory) {
                        AnvilInventory anvil = (AnvilInventory) inv;

                        switch (this.windowId) {
                            case SOURCE_TYPE_ANVIL_INPUT:
                                this.inventorySlot = 0;
                                return new CraftingTransferMaterialAction(this.oldItem, this.newItem, this.inventorySlot);
                            case SOURCE_TYPE_ANVIL_MATERIAL:
                                this.inventorySlot = 1;
                                return new CraftingTransferMaterialAction(this.oldItem, this.newItem, this.inventorySlot);
                            case SOURCE_TYPE_ANVIL_OUTPUT:
                                //System.out.println("action output");
                                break;
                            case SOURCE_TYPE_ANVIL_RESULT:
                                this.inventorySlot = 2;
                                anvil.setNewItemName(this.oldItem.getCustomName());
                                anvil.updateResult();
                                return new CraftingTakeResultAction(this.oldItem, this.newItem);
                        }
                    } else if (inv instanceof GrindstoneInventory) {
                        switch (this.windowId) {
                            case SOURCE_TYPE_ANVIL_INPUT:
                                this.inventorySlot = 0;
                                return new CraftingTransferMaterialAction(this.oldItem, this.newItem, this.inventorySlot);
                            case SOURCE_TYPE_ANVIL_MATERIAL:
                                this.inventorySlot = 1;
                                return new CraftingTransferMaterialAction(this.oldItem, this.newItem, this.inventorySlot);
                            case SOURCE_TYPE_ANVIL_OUTPUT:
                                //System.out.println("action output");
                                break;
                            case SOURCE_TYPE_ANVIL_RESULT:
                                this.inventorySlot = 2;
                                return new CraftingTakeResultExperienceAction(this.oldItem, this.newItem, ((GrindstoneInventory) inv).getResultExperience());
                        }
                    } else {
                        log.debug("Player {} has no open anvil or grindstone inventory", player.getName());
                        return null;
                    }
                }

                if (this.windowId >= SOURCE_TYPE_ENCHANT_OUTPUT && this.windowId <= SOURCE_TYPE_ENCHANT_INPUT) {
                    Inventory inv = player.getWindowById(Player.ENCHANT_WINDOW_ID);

                    if (!(inv instanceof EnchantInventory)) {
                        log.debug("Player {} has no open enchant inventory", player.getName());
                        return null;
                    }
                    EnchantInventory enchant = (EnchantInventory) inv;

                    switch (this.windowId) {
                        case SOURCE_TYPE_ENCHANT_INPUT:
                            return new EnchantingAction(this.oldItem, this.newItem, SOURCE_TYPE_ENCHANT_INPUT);
                        case SOURCE_TYPE_ENCHANT_MATERIAL:
                            return new EnchantingAction(this.newItem, this.oldItem, SOURCE_TYPE_ENCHANT_MATERIAL); // Mojang ish backwards?
                        case SOURCE_TYPE_ENCHANT_OUTPUT:
                            return new EnchantingAction(this.oldItem, this.newItem, SOURCE_TYPE_ENCHANT_OUTPUT);
                    }

                    return new SlotChangeAction(enchant, this.inventorySlot, this.oldItem, this.newItem);
                }

                if (this.windowId == SOURCE_TYPE_BEACON) {
                    Inventory inv = player.getWindowById(Player.BEACON_WINDOW_ID);

                    if (!(inv instanceof BeaconInventory)) {
                        log.debug("Player {} has no open beacon inventory", player.getName());
                        return null;
                    }
                    BeaconInventory beacon = (BeaconInventory) inv;

                    this.inventorySlot = 0;
                    return new SlotChangeAction(beacon, this.inventorySlot, this.oldItem, this.newItem);
                }

                //TODO: more stuff
                log.debug("Player {} has no open container with window ID {}", player.getName(), this.windowId);
                return null;
            default:
                log.debug("Unknown inventory source type {}", this.sourceType);
                return null;
        }
    }
}
