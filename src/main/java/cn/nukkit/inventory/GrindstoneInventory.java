package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.tag.CompoundTag;

public class GrindstoneInventory extends FakeBlockUIComponent {
    public static final int OFFSET = 16;
    private static final int SLOT_FIRST_ITEM = 0;
    private static final int SLOT_SECOND_ITEM = 1;
    private static final int SLOT_RESULT = 50 - OFFSET;

    public GrindstoneInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.GRINDSTONE, OFFSET, position);
    }

    @Override
    public void close(Player who) {
        onClose(who);
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

        clear(SLOT_FIRST_ITEM);
        clear(SLOT_SECOND_ITEM);

        who.resetCraftingGridType();
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.CRAFTING_GRINDSTONE;
    }
    
    public Item getFirstItem() {
        return getItem(SLOT_FIRST_ITEM);
    }
    
    public Item getSecondItem() {
        return getItem(SLOT_SECOND_ITEM);
    }
    
    public Item getResult() {
        return getItem(2);
    }
    
    public boolean setFirstItem(Item item, boolean send) {
        return setItem(SLOT_FIRST_ITEM, item, send);
    }
    
    public boolean setFirstItem(Item item) {
        return setFirstItem(item, true);
    }
    
    public boolean setSecondItem(Item item, boolean send) {
        return setItem(SLOT_SECOND_ITEM, item, send);
    }
    
    public boolean setSecondItem(Item item) {
        return setSecondItem(item, true);
    }
    
    public boolean setResult(Item item, boolean send) {
        return setItem(2, item, send);
    }
    
    public boolean setResult(Item item) {
        return setResult(item, true);
    }
    
    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        try {
            if (index > 1) {
                return;
            }
            Item firstItem = getFirstItem();
            Item secondItem = getSecondItem();
            if (!firstItem.isNull() && !secondItem.isNull() && firstItem.getId() != secondItem.getId()) {
                setResult(Item.get(0));
                return;
            }
    
            if (firstItem.isNull()) {
                Item air = firstItem;
                firstItem = secondItem;
                secondItem = air;
            }
    
            if (firstItem.isNull()) {
                setResult(Item.get(0));
                return;
            }
            
            Item result = firstItem.clone();
            CompoundTag tag = result.getNamedTag();
            if (tag == null) tag = new CompoundTag(); 
            tag.remove("ench");
            tag.putInt("RepairCost", 0);
            result.setCompoundTag(tag);
            if (!secondItem.isNull()) {
                int first = firstItem.getMaxDurability() - firstItem.getDamage();
                int second = secondItem.getMaxDurability() - secondItem.getDamage();
                int reduction = first + second + firstItem.getMaxDurability() * 5 / 100;
                int resultingDamage = Math.max(firstItem.getMaxDurability() - reduction + 1, 0);
                result.setDamage(resultingDamage);
            }
            setResult(result);
        } finally {
            super.onSlotChange(index, before, send);
        }
    }
    
    @Override
    public Item getItem(int index) {
        if (index < 0 || index > 3) {
            return Item.get(0);
        }
        if (index == 2) {
            index = SLOT_RESULT;
        }
        
        return super.getItem(index);
    }
    
    @Override
    public boolean setItem(int index, Item item, boolean send) {
        if (index < 0 || index > 3) {
            return false;
        }
        
        if (index == 2) {
            index = SLOT_RESULT;
        }
        
        return super.setItem(index, item, send);
    }
}
