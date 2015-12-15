package cn.nukkit.inventory;


import cn.nukkit.item.Item;
import cn.nukkit.tile.BrewingStand;

public class BrewingInventory extends ContainerInventory{
    public BrewingInventory(BrewingStand tile){
        super(tile, InventoryType.get(InventoryType.BREWING_STAND));
    }

    @Override
    public BrewingStand getHolder(){
        return (BrewingStand) this.holder;
    }

    public Item getIngredient(){
        return getItem(0);
    }

    public void setIngredient(Item item){
        setItem(0, item);
    }

    @Override
    public void onSlotChange(int index, Item before){
        super.onSlotChange(index, before);

        this.getHolder().scheduleUpdate();
    }
}