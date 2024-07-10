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
import cn.nukkit.utils.ServerException;

public class EntityVillager extends EntityCreature implements InventoryHolder, EntityNPC, EntityAgeable {

    public static final int NETWORK_ID = 115;
    private TradeInventory inventory;
    private List<TradeInventoryRecipe> recipes;

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
        this.recipes = new ArrayList<TradeInventoryRecipe>();
        this.dataProperties.putLong(DATA_TRADING_PLAYER_EID, 0L);
        if(this.namedTag.contains("Offers")) {
            ListTag<CompoundTag> nbtRecipes = getListRecipes();
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
    
    public void setTraderName(String name) {
        this.namedTag.putString("TraderName", name);
    }
    
    public String getTraderName() {
        if(!this.namedTag.contains("TraderName")) {
            this.namedTag.putString("TraderName", getNameTag());
        }
        return this.namedTag.getString("TraderName");
    }
    
    public void setTradeTier(int tier) {
    	if(tier > 4){
    	    throw new ServerException("Maximal tier is 4, but your tier is higher than 4");
    	}
        this.namedTag.putInt("TradeTier", tier);
        this.dataProperties.putInt(DATA_TRADE_TIER, tier);
    }
    
    public void setMaxTradeTier(int maxTier) {
    	if(maxTier > 4) maxTier = 4;
        this.dataProperties.putInt(DATA_MAX_TRADE_TIER, maxTier);
    }
    
    public int getMaxTradeTier() {
        if(!this.dataProperties.exists(DATA_MAX_TRADE_TIER)) {
            this.setMaxTradeTier(4);
        }
        return this.dataProperties.getInt(DATA_MAX_TRADE_TIER);
    }
    
    public void setExperience(int experience) {
        this.dataProperties.putInt(DATA_TRADE_EXPERIENCE, experience);
    }
    
    public int getExperience() {
        if(!this.dataProperties.exists(DATA_TRADE_EXPERIENCE)) {
            setExperience(0);
        }
        return this.dataProperties.getInt(DATA_TRADE_EXPERIENCE);
    }
    
    public int getTradeTier() {
        if(!this.namedTag.contains("TradeTier")) {
            this.namedTag.putInt("TradeTier", 0);
        }
        return this.namedTag.getInt("TradeTier");
    }
    
    public void setWilling(boolean value) {
        this.namedTag.putBoolean("Willing", value);
    }
    
    public boolean isWilling() {
        if(!this.namedTag.contains("Willing")) {
            this.namedTag.putBoolean("Willing", true);
        }
        return this.namedTag.getBoolean("Willing");
    }
    
    public void cancelTradingWithPlayer() {
    	this.setTradingWith(0L);
    }
    
    public void setTradingWith(long eid) {
        this.dataProperties.putLong(DATA_TRADING_PLAYER_EID, eid);
    }
    
    public boolean isTrading() {
        return this.dataProperties.getLong(DATA_TRADING_PLAYER_EID) != 0L;
    }
    
    @Override
    public boolean onInteract(Player player, Item item) {
        if(this.namedTag.contains("Offers") && !isTrading()) {
            player.addWindow(this.getInventory());
            return true;
        }
        return false;
    }
    
    public void addTradeRecipe(TradeInventoryRecipe recipe) {
        this.recipes.add(recipe);
        getListRecipes().add(recipe.toNBT());
    }
    
    public List<TradeInventoryRecipe> getRecipes(){
        return this.recipes;
    }
    
    public ListTag<CompoundTag> getListRecipes(){
        if(!this.getOffers().contains("Recipes")) {
            this.getOffers().putList(new ListTag<CompoundTag>("Recipes"));
        }
        return this.getOffers().getList("Recipes", CompoundTag.class);
    }
    
    public CompoundTag getOffers() {
        if(!this.namedTag.contains("Offers")) {
            this.namedTag.putCompound("Offers", new CompoundTag("Offers"));
        }
        return this.namedTag.getCompound("Offers");
	}
	
	public ListTag<CompoundTag> getDefaultTierExpRequirements() {
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
