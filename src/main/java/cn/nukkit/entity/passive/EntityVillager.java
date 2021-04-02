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
    private int tradeTier = 0;

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
    }
    
    public void setTradeTier(int tier) {
        this.tradeTier = tier;
    }
    
    public int getTradeTier() {
        return this.tradeTier;
    }
    
    @Override
    public boolean onInteract(Player player, Item item) {
        if(recipes.size() > 0) {
            player.addWindow(this.getInventory());
            return true;
        }
        return false;
    }
    
    public void addTradeRcipe(TradeInventoryRecipe recipe) {
        this.recipes.add(recipe);
    }
    
	public CompoundTag getOffers() {
	    CompoundTag nbt = new CompoundTag();
	    nbt.putList(recipesToNbt());
	    nbt.putList(getTierExpRequirements());
	    return nbt;
	}
	
	private ListTag<CompoundTag> recipesToNbt() {
	    ListTag<CompoundTag> tag = new ListTag<CompoundTag>("Recipes");
	    for(TradeInventoryRecipe recipe : this.recipes) {
	        tag.add(recipe.toNBT());
	    }
	    return tag;
	}
	
	private ListTag<CompoundTag> getTierExpRequirements() {
	    ListTag<CompoundTag> tag = new ListTag<CompoundTag>("TierExpRequirements");
	    tag.add(new CompoundTag().putInt("0", 0));
	    tag.add(new CompoundTag().putInt("1", 10));
	    tag.add(new CompoundTag().putInt("2", 60));
	    tag.add(new CompoundTag().putInt("3", 160));
	    tag.add(new CompoundTag().putInt("4", 310));
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
