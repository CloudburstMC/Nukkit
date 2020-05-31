package cn.nukkit.inventory.transaction;

import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.inventory.*;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.player.Player;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.Identifier;
import com.nukkitx.protocol.bedrock.data.ContainerId;
import com.nukkitx.protocol.bedrock.packet.ContainerClosePacket;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static cn.nukkit.block.BlockIds.*;
import static cn.nukkit.item.ItemIds.*;

/**
 * @author CreeperFace
 */
@Getter
public class CraftingTransaction extends InventoryTransaction {

    protected int gridSize;

    protected List<Item> inputs;

    protected List<Item> secondaryOutputs;

    protected Item primaryOutput;

    protected CraftingRecipe recipe;

    public CraftingTransaction(Player source, List<InventoryAction> actions) {
        super(source, actions, false);

        this.gridSize = (source.getCraftingGrid() instanceof BigCraftingGrid) ? 3 : 2;
        this.inputs = new LinkedList<>();

        this.secondaryOutputs = new LinkedList<>();

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

    public void setExtraOutput(Item item) {
        if (inputs.size() < gridSize * gridSize) {
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
            throw new RuntimeException("Primary result item has already been set and does not match the current item (expected " + primaryOutput + ", got " + item + ")");
        }
    }

    public CraftingRecipe getRecipe() {
        return recipe;
    }


    public boolean canExecute() {
        recipe = source.getServer().getCraftingManager().matchRecipe(inputs, this.primaryOutput, this.secondaryOutputs);

        return this.recipe != null && super.canExecute();
    }

    protected boolean callExecuteEvent() {
        CraftItemEvent ev;

        this.source.getServer().getPluginManager().callEvent(ev = new CraftItemEvent(this));
        return !ev.isCancelled();
    }

    protected void sendInventories() {
        super.sendInventories();

        /*
         * TODO: HACK!
         * we can't resend the contents of the crafting window, so we force the client to close it instead.
         * So people don't whine about messy desync issues when someone cancels CraftItemEvent, or when a crafting
         * transaction goes wrong.
         */
        ContainerClosePacket packet = new ContainerClosePacket();
        packet.setWindowId((byte) ContainerId.NONE);
        source.getServer().getScheduler().scheduleDelayedTask(new Task() {
            @Override
            public void onRun(int currentTick) {
                source.sendPacket(packet);
            }
        }, 20);

        this.source.resetCraftingGridType();
    }

    public boolean execute() {
        if (super.execute()) {
            Identifier id = this.primaryOutput.getId();
            if (id == CRAFTING_TABLE) {
                source.awardAchievement("buildWorkBench");
            } else if (id == WOODEN_PICKAXE) {
                source.awardAchievement("buildPickaxe");
            } else if (id == FURNACE) {
                source.awardAchievement("buildFurnace");
            } else if (id == WOODEN_HOE) {
                source.awardAchievement("buildHoe");
            } else if (id == BREAD) {
                source.awardAchievement("makeBread");
            } else if (id == ItemIds.CAKE) {
                source.awardAchievement("bakeCake");
            } else if (id == STONE_PICKAXE || id == GOLDEN_PICKAXE || id == IRON_PICKAXE || id == DIAMOND_PICKAXE) {
                source.awardAchievement("buildBetterPickaxe");
            } else if (id == WOODEN_SWORD) {
                source.awardAchievement("buildSword");
            } else if (id == DIAMOND) {
                source.awardAchievement("diamond");
            }

            return true;
        }

        return false;
    }

    public boolean checkForCraftingPart(List<InventoryAction> actions) {
        for (InventoryAction action : actions) {
            if (action instanceof SlotChangeAction) {
                SlotChangeAction slotChangeAction = (SlotChangeAction) action;
                if (slotChangeAction.getInventory().getType() == InventoryType.UI && slotChangeAction.getSlot() == 50 &&
                        !slotChangeAction.getSourceItem().equals(slotChangeAction.getTargetItem())) {
                    return true;
                }
            }
        }
        return false;
    }
}
