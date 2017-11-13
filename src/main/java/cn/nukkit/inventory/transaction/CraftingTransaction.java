package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.inventory.BigCraftingGrid;
import cn.nukkit.inventory.CraftingRecipe;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.item.Item;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.types.ContainerIds;

import java.util.Arrays;
import java.util.List;

/**
 * @author CreeperFace
 */
public class CraftingTransaction extends InventoryTransaction {

    protected int gridSize;

    protected Item[][] inputs;

    protected Item[][] secondaryOutputs;

    protected Item primaryOutput;

    protected CraftingRecipe recipe;

    public CraftingTransaction(Player source, List<InventoryAction> actions) {
        super(source, actions, false);

        this.gridSize = (source.getCraftingGrid() instanceof BigCraftingGrid) ? 3 : 2;

        Item air = Item.get(Item.AIR, 0, 1);
        this.inputs = new Item[gridSize][gridSize];
        for (Item[] a : this.inputs) {
            Arrays.fill(a, air);
        }

        this.secondaryOutputs = new Item[gridSize][gridSize];
        for (Item[] a : this.secondaryOutputs) {
            Arrays.fill(a, air);
        }

        init(source, actions);
    }

    public void setInput(int index, Item item) {
        int y = NukkitMath.floorDouble((double) index / this.gridSize);
        int x = index % this.gridSize;

        if (this.inputs[y][x].isNull()) {
            inputs[y][x] = item.clone();
        } else if (!inputs[y][x].equals(item)) {
            throw new RuntimeException("Input " + index + " has already been set and does not match the current item (expected " + inputs[y][x] + ", got " + item + ")");
        }
    }

    public Item[][] getInputMap() {
        return inputs;
    }

    public void setExtraOutput(int index, Item item) {
        int y = (index / this.gridSize);
        int x = index % gridSize;

        if (secondaryOutputs[y][x].isNull()) {
            secondaryOutputs[y][x] = item.clone();
        } else if (!secondaryOutputs[y][x].equals(item)) {
            throw new RuntimeException("Output " + index + " has already been set and does not match the current item (expected " + secondaryOutputs[y][x] + ", got " + item + ")");
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

    private Item[][] reindexInputs() {
        int xOffset = gridSize;
        int yOffset = gridSize;

        int height = 0;
        int width = 0;

        for (int y = 0; y < this.inputs.length; y++) {
            Item[] row = this.inputs[y];

            for (int x = 0; x < row.length; x++) {
                Item item = row[x];

                if (!item.isNull()) {
                    xOffset = Math.min(x, xOffset);
                    yOffset = Math.min(y, yOffset);

                    height = Math.max(y + 1 - yOffset, height);
                    width = Math.max(x + 1 - xOffset, width);
                }
            }
        }

        if (height == 0 || width == 0) {
            return new Item[0][];
        }

        Item air = Item.get(Item.AIR, 0, 0);
        Item[][] reindexed = new Item[height][width];
        for (Item[] i : reindexed) {
            Arrays.fill(i, air);
        }

        for (int y = 0; y < reindexed.length; y++) {
            Item[] row = reindexed[y];

            for (int x = 0; x < row.length; x++) {
                reindexed[y][x] = this.inputs[y + yOffset][x + xOffset];
            }
        }

        return reindexed;
    }

    public boolean canExecute() {
        Item[][] inputs = reindexInputs();

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
        ContainerClosePacket pk = new ContainerClosePacket();
        pk.windowId = ContainerIds.NONE;
        this.source.dataPacket(pk);
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
                    source.awardAchievement("buildBetterPickaxe");
                    break;
                case Item.WOODEN_SWORD:
                    source.awardAchievement("buildSword");
                    break;
                case Item.DIAMOND:
                    source.awardAchievement("diamond");
                    break;
            }

            return true;
        }

        return false;
    }
}
