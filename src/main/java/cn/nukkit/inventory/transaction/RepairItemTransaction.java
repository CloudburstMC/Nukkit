package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.block.AnvilDamageEvent;
import cn.nukkit.event.block.AnvilDamageEvent.DamageCause;
import cn.nukkit.event.inventory.RepairItemEvent;
import cn.nukkit.inventory.AnvilInventory;
import cn.nukkit.inventory.FakeBlockMenu;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.transaction.action.RepairItemAction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.types.NetworkInventoryAction;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RepairItemTransaction extends InventoryTransaction {

    private Item inputItem;
    private Item materialItem;
    private Item outputItem;

    private int cost;

    public RepairItemTransaction(Player source, List<InventoryAction> actions) {
        super(source, actions);
    }

    @Override
    public boolean canExecute() {
        Inventory inventory = getSource().getWindowById(Player.ANVIL_WINDOW_ID);
        if (inventory == null) {
            return false;
        }
        AnvilInventory anvilInventory = (AnvilInventory) inventory;
        return this.inputItem != null && this.outputItem != null && this.inputItem.equals(anvilInventory.getInputSlot(), true, true)
                && (!this.hasMaterial() || this.materialItem.equals(anvilInventory.getMaterialSlot(), true, true))
                && this.checkRecipeValid();
    }

    @Override
    public boolean execute() {
        if (this.hasExecuted() || !this.canExecute()) {
            this.source.removeAllWindows(false);
            this.sendInventories();
            return false;
        }
        AnvilInventory inventory = (AnvilInventory) getSource().getWindowById(Player.ANVIL_WINDOW_ID);

        if (inventory.getCost() != this.cost && !this.source.isCreative()) {
            this.source.getServer().getLogger().debug("Got unexpected cost " + inventory.getCost() + " from " + this.source.getName() + "(expected " + this.cost + ")");
        }

        RepairItemEvent event = new RepairItemEvent(inventory, this.inputItem, this.outputItem, this.materialItem, this.cost, this.source);
        this.source.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            this.source.removeAllWindows(false);
            this.sendInventories();
            return true;
        }

        for (InventoryAction action : this.actions) {
            if (action.execute(this.source)) {
                action.onExecuteSuccess(this.source);
            } else {
                action.onExecuteFail(this.source);
            }
        }

        FakeBlockMenu holder = inventory.getHolder();
        Block block = this.source.level.getBlock(holder.getFloorX(), holder.getFloorY(), holder.getFloorZ());
        if (block.getId() == Block.ANVIL) {
            int oldDamage = block.getDamage() >= 8 ? 2 : block.getDamage() >= 4 ? 1 : 0;
            int newDamage = !this.source.isCreative() && ThreadLocalRandom.current().nextInt(100) < 12 ? oldDamage + 1 : oldDamage;

            AnvilDamageEvent ev = new AnvilDamageEvent(block, oldDamage, newDamage, DamageCause.USE, this.source);
            ev.setCancelled(oldDamage == newDamage);
            this.source.getServer().getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                newDamage = ev.getNewDamage();
                if (newDamage > 2) {
                    this.source.level.setBlock(block, Block.get(Block.AIR), true);
                    this.source.level.addLevelEvent(block, LevelEventPacket.EVENT_SOUND_ANVIL_BREAK);
                } else {
                    if (newDamage < 0) {
                        newDamage = 0;
                    }
                    if (newDamage != oldDamage) {
                        block.setDamage((newDamage << 2) | (block.getDamage() & 0x3));
                        this.source.level.setBlock(block, block, true);
                    }
                    this.source.level.addLevelEvent(block, LevelEventPacket.EVENT_SOUND_ANVIL_USE);
                }
            } else {
                this.source.level.addLevelEvent(block, LevelEventPacket.EVENT_SOUND_ANVIL_USE);
            }
        }

        if (!this.source.isCreative()) {
            this.source.setExperience(this.source.getExperience(), this.source.getExperienceLevel() - event.getCost());
        }
        return true;
    }

    @Override
    public void addAction(InventoryAction action) {
        super.addAction(action);
        if (action instanceof RepairItemAction) {
            switch (((RepairItemAction) action).getType()) {
                case NetworkInventoryAction.SOURCE_TYPE_ANVIL_INPUT:
                    this.inputItem = action.getTargetItem();
                    break;
                case NetworkInventoryAction.SOURCE_TYPE_ANVIL_RESULT:
                    this.outputItem = action.getSourceItem();
                    break;
                case NetworkInventoryAction.SOURCE_TYPE_ANVIL_MATERIAL:
                    this.materialItem = action.getTargetItem();
                    break;
            }
        }
    }

    private boolean checkRecipeValid() {
        int cost = 0;
        int baseRepairCost = this.inputItem.getRepairCost();

        if (this.isMapRecipe()) {
            if (!this.matchMapRecipe()) {
                return false;
            }
            baseRepairCost = 0;
        } else if (this.hasMaterial()) {
            baseRepairCost += this.materialItem.getRepairCost();

            if (this.inputItem.getMaxDurability() != -1 && this.matchRepairItem()) {
                int maxRepairDamage = this.inputItem.getMaxDurability() / 4;
                int repairDamage = Math.min(this.inputItem.getDamage(), maxRepairDamage);
                if (repairDamage <= 0) {
                    return false;
                }

                int damage = this.inputItem.getDamage();
                for (; repairDamage > 0 && cost < this.materialItem.getCount(); cost++) {
                    damage = damage - repairDamage;
                    repairDamage = Math.min(damage, maxRepairDamage);
                }
                if (this.outputItem.getDamage() != damage) {
                    return false;
                }
            } else {
                boolean consumeEnchantedBook = this.materialItem.getId() == Item.ENCHANTED_BOOK && this.materialItem.hasEnchantments();
                if (!consumeEnchantedBook && (this.inputItem.getMaxDurability() == -1 || this.inputItem.getId() != this.materialItem.getId())) {
                    return false;
                }

                if (!consumeEnchantedBook && this.inputItem.getMaxDurability() != -1) {
                    int damage = this.inputItem.getDamage() - this.inputItem.getMaxDurability() + this.materialItem.getDamage() - this.inputItem.getMaxDurability() * 12 / 100 + 1;
                    if (damage < 0) {
                        damage = 0;
                    }

                    if (damage < this.inputItem.getDamage()) {
                        if (this.outputItem.getDamage() != damage) {
                            return false;
                        }
                        cost += 2;
                    }
                }

                Int2IntMap enchantments = new Int2IntOpenHashMap();
                enchantments.defaultReturnValue(-1);
                for (Enchantment enchantment : this.inputItem.getEnchantments()) {
                    enchantments.put(enchantment.getId(), enchantment.getLevel());
                }

                boolean hasCompatibleEnchantments = false;
                boolean hasIncompatibleEnchantments = false;
                for (Enchantment materialEnchantment : this.materialItem.getEnchantments()) {
                    Enchantment enchantment = this.inputItem.getEnchantment(materialEnchantment.getId());
                    int inputLevel = enchantment != null ? enchantment.getLevel() : 0;
                    int materialLevel = materialEnchantment.getLevel();
                    int outputLevel = inputLevel == materialLevel ? materialLevel + 1 : Math.max(materialLevel, inputLevel);

                    boolean canEnchant = materialEnchantment.canEnchant(this.inputItem) || this.inputItem.getId() == Item.ENCHANTED_BOOK;
                    for (Enchantment inputEnchantment : this.inputItem.getEnchantments()) {
                        if (inputEnchantment.getId() != materialEnchantment.getId() && !materialEnchantment.isCompatibleWith(inputEnchantment)) {
                            canEnchant = false;
                            cost++;
                        }
                    }

                    if (!canEnchant) {
                        hasIncompatibleEnchantments = true;
                    } else {
                        hasCompatibleEnchantments = true;
                        if (outputLevel > materialEnchantment.getMaxLevel()) {
                            outputLevel = materialEnchantment.getMaxLevel();
                        }

                        enchantments.put(materialEnchantment.getId(), outputLevel);
                        int rarityFactor;
                        switch (materialEnchantment.getRarity()) {
                            case COMMON:
                                rarityFactor = 1;
                                break;
                            case UNCOMMON:
                                rarityFactor = 2;
                                break;
                            case RARE:
                                rarityFactor = 4;
                                break;
                            case VERY_RARE:
                            default:
                                rarityFactor = 8;
                                break;
                        }

                        if (consumeEnchantedBook) {
                            rarityFactor = Math.max(1, rarityFactor / 2);
                        }

                        cost += rarityFactor * Math.max(0, outputLevel - inputLevel);
                        if (this.inputItem.getCount() > 1) {
                            cost = 40;
                        }
                    }
                }

                Enchantment[] outputEnchantments = this.outputItem.getEnchantments();
                if (hasIncompatibleEnchantments && !hasCompatibleEnchantments || enchantments.size() != outputEnchantments.length) {
                    return false;
                }

                for (Enchantment enchantment : outputEnchantments) {
                    if (enchantments.get(enchantment.getId()) != enchantment.getLevel()) {
                        return false;
                    }
                }
            }
        }

        int renameCost = 0;
        if (!this.inputItem.getCustomName().equals(this.outputItem.getCustomName())) {
            if (this.outputItem.getCustomName().length() > 30) {
                return false;
            }
            renameCost = 1;
            cost += renameCost;
        }

        this.cost = baseRepairCost + cost;
        if (renameCost == cost && renameCost > 0 && this.cost >= 40) {
            this.cost = 39;
        }
        if (baseRepairCost < 0 || cost < 0 || cost == 0 && !this.isMapRecipe() || this.cost > 39 && !this.source.isCreative()) {
            return false;
        }

        int nextBaseRepairCost = this.inputItem.getRepairCost();
        if (!this.isMapRecipe()) {
            if (this.hasMaterial() && nextBaseRepairCost < this.materialItem.getRepairCost()) {
                nextBaseRepairCost = this.materialItem.getRepairCost();
            }
            if (renameCost == 0 || renameCost != cost) {
                nextBaseRepairCost = 2 * nextBaseRepairCost + 1;
            }
        }
        if (this.outputItem.getRepairCost() != nextBaseRepairCost) {
            this.source.getServer().getLogger().debug("Got unexpected base cost " + this.outputItem.getRepairCost() + " from " + this.source.getName() + "(expected " + nextBaseRepairCost + ")");
            return false;
        }

        return true;
    }

    private boolean hasMaterial() {
        return this.materialItem != null && !this.materialItem.isNull();
    }

    private boolean isMapRecipe() {
        return this.hasMaterial() && (this.inputItem.getId() == Item.MAP || this.inputItem.getId() == Item.EMPTY_MAP)
                && (this.materialItem.getId() == Item.EMPTY_MAP || this.materialItem.getId() == Item.PAPER || this.materialItem.getId() == Item.COMPASS);
    }

    private boolean matchMapRecipe() {
        if (this.inputItem.getId() == Item.EMPTY_MAP) {
            return this.inputItem.getDamage() != 2 && this.materialItem.getId() == Item.COMPASS // locator
                    && this.outputItem.getId() == Item.EMPTY_MAP && this.outputItem.getDamage() == 2 && this.outputItem.getCount() == 1;
        } else if (this.inputItem.getId() == Item.MAP && this.outputItem.getDamage() == this.inputItem.getDamage()) {
            if (this.materialItem.getId() == Item.COMPASS) { // locator
                return this.inputItem.getDamage() != 2 && this.outputItem.getId() == Item.MAP && this.outputItem.getCount() == 1;
            } else if (this.materialItem.getId() == Item.EMPTY_MAP) { // clone
                return this.outputItem.getId() == Item.MAP && this.outputItem.getCount() == 2;
            } else if (this.materialItem.getId() == Item.PAPER && this.materialItem.getCount() >= 8) { // zoom out
                return this.inputItem.getDamage() < 3 && this.outputItem.getId() == Item.MAP && this.outputItem.getCount() == 1;
            }
        }
        return false;
    }

    private boolean matchRepairItem() {
        switch (this.inputItem.getId()) {
            case Item.LEATHER_CAP:
            case Item.LEATHER_TUNIC:
            case Item.LEATHER_PANTS:
            case Item.LEATHER_BOOTS:
                return this.materialItem.getId() == Item.LEATHER;
            case Item.WOODEN_SWORD:
            case Item.WOODEN_PICKAXE:
            case Item.WOODEN_SHOVEL:
            case Item.WOODEN_AXE:
            case Item.WOODEN_HOE:
                return this.materialItem.getId() == Item.PLANKS;
            case Item.IRON_SWORD:
            case Item.IRON_PICKAXE:
            case Item.IRON_SHOVEL:
            case Item.IRON_AXE:
            case Item.IRON_HOE:
            case Item.IRON_HELMET:
            case Item.IRON_CHESTPLATE:
            case Item.IRON_LEGGINGS:
            case Item.IRON_BOOTS:
            case Item.CHAIN_HELMET:
            case Item.CHAIN_CHESTPLATE:
            case Item.CHAIN_LEGGINGS:
            case Item.CHAIN_BOOTS:
                return this.materialItem.getId() == Item.IRON_INGOT;
            case Item.GOLD_SWORD:
            case Item.GOLD_PICKAXE:
            case Item.GOLD_SHOVEL:
            case Item.GOLD_AXE:
            case Item.GOLD_HOE:
            case Item.GOLD_HELMET:
            case Item.GOLD_CHESTPLATE:
            case Item.GOLD_LEGGINGS:
            case Item.GOLD_BOOTS:
                return this.materialItem.getId() == Item.GOLD_INGOT;
            case Item.DIAMOND_SWORD:
            case Item.DIAMOND_PICKAXE:
            case Item.DIAMOND_SHOVEL:
            case Item.DIAMOND_AXE:
            case Item.DIAMOND_HOE:
            case Item.DIAMOND_HELMET:
            case Item.DIAMOND_CHESTPLATE:
            case Item.DIAMOND_LEGGINGS:
            case Item.DIAMOND_BOOTS:
                return this.materialItem.getId() == Item.DIAMOND;
            case Item.NETHERITE_SWORD:
            case Item.NETHERITE_PICKAXE:
            case Item.NETHERITE_SHOVEL:
            case Item.NETHERITE_AXE:
            case Item.NETHERITE_HOE:
            case Item.NETHERITE_HELMET:
            case Item.NETHERITE_CHESTPLATE:
            case Item.NETHERITE_LEGGINGS:
            case Item.NETHERITE_BOOTS:
                return this.materialItem.getId() == Item.NETHERITE_INGOT;
            case Item.TURTLE_SHELL:
                return this.materialItem.getId() == Item.SCUTE;
            case Item.ELYTRA:
                return this.materialItem.getId() == Item.PHANTOM_MEMBRANE;
        }
        return false;
    }

    public Item getInputItem() {
        return this.inputItem;
    }

    public Item getMaterialItem() {
        return this.materialItem;
    }

    public Item getOutputItem() {
        return this.outputItem;
    }

    public int getCost() {
        return this.cost;
    }

    public static boolean checkForRepairItemPart(List<InventoryAction> actions) {
        for (InventoryAction action : actions) {
            if (action instanceof RepairItemAction) {
                return true;
            }
        }
        return false;
    }
}
