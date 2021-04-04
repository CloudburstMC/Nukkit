package cn.nukkit.entity.passive;

import java.util.ArrayList;
import java.util.List;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityAgeable;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.TradeInventory;
import cn.nukkit.inventory.TradeInventoryRecipe;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

public class EntityVillager extends EntityCreature implements InventoryHolder, EntityNPC, EntityAgeable {

    public static final int NETWORK_ID = 115;
    private TradeInventory inventory;
    private List<TradeInventoryRecipe> recipes = new ArrayList<TradeInventoryRecipe>();

    public EntityVillager(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.3f;
        }
        return 0.6f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.9f;
        }
        return 1.8f;
    }

    @Override
    public String getName() {
        return "Villager";
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(20);
        this.inventory = new TradeInventory(this);
        if(this.namedTag.getCompound("Offers") != null) {
            ListTag<CompoundTag> nbtRecipes = this.namedTag.getCompound("Offers").getList("Recipes", CompoundTag.class);
            for(CompoundTag nbt : nbtRecipes.getAll()) {
                recipes.add(TradeInventoryRecipe.toNBT(nbt));
            }
        } else {
            CompoundTag nbt = new CompoundTag("Offers");
            nbt.putList(new ListTag<CompoundTag>("Recipes"));
            nbt.putList(getDefaultTierExpRequirements());
            this.namedTag.putCompound("Offers", nbt);
        }
    }
    
    public void setTradeTier(int tier) {
        this.namedTag.putInt("TradeTier", tier);
    }
    
    public int getTradeTier() {
        return this.namedTag.getInt("TradeTier");
    }
    
    public void setWilling(boolean value) {
        this.namedTag.putBoolean("Willing", value);
    }
    
    public boolean isWilling() {
        return this.namedTag.getBoolean("Willing");
    }
    
    @Override
    public boolean onInteract(Player player, Item item) {
        if(this.namedTag.getCompound("Offers") != null) {
            player.addWindow(this.getInventory());
            return true;
        }
        return false;
    }
    
    public void addTradeRcipe(TradeInventoryRecipe recipe) {
        this.recipes.add(recipe);
        ListTag<CompoundTag> nbtRecipes = this.getOffers().getList("Recipes", CompoundTag.class);
        nbtRecipes.add(recipe.toNBT());
    }
    
    public List<TradeInventoryRecipe> getRecipes(){
        return this.recipes;
    }
    
    public CompoundTag getOffers() {
        return this.namedTag.getCompound("Offers");
	}
	
	private ListTag<CompoundTag> getDefaultTierExpRequirements() {
	    ListTag<CompoundTag> tag = new ListTag<CompoundTag>("TierExpRequirements");
	    tag.add(new CompoundTag().putInt("0", 0));
	    tag.add(new CompoundTag().putInt("1", 10));
	    tag.add(new CompoundTag().putInt("2", 70));
	    tag.add(new CompoundTag().putInt("3", 150));
	    tag.add(new CompoundTag().putInt("4", 250));
	    return tag;
    }

    public boolean isBaby() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_BABY);
    }

    public void setBaby(boolean baby) {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_BABY, baby);
        this.setScale(baby ? 0.5f : 1);
    }

	@Override
	public Inventory getInventory() {
		return this.inventory;
	}
}
