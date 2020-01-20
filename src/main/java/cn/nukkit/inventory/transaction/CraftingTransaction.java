package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.inventory.*;
import cn.nukkit.inventory.transaction.action.DamageAnvilAction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.inventory.transaction.action.TakeLevelAction;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.types.ContainerIds;
import cn.nukkit.scheduler.Task;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author CreeperFace
 */
public class CraftingTransaction extends InventoryTransaction {

    protected int gridSize;

    protected Item[][] inputs;

    protected Item[][] secondaryOutputs;

    protected Item primaryOutput;

    protected Recipe recipe;

    protected int craftingType;

    public CraftingTransaction(Player source, List<InventoryAction> actions) {
        super(source, actions, false);

        Item air = Item.get(Item.AIR, 0, 1);
        this.craftingType = source.craftingType;
        if (source.craftingType == Player.CRAFTING_STONECUTTER) {
            this.gridSize = 1;
            this.inputs = new Item[1][1];
            this.inputs[0][0] = air;
            this.secondaryOutputs = new Item[1][1];
            this.secondaryOutputs[0][0] = air;
        } else {
            this.gridSize = (source.getCraftingGrid() instanceof BigCraftingGrid) ? 3 : 2;
            this.inputs = new Item[gridSize][gridSize];
            for (Item[] a : this.inputs) {
                Arrays.fill(a, air);
            }

            this.secondaryOutputs = new Item[gridSize][gridSize];
            for (Item[] a : this.secondaryOutputs) {
                Arrays.fill(a, air);
            }
        }

        init(source, actions);
    }

    public void setInput(int index, Item item) {
        int y = index / this.gridSize;
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

    public Recipe getRecipe() {
        return recipe;
    }

    private Item[][] reindexInputs() {
        int xMin = gridSize - 1;
        int yMin = gridSize - 1;

        int xMax = 0;
        int yMax = 0;

        for (int y = 0; y < this.inputs.length; y++) {
            Item[] row = this.inputs[y];

            for (int x = 0; x < row.length; x++) {
                Item item = row[x];

                if (!item.isNull()) {
                    xMin = Math.min(x, xMin);
                    yMin = Math.min(y, yMin);

                    xMax = Math.max(x, xMax);
                    yMax = Math.max(y, yMax);
                }
            }
        }

        final int height = yMax - yMin + 1;
        final int width = xMax - xMin + 1;

        if (height < 1 || width < 1) {
            return new Item[0][];
        }

        Item[][] reindexed = new Item[height][width];

        for (int y = yMin, i = 0; y <= yMax; y++, i++) {
            System.arraycopy(inputs[y], xMin, reindexed[i], 0, width);
        }

        return reindexed;
    }

    public boolean canExecute() {
        Item[][] inputs = reindexInputs();

        CraftingManager craftingManager = source.getServer().getCraftingManager();
        if (craftingType == Player.CRAFTING_STONECUTTER) {
            recipe = craftingManager.matchStonecutterRecipe(this.primaryOutput);
        } else if (craftingType == Player.CRAFTING_CARTOGRAPHY) {
            recipe = craftingManager.matchCartographyRecipe(inputs, this.primaryOutput, this.secondaryOutputs);
        } else if (craftingType == Player.CRAFTING_ANVIL) {
            Inventory inventory = source.getWindowById(Player.ANVIL_WINDOW_ID);
            if (inventory instanceof AnvilInventory) {
                AnvilInventory anvil = (AnvilInventory) inventory;
                addInventory(anvil);
                if (this.primaryOutput.equals(anvil.getResult(), true, true)) {
                    TakeLevelAction takeLevel = new TakeLevelAction(anvil.getLevelCost());
                    addAction(takeLevel);
                    if (takeLevel.isValid(source)) {
                        recipe = new RepairRecipe(InventoryType.ANVIL, this.primaryOutput, Arrays.asList(inputs[0]));
                        PlayerUIInventory uiInventory = source.getUIInventory();
                        actions.add(new DamageAnvilAction(anvil, !source.isCreative() && ThreadLocalRandom.current().nextFloat() < 0.12F, this));
                        actions.stream()
                                .filter(a -> a instanceof SlotChangeAction)
                                .map(a-> (SlotChangeAction) a)
                                .filter(a -> a.getInventory() == uiInventory)
                                .filter(a -> a.getSlot() == 50)
                                .findFirst()
                                .ifPresent(a -> {
                                    // Move the set result action to the end, otherwise the result would be cleared too early
                                    actions.remove(a);
                                    actions.add(a);
                                });
                        anvil.setResult(Item.get(0), false);
                    }
                }
            }
        } else if (craftingType == Player.CRAFTING_GRINDSTONE) {
            Inventory inventory = source.getWindowById(Player.GRINDSTONE_WINDOW_ID);
            if (inventory instanceof GrindstoneInventory) {
                GrindstoneInventory grindstone = (GrindstoneInventory) inventory;
                addInventory(grindstone);
                if (this.primaryOutput.equals(grindstone.getResult(), true, true)) {
                    recipe = new RepairRecipe(InventoryType.GRINDSTONE, this.primaryOutput, Arrays.asList(inputs[0]));
                    grindstone.setResult(Item.get(0), false);
                }
            }
        } else {
            recipe = craftingManager.matchRecipe(inputs, this.primaryOutput, this.secondaryOutputs);
        }

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
        source.getServer().getScheduler().scheduleDelayedTask(new Task() {
            @Override
            public void onRun(int currentTick) {
                source.dataPacket(pk);
            }
        }, 20);

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
