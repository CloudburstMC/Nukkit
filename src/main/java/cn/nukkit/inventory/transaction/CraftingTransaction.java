package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.inventory.BigCraftingGrid;
import cn.nukkit.inventory.CraftingRecipe;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.types.ContainerIds;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CreeperFace
 */
public class CraftingTransaction extends InventoryTransaction {

    protected int gridSize;

    protected List<Item> inputs;

    protected List<Item> secondaryOutputs;

    protected Item primaryOutput;

    protected CraftingRecipe recipe;

    public CraftingTransaction(Player source, List<InventoryAction> actions) {
        super(source, actions, false);

        this.gridSize = (source.getCraftingGrid() instanceof BigCraftingGrid) ? 3 : 2;

        this.inputs = new ArrayList<>();

        this.secondaryOutputs = new ArrayList<>();

        init(source, actions);
    }

    public void setInput(Item item) {
        if (inputs.size() < gridSize * gridSize) {
            for (Item existingInput : this.inputs) {
                if (existingInput.equals(item, item.hasMeta(), item.hasCompoundTag())) {
                    existingInput.setCount(existingInput.getCount() + item.getCount());
                    return;
                }
            }
            inputs.add(item.clone());
        } else {
            throw new RuntimeException("Input list is full can't add " + item);
        }
    }

    public List<Item> getInputList() {
        return inputs;
    }

    public void setExtraOutput(Item item) {
        if (secondaryOutputs.size() < gridSize * gridSize) {
            secondaryOutputs.add(item.clone());
        } else {
            throw new RuntimeException("Output list is full can't add " + item);
        }
    }

    public Item getPrimaryOutput() {
        return primaryOutput;
    }

    public void setPrimaryOutput(Item item) {
        if (primaryOutput == null) {
            primaryOutput = item.clone();
        } else if (!primaryOutput.equals(item)) {
            throw new RuntimeException("Primary result item has already been set and does not match the current item (expected " + primaryOutput + ", got " + item + ')');
        }
    }

    public CraftingRecipe getRecipe() {
        return recipe;
    }

    public boolean canExecute() {
        this.recipe = source.getServer().getCraftingManager().matchRecipe(inputs, this.primaryOutput, this.secondaryOutputs);
        return this.recipe != null && super.canExecute();
    }

    protected boolean callExecuteEvent() {
        CraftItemEvent ev;

        this.source.getServer().getPluginManager().callEvent(ev = new CraftItemEvent(this));
        return !ev.isCancelled();
    }

    protected void sendInventories() {
        super.sendInventories();

        if (source.craftingType == Player.CRAFTING_SMALL) {
            return; // Already closed
        }

        /*
         * TODO: HACK!
         * we can't resend the contents of the crafting window, so we force the client to close it instead.
         * So people don't whine about messy desync issues when someone cancels CraftItemEvent, or when a crafting
         * transaction goes wrong.
         */
        source.getServer().getScheduler().scheduleDelayedTask(null, () -> {
            if (source.isOnline() && source.isAlive()) {
                ContainerClosePacket pk = new ContainerClosePacket();
                pk.windowId = ContainerIds.NONE;
                pk.wasServerInitiated = true;
                source.dataPacket(pk);
            }
        }, 10);

        this.source.resetCraftingGridType();
    }

    public boolean execute() {
        if (super.execute()) {
            switch (this.primaryOutput.getId()) {
                case Item.CRAFTING_TABLE:
                    source.awardAchievement("buildWorkBench");
                    break;
                case Item.WOODEN_PICKAXE:
                    source.awardAchievement("buildPickaxe");
                    break;
                case Item.FURNACE:
                    source.awardAchievement("buildFurnace");
                    break;
                case Item.WOODEN_HOE:
                    source.awardAchievement("buildHoe");
                    break;
                case Item.BREAD:
                    source.awardAchievement("makeBread");
                    break;
                case Item.CAKE:
                    source.awardAchievement("bakeCake");
                    break;
                case Item.STONE_PICKAXE:
                case Item.GOLDEN_PICKAXE:
                case Item.IRON_PICKAXE:
                case Item.DIAMOND_PICKAXE:
                case Item.NETHERITE_PICKAXE:
                    source.awardAchievement("buildBetterPickaxe");
                    break;
                case Item.WOODEN_SWORD:
                case Item.STONE_SWORD:
                case Item.GOLDEN_SWORD:
                case Item.IRON_SWORD:
                case Item.DIAMOND_SWORD:
                case Item.NETHERITE_SWORD:
                    source.awardAchievement("buildSword");
                    break;
                case Item.ENCHANT_TABLE:
                    source.awardAchievement("enchantments");
                    break;
                case Item.BOOKSHELF:
                    source.awardAchievement("bookcase");
                    break;
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean checkForItemPart(List<InventoryAction> actions) {
        for (InventoryAction action : actions) {
            if (action instanceof SlotChangeAction) {
                SlotChangeAction slotChangeAction = (SlotChangeAction) action;
                if (slotChangeAction.getInventory().getType() == InventoryType.UI) {
                    if (slotChangeAction.getSlot() == 50) {
                        if (!slotChangeAction.getSourceItem().equals(slotChangeAction.getTargetItem())) {
                            return true;
                        } else {
                            Server.getInstance().getLogger().debug("Source equals target");
                            return false;
                        }
                    } else {
                        Server.getInstance().getLogger().debug("Invalid slot: " + slotChangeAction.getSlot());
                        return false;
                    }
                } else {
                    Server.getInstance().getLogger().debug("Invalid action type: " + slotChangeAction.getInventory().getType());
                    return false;
                }
            } else {
                Server.getInstance().getLogger().debug("SlotChangeAction expected, got " + action);
                return false;
            }
        }
        Server.getInstance().getLogger().debug("No actions on the list");
        return false;
    }
}
