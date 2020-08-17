package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.tag.CompoundTag;
import io.netty.util.internal.StringUtil;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class AnvilInventory extends FakeBlockUIComponent {
    
    public static final int OFFSET = 1;
    public static final int TARGET = 0;
    public static final int SACRIFICE = 1;
    public static final int RESULT = 50;
    
    private int levelCost;
    private String newItemName;
    
    @NonNull
    private Item currentResult = Item.get(0);

    public AnvilInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.ANVIL, OFFSET, position);
    }
    
    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        try {
            if (index <= 1) {
                updateResult();
            }
        } finally {
            super.onSlotChange(index, before, send);
        }
    }
    
    public void updateResult() {
        Item target = getFirstItem();
        Item sacrifice = getSecondItem();
        
        if (target.isNull() && sacrifice.isNull()) {
            setResult(Item.get(0));
            setLevelCost(0);
            return;
        }
        
        setLevelCost(1);
        int extraCost = 0;
        int costHelper = 0;
        int repairMaterial = getRepairMaterial(target);
        Item result = target.clone();
        int levelCost = getRepairCost(result) + (sacrifice.isNull() ? 0 : getRepairCost(sacrifice));
        Map<Integer, Enchantment> enchantmentMap = new LinkedHashMap<>();
        for (Enchantment enchantment : target.getEnchantments()) {
            enchantmentMap.put(enchantment.getId(), enchantment);
        }
        if (!sacrifice.isNull()) {
            boolean enchantedBook = sacrifice.getId() == Item.ENCHANTED_BOOK && sacrifice.getEnchantments().length > 0;
            int repair;
            int repair2;
            int repair3;
            if (result.getMaxDurability() != -1 && sacrifice.getId() == repairMaterial) {
                repair = Math.min(result.getDamage(), result.getMaxDurability() / 4);
                if (repair <= 0) {
                    setResult(Item.get(0));
                    setLevelCost(0);
                    return;
                }
        
                for(repair2 = 0; repair > 0 && repair2 < sacrifice.getCount(); ++repair2) {
                    repair3 = result.getDamage() - repair;
                    result.setDamage(repair3);
                    ++extraCost;
                    repair = Math.min(result.getDamage(), result.getMaxDurability() / 4);
                }
    
            } else {
                if (!enchantedBook && (result.getId() != sacrifice.getId() || result.getMaxDurability() == -1)) {
                    setResult(Item.get(0));
                    setLevelCost(0);
                    return;
                }
        
                if ((result.getMaxDurability() != -1) && !enchantedBook) {
                    repair = target.getMaxDurability() - target.getDamage();
                    repair2 = sacrifice.getMaxDurability() - sacrifice.getDamage();
                    repair3 = repair2 + result.getMaxDurability() * 12 / 100;
                    int totalRepair = repair + repair3;
                    int finalDamage = result.getMaxDurability() - totalRepair + 1;
                    if (finalDamage < 0) {
                        finalDamage = 0;
                    }
            
                    if (finalDamage < result.getDamage()) {
                        result.setDamage(finalDamage);
                        extraCost += 2;
                    }
                }
        
                Enchantment[] sacrificeEnchantments = sacrifice.getEnchantments();
                boolean compatibleFlag = false;
                boolean incompatibleFlag = false;
                Iterator<Enchantment> sacrificeEnchIter = Arrays.stream(sacrificeEnchantments).iterator();
        
                iter:
                while(true) {
                    Enchantment sacrificeEnchantment;
                    do {
                        if (!sacrificeEnchIter.hasNext()) {
                            if (incompatibleFlag && !compatibleFlag) {
                                setResult(Item.get(0));
                                setLevelCost(0);
                                return;
                            }
                            break iter;
                        }
                
                        sacrificeEnchantment = sacrificeEnchIter.next();
                    } while(sacrificeEnchantment == null);

                    Enchantment resultEnchantment = result.getEnchantment(sacrificeEnchantment.id);
                    int targetLevel = resultEnchantment != null? resultEnchantment.getLevel() : 0;
                    int resultLevel = sacrificeEnchantment.getLevel();
                    resultLevel = targetLevel == resultLevel ? resultLevel + 1 : Math.max(resultLevel, targetLevel);
                    boolean compatible = sacrificeEnchantment.isItemAcceptable(target);
                    if (playerUI.getHolder().isCreative() || target.getId() == Item.ENCHANTED_BOOK) {
                        compatible = true;
                    }
            
                    Iterator<Enchantment> targetEnchIter = Stream.of(target.getEnchantments()).iterator();
            
                    while(targetEnchIter.hasNext()) {
                        Enchantment targetEnchantment = targetEnchIter.next();
                        if (targetEnchantment.id != sacrificeEnchantment.id && (!sacrificeEnchantment.isCompatibleWith(targetEnchantment) || !targetEnchantment.isCompatibleWith(sacrificeEnchantment))) {
                            compatible = false;
                            ++extraCost;
                        }
                    }
            
                    if (!compatible) {
                        incompatibleFlag = true;
                    } else {
                        compatibleFlag = true;
                        if (resultLevel > sacrificeEnchantment.getMaxLevel()) {
                            resultLevel = sacrificeEnchantment.getMaxLevel();
                        }
                        
                        enchantmentMap.put(sacrificeEnchantment.getId(), Enchantment.get(sacrificeEnchantment.getId()).setLevel(resultLevel));
                        int rarity = 0;
                        int weight = sacrificeEnchantment.getWeight();
                        if (weight >= 10) {
                            rarity = 1;
                        } else if (weight >= 5) {
                            rarity = 2;
                        } else if (weight >= 2) {
                            rarity = 4;
                        } else {
                            rarity = 8;
                        }
                
                        if (enchantedBook) {
                            rarity = Math.max(1, rarity / 2);
                        }
                
                        extraCost += rarity * Math.max(0, resultLevel - targetLevel);
                        if (target.getCount() > 1) {
                            extraCost = 40;
                        }
                    }
                }
            }
        }
        
        if (StringUtil.isNullOrEmpty(this.newItemName)) {
            if (target.hasCustomName()) {
                costHelper = 1;
                extraCost += costHelper;
                result.clearCustomName();
            }
        } else {
            costHelper = 1;
            extraCost += costHelper;
            result.setCustomName(this.newItemName);
        }
        
        setLevelCost(levelCost + extraCost);
        if (extraCost <= 0) {
            result = Item.get(0);
        }
        
        if (costHelper == extraCost && costHelper > 0 && getLevelCost() >= 40) {
            setLevelCost(39);
        }
        
        if (getLevelCost() >= 40 && !this.playerUI.getHolder().isCreative()) {
            result = Item.get(0);
        }
        
        if (!result.isNull()) {
            int repairCost = getRepairCost(result);
            if (!sacrifice.isNull() && repairCost < getRepairCost(sacrifice)) {
                repairCost = getRepairCost(sacrifice);
            }
    
            if (costHelper != extraCost || costHelper == 0) {
                repairCost = repairCost * 2 + 1;
            }

            CompoundTag namedTag = result.getNamedTag();
            if (namedTag == null) {
                namedTag = new CompoundTag();
            }
            namedTag.putInt("RepairCost", repairCost);
            namedTag.remove("ench");
            result.setNamedTag(namedTag);
            if (!enchantmentMap.isEmpty()) {
                result.addEnchantment(enchantmentMap.values().toArray(new Enchantment[0]));
            }
        }
        setResult(result);
    }
    
    @Override
    public void onClose(Player who) {
        super.onClose(who);
        who.craftingType = Player.CRAFTING_SMALL;

        Item[] drops = new Item[]{ getFirstItem(), getSecondItem() };
        drops = who.getInventory().addItem(drops);
        for (Item drop : drops) {
            if (!who.dropItem(drop)) {
                this.getHolder().getLevel().dropItem(this.getHolder().add(0.5, 0.5, 0.5), drop);
            }
        }
        
        clear(TARGET);
        clear(SACRIFICE);
        
        who.resetCraftingGridType();
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.CRAFTING_ANVIL;
    }
    
    @Override
    public Item getItem(int index) {
        if (index < 0 || index > 3) {
            return Item.get(0);
        }
        if (index == 2) {
            return getResult();
        }
        
        return super.getItem(index);
    }
    
    @Override
    public boolean setItem(int index, Item item, boolean send) {
        if (index < 0 || index > 3) {
            return false;
        }
        
        if (index == 2) {
            return setResult(item);
        }
        
        return super.setItem(index, item, send);
    }
    
    public Item getFirstItem() {
        return getItem(TARGET);
    }
    
    public Item getSecondItem() {
        return getItem(SACRIFICE);
    }
    
    public Item getResult() {
        return currentResult.clone();
    }

    @Override
    public void sendContents(Player... players) {
        super.sendContents(players);
        // Fixes desync when transactions are cancelled.
        for (Player player : players) {
            player.sendExperienceLevel();
        }
    }

    public boolean setFirstItem(Item item, boolean send) {
        return setItem(SACRIFICE, item, send);
    }
    
    public boolean setFirstItem(Item item) {
        return setFirstItem(item, true);
    }
    
    public boolean setSecondItem(Item item, boolean send) {
        return setItem(SACRIFICE, item, send);
    }
    
    public boolean setSecondItem(Item item) {
        return setSecondItem(item, true);
    }

    /**
     * @deprecated send parameter is deprecated. This method will be removed in 1.3.0.0-PN.
     */
    @Deprecated
    public boolean setResult(Item item, boolean send) {
        return setItem(2, item, send);
    }

    /**
     * @deprecated the client won't see this change, and the transaction might fail. This method will be removed from public in 1.3.0.0-PN.
     */
    @Deprecated
    public boolean setResult(Item item) {
        if (item == null || item.isNull()) {
            this.currentResult = Item.get(0);
        } else {
            this.currentResult = item.clone();
        }
        return true;
    }
    
    private static int getRepairCost(Item item) {
        return item.hasCompoundTag() && item.getNamedTag().contains("RepairCost") ? item.getNamedTag().getInt("RepairCost") : 0;
    }
    
    public int getLevelCost() {
        return levelCost;
    }
    
    protected void setLevelCost(int levelCost) {
        this.levelCost = levelCost;
    }
    
    public String getNewItemName() {
        return newItemName;
    }
    
    public void setNewItemName(String newItemName) {
        this.newItemName = newItemName;
    }
    
    private static int getRepairMaterial(Item target) {
        switch (target.getId()) {
            case Item.WOODEN_SWORD:
            case Item.WOODEN_PICKAXE:
            case Item.WOODEN_SHOVEL:
            case Item.WOODEN_AXE:
            case Item.WOODEN_HOE:
                return Block.PLANKS;
        
            case Item.IRON_SWORD:
            case Item.IRON_PICKAXE:
            case Item.IRON_SHOVEL:
            case Item.IRON_AXE:
            case Item.IRON_HOE:
            case Item.IRON_HELMET:
            case Item.IRON_CHESTPLATE:
            case Item.IRON_LEGGINGS:
            case Item.IRON_BOOTS:
                return Item.IRON_INGOT;
        
            case Item.GOLD_SWORD:
            case Item.GOLD_PICKAXE:
            case Item.GOLD_SHOVEL:
            case Item.GOLD_AXE:
            case Item.GOLD_HOE:
            case Item.GOLD_HELMET:
            case Item.GOLD_CHESTPLATE:
            case Item.GOLD_LEGGINGS:
            case Item.GOLD_BOOTS:
                return Item.GOLD_INGOT;
        
            case Item.DIAMOND_SWORD:
            case Item.DIAMOND_PICKAXE:
            case Item.DIAMOND_SHOVEL:
            case Item.DIAMOND_AXE:
            case Item.DIAMOND_HOE:
            case Item.DIAMOND_HELMET:
            case Item.DIAMOND_CHESTPLATE:
            case Item.DIAMOND_LEGGINGS:
            case Item.DIAMOND_BOOTS:
                return Item.DIAMOND;
        
            case Item.LEATHER_CAP:
            case Item.LEATHER_TUNIC:
            case Item.LEATHER_PANTS:
            case Item.LEATHER_BOOTS:
                return Item.LEATHER;
                
            case Item.STONE_SWORD:
            case Item.STONE_PICKAXE:
            case Item.STONE_SHOVEL:
            case Item.STONE_AXE:
            case Item.STONE_HOE:
                return Item.COBBLESTONE;

            case Item.NETHERITE_SWORD:
            case Item.NETHERITE_PICKAXE:
            case Item.NETHERITE_SHOVEL:
            case Item.NETHERITE_AXE:
            case Item.NETHERITE_HOE:
            case Item.NETHERITE_HELMET:
            case Item.NETHERITE_CHESTPLATE:
            case Item.NETHERITE_LEGGINGS:
            case Item.NETHERITE_BOOTS:
                return Item.NETHERITE_INGOT;

            default:
                return 0;
        }
    }
}
